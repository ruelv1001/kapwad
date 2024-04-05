package dswd.ziacare.app.ui.main.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.group.response.PendingGroupRequestData
import dswd.ziacare.app.databinding.FragmentGroupsPendingRequestsBinding
import dswd.ziacare.app.ui.main.activity.MainActivity
import dswd.ziacare.app.ui.main.adapter.GroupsPendingRequestsAdapter
import dswd.ziacare.app.ui.main.viewmodel.GroupListViewModel
import dswd.ziacare.app.ui.main.viewmodel.GroupListViewState
import dswd.ziacare.app.utils.dialog.CommonDialog
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupsPendingRequestsFragment : Fragment(), GroupsPendingRequestsAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentGroupsPendingRequestsBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupsPendingRequestsAdapter? = null
    private val viewModel: GroupListViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null
    private val activity by lazy { requireActivity() as MainActivity }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupsPendingRequestsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setupAdapter()
        observeGroupList()
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupsPendingRequestsAdapter(requireActivity(), this@GroupsPendingRequestsFragment)
        swipeRefreshLayout.setOnRefreshListener(this@GroupsPendingRequestsFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    placeHolderTextView.isVisible = false
                    shimmerLayout.isVisible = true
                    recyclerView.isVisible = false
                }
                loadState.source.refresh is LoadState.Error -> {
                    placeHolderTextView.isVisible = true
                    shimmerLayout.isVisible = false
                    recyclerView.isVisible = false
                }
                loadState.source.refresh is LoadState.NotLoading && adapter?.hasData() == true -> {
                    placeHolderTextView.isVisible = false
                    shimmerLayout.isVisible = false
                    shimmerLayout.stopShimmer()
                    recyclerView.isVisible = true

                    //gets how many group pending request is of type (self request on group)
                    viewModel.getSelfGroupRequestCount = adapter?.getSelfGroupRequestCount()

                }
            }
        }
    }

    private fun observeGroupList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGroupSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupListViewState) {
        when (viewState) {
            is GroupListViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is GroupListViewState.LoadingAcceptDeclineInvitation -> showLoadingDialog(R.string.loading)
            is GroupListViewState.SuccessGetPendingRequestList -> {
                binding.swipeRefreshLayout.isRefreshing = false
                clearList()
                showPending(viewState.pagingData)
            }
            is GroupListViewState.SuccessAcceptDeclineInvitation -> {
                viewModel.refreshPendingRequestList()
                requireActivity().toastSuccess(viewState.msg, CpmToast.SHORT_DURATION)
                findNavController().popBackStack()
            }
            is GroupListViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> Unit
        }
    }

    private fun showPending(pendingListData: PagingData<PendingGroupRequestData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, pendingListData)
    }

    private fun setClickListeners() = binding.run {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance() = GroupsPendingRequestsFragment()
    }

    override fun onItemClicked(data: PendingGroupRequestData) {

    }

    override fun onAcceptClicked(data: PendingGroupRequestData) {
        if(viewModel.getUserKYC() != "completed"){
            if (activity.groupCount < 1) {
                if((viewModel.doGetSelfGroupRequestCount() ?: 0) >= 1){ // if more than one then wait for that request first
                    requireActivity().toastWarning(getString(R.string.group_self_request_non_verified),CpmToast.LONG_DURATION)
                }else{
                    openAcceptInvitation(data)
                }
            }else{
                requireActivity().toastWarning(
                    getString(R.string.not_verified_group),
                    CpmToast.LONG_DURATION
                )
            }
        }else{
            openAcceptInvitation(data)

        }
    }

    override fun onDeclineClicked(data: PendingGroupRequestData) {
        if(viewModel.getUserKYC() != "completed"){
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified))
        }else{
            openDeclineInvitation(data)
        }
    }

    override fun onCancelClicked(data: PendingGroupRequestData) {
        openCancelJoinRequest(data)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }
    override fun onRefresh() {
        viewModel.refreshPendingRequestList()
    }

    private fun clearList() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

    private fun openAcceptInvitation(data: PendingGroupRequestData) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure your want to accept the invitation from ${data.group?.name}?")
        builder.setPositiveButton(getString(R.string.option_yes_txt)) { _, _ ->
            viewModel.doAcceptInvitation(data.id?.toLong()?: 0, data.group_id.orEmpty())
        }
        builder.setNegativeButton(getString(R.string.option_no_txt), null)
        builder.show()
    }

    private fun openDeclineInvitation(data: PendingGroupRequestData) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure your want to decline the invitation from ${data.group?.name}?")
        builder.setPositiveButton(getString(R.string.option_yes_txt)) { _, _ ->
            viewModel.doDeclineInvitation(data.id?.toLong()?: 0, data.group_id.orEmpty())
        }
        builder.setNegativeButton(getString(R.string.option_no_txt), null)
        builder.show()
    }

    private fun openCancelJoinRequest(data: PendingGroupRequestData) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure your want to cancel your request to ${data.group?.name}?")
        builder.setPositiveButton(getString(R.string.option_yes_txt)) { _, _ ->
            viewModel.cancelJoinRequest(data.id.toString(), data.group_id.orEmpty())
        }
        builder.setNegativeButton(getString(R.string.option_no_txt), null)
        builder.show()
    }

    fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(childFragmentManager)
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        hideLoadingDialog()
        super.onDestroy()
    }

}