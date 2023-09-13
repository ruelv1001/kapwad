package com.lionscare.app.ui.group.fragment

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
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.FragmentGroupMembershipReqBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.GroupMembersAdapter
import com.lionscare.app.ui.group.dialog.RemoveConfirmationDialog
import com.lionscare.app.ui.group.viewmodel.AdminViewModel
import com.lionscare.app.ui.group.viewmodel.AdminViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
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

    private fun setupAdapter(value : Boolean = false) = binding.run {
        adapter = GroupMembersAdapter(requireContext(),this@AdminListFragment, viewModel.user.id,value)
        swipeRefreshLayout.setOnRefreshListener(this@AdminListFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

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

        adapter?.addLoadStateListener {
            if(adapter?.hasData() == true){
                binding.placeHolderTextView.isVisible = false
                binding.recyclerView.isVisible = true
            }else{
                binding.placeHolderTextView.isVisible = true
                binding.recyclerView.isVisible = false
            }
        }
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
                Toast.makeText(requireActivity(),viewState.message,Toast.LENGTH_SHORT).show()
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

    override fun onRemoveClicked(data: MemberListData) {
//        TODO("Not yet implemented")
    }
}