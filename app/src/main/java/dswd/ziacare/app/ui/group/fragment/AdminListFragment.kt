package dswd.ziacare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
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
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.member.response.MemberListData
import dswd.ziacare.app.databinding.FragmentGroupMembershipReqBinding
import dswd.ziacare.app.ui.group.activity.GroupActivity
import dswd.ziacare.app.ui.group.adapter.GroupMembersAdapter
import dswd.ziacare.app.ui.group.dialog.RemoveConfirmationDialog
import dswd.ziacare.app.ui.group.viewmodel.AdminViewModel
import dswd.ziacare.app.ui.group.viewmodel.AdminViewState
import dswd.ziacare.app.utils.CommonLogger
import dswd.ziacare.app.utils.loadAvatar
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminListFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener, GroupMembersAdapter.MembersCallback {

    private var _binding: FragmentGroupMembershipReqBinding? = null
    private val binding get() = _binding!!
    private var adapter : GroupMembersAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: AdminViewModel by viewModels()
    private val activity by lazy { requireActivity() as GroupActivity }

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
        observeMemberList()
        setOwner()
        onRefresh()
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupMembersAdapter(requireContext(),this@AdminListFragment, viewModel.user.id, isInMemberList = false)
        swipeRefreshLayout.setOnRefreshListener(this@AdminListFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
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
                }
            }
        }
    }

    private fun setClickListeners() = binding.run {
       activity.getRolesView().setOnSingleClickListener {
           findNavController().navigate(AdminListFragmentDirections.actionNavigationGroupRolesUpdate())
       }
    }

    private fun setOwner() = binding.run{
        ownerLinearLayout.isVisible = true
        groupOwnerLayout.nameTextView.text = activity.groupDetails?.owner?.name
        groupOwnerLayout.idNoTextView.text = activity.groupDetails?.owner?.qrcode
        groupOwnerLayout.profileImageView.loadAvatar(activity.groupDetails?.owner?.avatar?.thumb_path,requireActivity())

        groupOwnerLayout.youTextView.isVisible = viewModel.user.id == activity.groupDetails?.owner_user_id
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_group_community_roles))
        activity.getRolesView().isVisible = activity.groupDetails?.is_admin == true
    }

    private fun observeMemberList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.adminSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: AdminViewState) {
        when (viewState) {
            AdminViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is AdminViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is AdminViewState.SuccessGetListOfAdmin -> showList(viewState.pagingData)
            is AdminViewState.SuccessDemoteAdmin -> {
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
            }
            else -> Unit
        }
    }

    private fun showList(memberListData: PagingData<MemberListData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, memberListData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        viewModel.refresh(activity.groupDetails?.id.toString())
    }

    override fun onItemClicked(data: MemberListData) {
//        TODO("Not yet implemented")
    }

}