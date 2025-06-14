package kapwad.reader.app.ui.group.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.member.response.PendingMemberData
import kapwad.reader.app.databinding.FragmentGroupMembershipReqBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.adapter.GroupsInvitesAdapter
import kapwad.reader.app.ui.group.dialog.RequestInviteFilterDialog
import kapwad.reader.app.ui.group.viewmodel.MemberViewModel
import kapwad.reader.app.ui.group.viewmodel.MemberViewState
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MembershipRequestFragment : Fragment(), GroupsInvitesAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentGroupMembershipReqBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupsInvitesAdapter? = null
    private val viewModel: MemberViewModel by viewModels()
    private val activity by lazy { requireActivity() as GroupActivity }
    private var filterType = "all"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupMembershipReqBinding.inflate(
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
        observeAllPendingRequestList()
        viewModel.refresh(activity.groupDetails?.id.toString(), filterType)
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupsInvitesAdapter(requireActivity(), this@MembershipRequestFragment, activity.groupDetails?.is_admin)
        swipeRefreshLayout.setOnRefreshListener(this@MembershipRequestFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        ownerLinearLayout.isGone = true

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
                }
            }
        }
    }

    private fun setClickListeners() = binding.run {
        activity.getFilterImageView().setOnSingleClickListener {
            RequestInviteFilterDialog.newInstance(object :
                RequestInviteFilterDialog.RequestInviteFilterDialogListener {
                override fun onFilter(filter: String) {
                    clear()
                    viewModel.refresh(activity.groupDetails?.id.toString(), filter)
                }

                override fun onCancel() {
                    clear()
                    viewModel.refresh(activity.groupDetails?.id.toString(), filterType)
                }
            }).show(childFragmentManager, RequestInviteFilterDialog.TAG)
        }
    }

    private fun observeAllPendingRequestList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: MemberViewState) {
        when (viewState) {
            MemberViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is MemberViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is MemberViewState.SuccessGetPendingRequest -> showList(viewState.pagingData)
            is MemberViewState.SuccessRejectJoinRequest -> {
                binding.swipeRefreshLayout.isRefreshing = false
                clear()
                viewModel.refresh(activity.groupDetails?.id.toString(), filterType)
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
            }
            is MemberViewState.SuccessApproveJoinRequest -> {
                binding.swipeRefreshLayout.isRefreshing = false
                clear()
                requireActivity().toastSuccess(viewState.approveRequestResponse?.msg.toString(), CpmToast.LONG_DURATION)
            }
            else -> Unit
        }
    }

    private fun showList(pendingMemberList: PagingData<PendingMemberData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, pendingMemberList)
    }

    fun clear() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    companion object {
        private const val REQUEST = "REQUEST"
        fun newInstance(): MembershipRequestFragment {
            return MembershipRequestFragment()
        }
    }

    override fun onItemClicked(data: PendingMemberData) {
    }

    override fun onAcceptClicked(data: PendingMemberData) {
        openApproveJoinRequest(data)
    }

    override fun onDeclineClicked(data: PendingMemberData) {
        openRejectJoinRequest(data)
    }

    override fun onCancelClicked(data: PendingMemberData) {
        openCancelInvitation(data)
    }

    private fun openApproveJoinRequest(data: PendingMemberData) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure your want to approve the join request from ${data.user?.name}?")
        builder.setPositiveButton(getString(R.string.option_yes_txt)) { _, _ ->
            viewModel.approveJoinRequest(data.id.toString(), activity.groupDetails?.id.toString())
        }
        builder.setNegativeButton(getString(R.string.option_no_txt), null)
        builder.show()
    }

    private fun openRejectJoinRequest(data: PendingMemberData) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure your want to reject the join request from ${data.user?.name}?")
        builder.setPositiveButton(getString(R.string.option_yes_txt)) { _, _ ->
            viewModel.rejectJoinRequest(data.id.toString(), activity.groupDetails?.id.toString())
        }
        builder.setNegativeButton(getString(R.string.option_no_txt), null)
        builder.show()
    }

    private fun openCancelInvitation(data: PendingMemberData) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Are you sure your want to cancel your invitation to ${data.user?.name}?")
        builder.setPositiveButton(getString(R.string.option_yes_txt)) { _, _ ->
            viewModel.cancelInvitation(data.id.toString(), data.group_id.toString())
        }
        builder.setNegativeButton(getString(R.string.option_no_txt), null)
        builder.show()
    }

    override fun onRefresh() {
        clear()
        viewModel.refresh(activity.groupDetails?.id.toString(), filterType)
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }
}