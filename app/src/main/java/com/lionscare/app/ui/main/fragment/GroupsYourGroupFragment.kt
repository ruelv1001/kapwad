package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.databinding.FragmentGroupsYourGroupBinding
import com.lionscare.app.ui.main.viewmodel.GroupListViewModel
import com.lionscare.app.ui.group.activity.GroupDetailsActivity
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter
import com.lionscare.app.ui.main.adapter.ImmediateFamilyAdapter
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewModel
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupsYourGroupFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener, ImmediateFamilyAdapter.ImmediateFamilyCallback {

    private var _binding: FragmentGroupsYourGroupBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var linearLayoutManager2: LinearLayoutManager? = null
    private var iFAdapter: ImmediateFamilyAdapter? = null
    private var orgAdapter: GroupsYourGroupAdapter? = null
    private val viewModel: GroupListViewModel by viewModels()
    private val iFViewModel: ImmediateFamilyViewModel by viewModels()

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
        setClickListeners()
        setupAdapter()
        observeGroupList()
        observeImmediateFamily()
        viewModel.refreshAll()
        iFViewModel.getImmediateFamily()
    }

    private fun setupAdapter() = binding.run {
        iFAdapter = ImmediateFamilyAdapter(requireActivity(), this@GroupsYourGroupFragment)
        iFSwipeRefreshLayout.setOnRefreshListener(this@GroupsYourGroupFragment)
        linearLayoutManager = LinearLayoutManager(context)
        immediateFamilyGroupRecyclerView.layoutManager = linearLayoutManager
        immediateFamilyGroupRecyclerView.adapter = iFAdapter

        orgAdapter = GroupsYourGroupAdapter(requireActivity(), this@GroupsYourGroupFragment)
        orgSwipeRefreshLayout.setOnRefreshListener(this@GroupsYourGroupFragment)
        linearLayoutManager2 = LinearLayoutManager(context)
        organizationRecyclerView.layoutManager = linearLayoutManager2
        organizationRecyclerView.adapter = orgAdapter

    }

    private fun observeGroupList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGroupSharedFlow.collectLatest {viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun observeImmediateFamily(){
        viewLifecycleOwner.lifecycleScope.launch {
            iFViewModel.immediateFamilySharedFlow.collectLatest {viewState ->
                iFHandleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupListViewState) {
        when (viewState) {
            is GroupListViewState.Loading -> binding.orgSwipeRefreshLayout.isRefreshing = true
            is GroupListViewState.Success -> showGroup(viewState.pagingData)
            is GroupListViewState.PopupError -> {
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }
        }
    }

    private fun iFHandleViewState(viewState: ImmediateFamilyViewState) {
        when (viewState) {
            ImmediateFamilyViewState.Loading ->  binding.iFSwipeRefreshLayout.isRefreshing = true
            is ImmediateFamilyViewState.PopupError -> {
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }
            is ImmediateFamilyViewState.Success -> {
                binding.iFSwipeRefreshLayout.isRefreshing = false
                val family = viewState.immediateFamilyResponse?.data
                val familyList: List<GroupListData> = family?.let { listOf(it) } ?: emptyList()
                iFAdapter?.clear()
                iFAdapter?.appendData(familyList)
                if(familyList.isEmpty()){
                    binding.placeHolderTextView.isVisible = true
                    binding.immediateFamilyGroupRecyclerView.isGone = true
                }else{
                    binding.placeHolderTextView.isGone = true
                    binding.immediateFamilyGroupRecyclerView.isVisible = true
                }
            }
        }
    }

    private fun showGroup(groupListData: PagingData<GroupListData>) {
        binding.orgSwipeRefreshLayout.isRefreshing = false
        orgAdapter?.submitData(viewLifecycleOwner.lifecycle, groupListData)
    }

    private fun setClickListeners() = binding.run {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = GroupsYourGroupFragment()
    }

    override fun onRefresh() {
        binding.orgSwipeRefreshLayout.isRefreshing = true
        binding.iFSwipeRefreshLayout.isRefreshing = true
        viewModel.refreshAll()
        iFViewModel.getImmediateFamily()
    }

    override fun onItemClicked(data: GroupListData) {
        val intent = GroupDetailsActivity.getIntent(requireActivity())
        startActivity(intent)
    }

    override fun onImmediateFamilyItemClicked(data: GroupListData) {

    }


}