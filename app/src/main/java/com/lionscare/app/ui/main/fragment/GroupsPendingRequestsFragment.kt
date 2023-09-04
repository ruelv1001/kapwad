package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestData
import com.lionscare.app.databinding.FragmentGroupsPendingRequestsBinding
import com.lionscare.app.ui.main.adapter.GroupsPendingRequestsAdapter
import com.lionscare.app.ui.main.viewmodel.GroupListViewModel
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.utils.showPopupError
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

        adapter?.addLoadStateListener {
            if(adapter?.hasData() == true){
                placeHolderTextView.isVisible = false
                recyclerView.isVisible = true
            }else{
                placeHolderTextView.isVisible = true
                recyclerView.isVisible = false
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
            is GroupListViewState.SuccessGetPendingRequestList -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showPending(viewState.pagingData)
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
        Toast.makeText(requireActivity(),"Accept Title : ${data.group?.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDeclineClicked(data: PendingGroupRequestData) {

    }

    override fun onCancelClicked(data: PendingGroupRequestData) {

    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }
    override fun onRefresh() {
        viewModel.refreshPendingRequestList()
    }

    fun clear() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

}