package com.lionscare.app.ui.billing.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.FragmentGroupsYourGroupBinding
import com.lionscare.app.ui.billing.viewmodel.BillingViewModel
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.loadGroupAvatar
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AskForDonationsGroupRequestFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback {
    private var _binding: FragmentGroupsYourGroupBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var orgAdapter: GroupsYourGroupAdapter? = null

    private val viewModel: BillingViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupsYourGroupBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGroupList()
        observeImmediateFamily()
        setOnClickListeners()
        setupAdapter()
        setContentViews()

        viewModel.loadGroups()
        viewModel.getImmediateFamily()
    }

    private fun handleViewState(viewState: GroupListViewState) {
        when (viewState) {
            is GroupListViewState.Loading -> binding.orgSwipeRefreshLayout.isRefreshing = true
            is GroupListViewState.Success -> {
                showGroup(viewState.pagingData)
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

    private fun iFHandleViewState(viewState: ImmediateFamilyViewState) {
        when (viewState) {
            ImmediateFamilyViewState.Loading -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isVisible = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }

            is ImmediateFamilyViewState.PopupError -> {
                binding.createGroupButton.isVisible = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }

            is ImmediateFamilyViewState.Success -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true

                viewState.immediateFamilyResponse?.data?.let { setImmediateFamily(it) }
                viewModel.immediateFamilyId = viewState.immediateFamilyResponse?.data?.id.toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setContentViews() = binding.run {
        //Only show this views on this fragment
        sendRequestButton.isVisible = true
        noteSendRequestText.isVisible = true
        searchLinearLayout.isVisible = true

        noteSendRequestText.text =
            "Note: This Billing ${viewModel.billingStatementNumber} " +
                    "will be posted in the Request for Donations section " +
                    "of your selected groups"
    }

    private fun setOnClickListeners() = binding.run {
        sendRequestButton.setOnSingleClickListener {
            //get groups data where it is checked
            val groupsWithChecked = orgAdapter?.getCustomData()
                ?.filter{ it.isChecked }
            CommonLogger.instance.sysLogI("Custom Group List", groupsWithChecked)
            //TODO
        }

    }

    override fun onItemClicked(data: GroupListData) {
        //TODO
    }
    override fun onRemoveButtonClicked(data: GroupListData) {
        TODO("Not yet implemented")
    }
    private fun setupAdapter() = binding.run {
        orgAdapter = GroupsYourGroupAdapter(
            requireActivity(),
            this@AskForDonationsGroupRequestFragment,
            shouldShowCheckbox = !viewModel.shouldShowRemoveButton,
            shouldShowRemoveTextButton = viewModel.shouldShowRemoveButton
        )
        orgSwipeRefreshLayout.setOnRefreshListener { orgSwipeRefreshLayout.isRefreshing = false }
        linearLayoutManager = LinearLayoutManager(context)
        organizationRecyclerView.layoutManager = linearLayoutManager
        organizationRecyclerView.adapter = orgAdapter

        orgAdapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    orgPlaceHolderTextView.isVisible = false
                    orgShimmerLayout.isVisible = true
                    organizationRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.Error -> {
                    orgPlaceHolderTextView.isVisible = true
                    orgShimmerLayout.isVisible = false
                    organizationRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.NotLoading && orgAdapter?.hasData() == true -> {
                    orgPlaceHolderTextView.isVisible = false
                    orgShimmerLayout.isVisible = false
                    orgShimmerLayout.stopShimmer()
                    organizationRecyclerView.isVisible = true
                }
            }
        }
    }


    private fun observeGroupList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.getGroupSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun observeImmediateFamily() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.immediateFamilySharedFlow.collectLatest { viewState ->
                iFHandleViewState(viewState)
            }
        }
    }

    private fun setImmediateFamily(data: GroupListData) = binding.run {
        immediateFamilyLayout.imageView.loadGroupAvatar(data.avatar?.thumb_path)
        immediateFamilyLayout.titleTextView.text = data.name
        immediateFamilyLayout.membersTextView.text = if ((data.member_count ?: 0) > 1) {
            "${data.member_count} members"
        } else {
            "${data.member_count} member"
        }
        immediateFamilyLayout.referenceTextView.text = data.qrcode
    }

    private fun showGroup(groupListData: PagingData<GroupListData>) {
        binding.orgSwipeRefreshLayout.isRefreshing = false
        orgAdapter?.submitData(viewLifecycleOwner.lifecycle, groupListData)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.sendRequestButton.isVisible = false
        binding.noteSendRequestText.isVisible = false
        binding.searchLinearLayout.isVisible = false
        orgAdapter?.removeLoadStateListener { requireContext() }
        _binding = null
    }


}