package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.FragmentGroupInviteBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.GroupMembersAdapter
import com.lionscare.app.ui.group.adapter.SearchInviteMemberAdapter
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.ui.wallet.fragment.WalletSearchFragmentDirections
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.dialog.ScannerDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class GroupInviteFragment : Fragment(), SearchInviteMemberAdapter.SearchCallback {
    private var _binding: FragmentGroupInviteBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private var adapter : SearchInviteMemberAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel : MemberViewModel by viewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupInviteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setClickListeners()
        setView()
        onResume()
        observeMember()
        observeWallet()
    }

    private fun setupAdapter() = binding.run {
        adapter = SearchInviteMemberAdapter(requireActivity(),this@GroupInviteFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        memberRecyclerView.layoutManager = linearLayoutManager
        memberRecyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = R.drawable.img_profile,
                title = "Raquel Castro",
                amount = "LC-000004",
            ),
            SampleData(
                id = R.drawable.img_profile,
                title = "Romeo Dela Cruz",
                amount = "LC-000001",
            )
        )
        //adapter?.submitData(lifecycle, PagingData.from(dataList))
        //complete button removed
//        if (activity.start == START_INVITE){
//            completeButton.text = getText(R.string.lbl_invite)
//        } else {
//            completeButton.text = getText(R.string.lbl_complete)
//        }
    }


    override fun onResume() {
        super.onResume()
        activity.setTitlee("Invite to ${activity.groupDetails?.name}")
    }

    private fun setView() = binding.run {
        searchEditText.doAfterTextChanged {
            applyTextView.isVisible = it.toString().isNotEmpty()
        searchEditText.doOnTextChanged {
                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
            if (searchEditText.text?.isNotEmpty() == true){
                recyclerView.visibility = View.VISIBLE
                closeImageView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.GONE
                closeImageView.visibility = View.GONE
            }
        }
        closeImageView.setOnSingleClickListener {
            closeImageView.visibility = View.GONE
            searchEditText.setText("")
        }
    }

    private fun setClickListeners() = binding.run {
        activity.getScanImageView().setOnSingleClickListener {
            ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
                override fun onScannerSuccess(qrValue: String) {
                    val jsonObject: JSONObject
                    val res = qrValue.replace("\\", "")
                    try {
                        jsonObject = JSONObject(res)
                        val type = jsonObject.getString("type")
                        val value = jsonObject.getString("value")
                        walletViewModel.doScanQr(value)
                    } catch (e: JSONException) {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.invalid_qr_code_msg),
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                }
            }, "Scan QR").show(childFragmentManager, ScannerDialog.TAG)
        }

        applyTextView.setOnSingleClickListener {
            viewModel.doSearchUser(searchEditText.text.toString())
        }

    }

    private fun observeMember() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: MemberViewState) {
        when (viewState) {
            MemberViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is MemberViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is MemberViewState.SuccessSearchUser -> {
                activity.hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.listData)
                if (adapter?.getData()?.size == 0) {
                    binding.memberPlaceHolderTextView.isVisible = true
                    binding.memberRecyclerView.isGone = true
                } else {
                    binding.memberPlaceHolderTextView.isGone = true
                    binding.memberRecyclerView.isVisible = true
                }
            }
            is MemberViewState.SuccessInviteMember -> {
                activity.hideLoadingDialog()
                Toast.makeText(activity,viewState.data?.msg,Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    private fun observeWallet() {
        viewLifecycleOwner.lifecycleScope.launch{
            walletViewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: WalletViewState) {
        when (viewState) {
            is WalletViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.LoadingScanGroup -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.SuccessScanQR -> {
                activity.hideLoadingDialog()
                viewModel.inviteMember(viewState.scanQRData?.id.toString(),activity.groupDetails?.id.toString())
            }
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val START_INVITE = "START_INVITE"
    }

    override fun onItemClicked(data: MemberListData) {

    }

    override fun onRemoveClicked(data: MemberListData) {
//        TODO("Not yet implemented")
    override fun onItemClicked(data: QRData) {
        viewModel.inviteMember(data.id.toString(),activity.groupDetails?.id.toString())
    }
}