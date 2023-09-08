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
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.FragmentGroupMembershipReqBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.GroupMembersAdapter
import com.lionscare.app.ui.group.dialog.MemberDetailsDialog
import com.lionscare.app.ui.group.dialog.RemoveConfirmationDialog
import com.lionscare.app.ui.group.viewmodel.AdminViewModel
import com.lionscare.app.ui.group.viewmodel.AdminViewState
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MemberListFragment : Fragment(),
    GroupMembersAdapter.MembersCallback, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentGroupMembershipReqBinding? = null
    private val binding get() = _binding!!
    private var adapter: GroupMembersAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: MemberViewModel by viewModels()
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModelAdmin: AdminViewModel by viewModels()

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
        viewModel.refreshListOfMembers(activity.groupDetails?.id.toString(), true)
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupMembersAdapter(this@MemberListFragment, viewModel.user.id)
        swipeRefreshLayout.setOnRefreshListener(this@MemberListFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        ownerLinearLayout.isGone = true

        adapter?.addLoadStateListener {
            if (adapter?.hasData() == true) {
                placeHolderTextView.isVisible = false
                recyclerView.isVisible = true
            } else {
                placeHolderTextView.isVisible = true
                recyclerView.isVisible = false
            }
        }
    }

    private fun setClickListeners() = binding.run {

    }

    private fun observeMemberList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModelAdmin.adminSharedFlow.collectLatest { viewState ->
                handleViewStateAdmin(viewState)
            }
        }
    }

    private fun handleViewStateAdmin(viewState: AdminViewState) {
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
            is AdminViewState.SuccessRemoveMember -> {
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_SHORT).show()
                setupAdapter()
                onRefresh()
            }

            else -> Unit
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

            is MemberViewState.SuccessGetListOfMembers -> showList(viewState.pagingData)
            else -> Unit
        }
    }
    private fun showList(memberListData: PagingData<MemberListData>){
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, memberListData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: MemberListData) {
        callMemberDetailDialog(data)
    }

    override fun onRemoveClicked(data: MemberListData) = Unit

    private fun callMemberDetailDialog(data: MemberListData) {
        MemberDetailsDialog.newInstance(
            object : MemberDetailsDialog.MembershipCallback {
                override fun onRemoveMember(memberListData: MemberListData) {
                    callRemoveMemberDialog(memberListData.id ?: 0)
                }

                override fun onSendPoint(memberListData: MemberListData) {
                    val intent = WalletActivity.getIntent(
                        requireActivity(),
                        "Send Points",
                        true,
                        activity.groupDetails?.id.orEmpty(),
                        QRData(
                            data.user?.id,
                            data.user?.name,
                        ),
                        "START_INPUT"
                    )
                    startActivity(intent)
                }
            }, data
        ).show(childFragmentManager, MemberDetailsDialog.TAG)
    }

    private fun callRemoveMemberDialog(id: Int) {
        RemoveConfirmationDialog.newInstance(
            object : RemoveConfirmationDialog.ConfirmationCallback {
                override fun onConfirm(id: String) {
                    viewModelAdmin.doRemoveMember(activity.groupDetails?.id.orEmpty(), id.toInt())
                }
            },
            title = "Remove Selected Member?",
            groupId = id.toString()
        ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
    }

    override fun onRefresh() {
        viewModel.refreshListOfMembers(activity.groupDetails?.id.toString(), true)
    }

    companion object {
        private const val REQUEST = "REQUEST"
        fun newInstance(): MemberListFragment {
            return MemberListFragment()
        }
    }

}