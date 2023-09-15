package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.FragmentGroupCommunityRolesUpdateBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.GroupMembersAdapter
import com.lionscare.app.ui.group.dialog.RemoveConfirmationDialog
import com.lionscare.app.ui.group.dialog.SaveSuccessDialog
import com.lionscare.app.ui.group.viewmodel.AdminViewModel
import com.lionscare.app.ui.group.viewmodel.AdminViewState
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityRolesUpdateFragment: Fragment(), GroupMembersAdapter.MembersCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentGroupCommunityRolesUpdateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private var adapter : GroupMembersAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupCommunityRolesUpdateBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setView()
        onResume()
        setupAdapter()
        observeMemberList()
        onRefresh()
    }


    private fun setupAdapter() = binding.run {
        adapter = GroupMembersAdapter(requireContext(),this@CommunityRolesUpdateFragment, isUpdating = true)
        swipeRefreshLayout.setOnRefreshListener(this@CommunityRolesUpdateFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter?.addLoadStateListener {
            recyclerView.isVisible = adapter?.hasData() == true
        }
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_group_community_roles_update))
    }

    private fun setView() = binding.run{
        groupOwnerLayout.nameTextView.text = activity.groupDetails?.owner?.name
        groupOwnerLayout.idNoTextView.text = activity.groupDetails?.owner?.qrcode
        groupOwnerLayout.profileImageView.loadAvatar(activity?.groupDetails?.owner?.avatar?.thumb_path, requireActivity())

    }

    private fun setClickListeners() = binding.run {

        addLinearLayout.setOnSingleClickListener {
            findNavController().navigate(CommunityRolesUpdateFragmentDirections.actionNavigationGroupRolesPromote())
        }
        saveButton.setOnSingleClickListener {
            SaveSuccessDialog.newInstance().show(childFragmentManager, SaveSuccessDialog.TAG)
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
                Toast.makeText(requireActivity(),viewState.message, Toast.LENGTH_SHORT).show()
                setupAdapter()
                onRefresh()
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

    override fun onItemClicked(data: MemberListData) {
//        TODO("Not yet implemented")
    }

    override fun onRemoveClicked(data: MemberListData) {
        RemoveConfirmationDialog.newInstance(
            object : RemoveConfirmationDialog.ConfirmationCallback {
                override fun onConfirm(id: String) {
                    viewModel.doDemoteAdmin(activity.groupDetails?.id!!,id.toInt())
                }
            },
            title = "Demote Selected Admin?",
            groupId = data.id.toString()
        ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
    }

    override fun onRefresh() {
        viewModel.refresh(activity.groupDetails?.id.toString())
    }

}