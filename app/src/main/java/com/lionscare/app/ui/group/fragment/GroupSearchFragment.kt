package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import com.lionscare.app.R
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.FragmentGroupSearchBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.dialog.JoinGroupConfirmationDialog
import com.lionscare.app.ui.group.viewmodel.GroupViewModel
import com.lionscare.app.ui.group.viewmodel.GroupViewState
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.ui.main.adapter.GroupsGroupAdapter
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.dialog.ScannerDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class GroupSearchFragment : Fragment(), GroupsGroupAdapter.GroupCallback {

    private var _binding: FragmentGroupSearchBinding? = null
    private val binding get() = _binding!!
    private var groupLinearLayoutManager: LinearLayoutManager? = null
    private var groupsAdapter : GroupsGroupAdapter? = null
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: GroupViewModel by viewModels()
    private val memberViewModel : MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGroupAdapter()
        setupClickListener()
        observeMember()
        observeGroup()
    }

    override fun onResume() {
        super.onResume()
        viewModel.doGetGroupListCount()
        activity.setTitlee(getString(R.string.lbl_group_search))
    }

    private fun setupGroupAdapter() = binding.run {
        groupsAdapter = GroupsGroupAdapter(requireActivity(), this@GroupSearchFragment)
        groupLinearLayoutManager = LinearLayoutManager(requireActivity())
        groupRecyclerView.layoutManager = groupLinearLayoutManager
        groupRecyclerView.adapter = groupsAdapter
    }

    private fun setupClickListener() = binding.run{

        searchEditText.doAfterTextChanged {
            applyTextView.isVisible = it.toString().isNotEmpty()
        }

        activity.getScanImageView().setOnSingleClickListener {
            ScannerDialog.newInstance( object : ScannerDialog.ScannerListener{
                override fun onScannerSuccess(qrValue: String) {
                    val jsonObject: JSONObject
                    val res = qrValue.replace("\\", "")
                    try {
                        jsonObject = JSONObject(res)
                        val type = jsonObject.getString("type")
                        val value = jsonObject.getString("value")

                        viewModel.doSearchGroupWithLoading(value)

                    } catch (e: JSONException) {
                        requireActivity().toastError(getString(R.string.invalid_qr_code_msg), CpmToast.LONG_DURATION)
                        e.printStackTrace()
                    }
                }
            }, "Scan QR")
                .show(childFragmentManager, ScannerDialog.TAG)
        }

        applyTextView.setOnSingleClickListener {
          //  viewModel.doSearchUser(searchEditText.text.toString())
            viewModel.doSearchGroupWithLoading(searchEditText.text.toString())
        }
    }

    private fun observeGroup() {
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.groupSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }

        }
    }

    private fun handleViewState(viewState: GroupViewState) {
        when (viewState) {
            is GroupViewState.Loading -> activity.showLoadingDialog(R.string.loading)
           /* is GroupViewState.SuccessScanQR -> {
               *//* activity.hideLoadingDialog()
                activity.qrData = viewState.scanQRData?: QRData()*//*
            }
            is GroupViewState.SuccessSearchUser -> {
                activity.hideLoadingDialog()
             *//*   adapter?.clear()
                adapter?.appendData(viewState.listData)
                if (adapter?.getData()?.size == 0) {
                    binding.placeHolderTextView.isVisible = true
                    binding.recyclerView.isGone = true
                } else {
                    binding.placeHolderTextView.isGone = true
                    binding.recyclerView.isVisible = true
                }*//*
            }*/
            is GroupViewState.SuccessPendingGroupListCount -> {
                activity.hideLoadingDialog()
                for (i in 0 until viewState.pendingGroupRequestsListResponse.data?.size!!) {
                    if(viewState.pendingGroupRequestsListResponse.data!![i].type == "request"){
                        viewModel.curretGroupCount++
                    }
                }
            }
            is GroupViewState.SuccessSearchGroup -> {
                activity.hideLoadingDialog()
                groupsAdapter?.clear()
                groupsAdapter?.appendData(viewState.listData)
                if (groupsAdapter?.getData()?.size == 0) {
                    binding.groupPlaceHolderTextView.isVisible = true
                    binding.groupRecyclerView.isGone = true
                } else {
                    binding.groupPlaceHolderTextView.isGone = true
                    binding.groupRecyclerView.isVisible = true
                }
            }
            is GroupViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun observeMember() {
        viewLifecycleOwner.lifecycleScope.launch {
            memberViewModel.memberSharedFlow.collectLatest { viewState ->
                memberHandleViewState(viewState)
            }
        }
    }

    private fun memberHandleViewState(viewState: MemberViewState) {
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
            is MemberViewState.SuccessJoinGroup -> {
                activity.hideLoadingDialog()
                requireActivity().toastSuccess(viewState.data?.msg.toString(), CpmToast.LONG_DURATION)
                activity.finish()
            }
            else -> Unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: GroupData) {
    }

    override fun onJoinClicked(data: GroupData) {
        if(viewModel.getUserKYC() != "completed"){
            if (viewModel.curretGroupCount >= 1){
                requireActivity().toastWarning(getString(R.string.group_self_request_non_verified),CpmToast.LONG_DURATION)
            }else{
                JoinGroupConfirmationDialog.newInstance(object : JoinGroupConfirmationDialog.ConfirmationCallback{
                    override fun onConfirm(group_id: String, privacy: String, passcode: String) {
                        memberViewModel.joinGroup(group_id, passcode)
                    }
                }, "Do you want to join ${data.name}?", data).show(childFragmentManager, JoinGroupConfirmationDialog.TAG)
            }
        }else{
            JoinGroupConfirmationDialog.newInstance(object : JoinGroupConfirmationDialog.ConfirmationCallback{
                override fun onConfirm(group_id: String, privacy: String, passcode: String) {
                    memberViewModel.joinGroup(group_id, passcode)
                }
            }, "Do you want to join ${data.name}?", data).show(childFragmentManager, JoinGroupConfirmationDialog.TAG)
        }


    }
}