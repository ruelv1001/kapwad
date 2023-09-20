package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.FragmentGroupInviteBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.SearchInviteMemberAdapter
import com.lionscare.app.ui.group.dialog.InviteMemberDetailsDialog
import com.lionscare.app.ui.group.dialog.MemberDetailsDialog
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.ui.group.viewmodel.MemberViewState
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
    private var adapter: SearchInviteMemberAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: MemberViewModel by viewModels()
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

    private fun observeWallet() {
        viewLifecycleOwner.lifecycleScope.launch {
            walletViewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewStateWallet(viewState)
            }
        }
    }

    private fun handleViewStateWallet(viewState: WalletViewState) {
        when (viewState) {
            WalletViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is WalletViewState.SuccessScanQR -> {
                activity.hideLoadingDialog() //hide dialog because it messes cycle stuff
                Toast.makeText(requireContext(),
                    getString(R.string.scan_completed_info), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() //get out of qr fragment

                //get data returned from scanning QR then pass it to invite member
                viewModel.inviteMember( viewState.scanQRData?.id.toString(),activity.groupDetails?.id.toString())

            }

            is WalletViewState.SuccessSearchUser -> {
                activity.hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.listData)
                if (adapter?.getData()?.size == 0) {
                    binding.memberPlaceHolderTextView.isVisible = true
                    binding.recyclerView.isGone = true
                } else {
                    binding.memberPlaceHolderTextView.isGone = true
                    binding.recyclerView.isVisible = true
                }
            }

            else -> Unit
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
                    binding.recyclerView.isGone = true
                } else {
                    binding.memberPlaceHolderTextView.isGone = true
                    binding.recyclerView.isVisible = true
                }
            }

            is MemberViewState.SuccessInviteMember -> {
                activity.hideLoadingDialog()
                activity.onBackPressedDispatcher.onBackPressed()
                Toast.makeText(activity, viewState.data?.msg, Toast.LENGTH_LONG).show()
            }

            else -> Unit
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

    private fun setupAdapter() = binding.run {
        adapter = SearchInviteMemberAdapter(requireActivity(), this@GroupInviteFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        activity.setTitlee("Invite to ${activity.groupDetails?.name}")
    }

    private fun setView() = binding.run {
        searchEditText.doAfterTextChanged {
            applyTextView.isVisible = it.toString().isNotEmpty()
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.doSearchUser(searchEditText.text.toString())
                true
            }
            false
        }
    }

    override fun onItemClicked(data: QRData) {
        callInviteMemberDialog(data)
    }

    private fun callInviteMemberDialog(data: QRData) {
        InviteMemberDetailsDialog.newInstance(
            object : InviteMemberDetailsDialog.MembershipCallback {
                override fun onInviteMember(memberListData: QRData) {
                    viewModel.inviteMember(data.id.toString(),activity.groupDetails?.id.toString())
                }

            }, data
        ).show(childFragmentManager, MemberDetailsDialog.TAG)
    }

}