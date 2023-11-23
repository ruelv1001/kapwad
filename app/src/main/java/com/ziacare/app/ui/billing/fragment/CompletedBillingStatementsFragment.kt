package com.ziacare.app.ui.billing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ziacare.app.data.model.SampleData
import com.ziacare.app.data.repositories.billing.response.BillData
import com.ziacare.app.databinding.FragmentCompletedBillingStatementsBinding
import com.ziacare.app.ui.billing.activity.BillingActivity
import com.ziacare.app.ui.billing.viewmodel.BillingViewModel
import com.ziacare.app.ui.billing.viewstate.BillingViewState
import com.ziacare.app.ui.bulletin.adapter.BillAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompletedBillingStatementsFragment() : Fragment(), BillAdapter.OnClickCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentCompletedBillingStatementsBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : BillAdapter? = null
    private val viewModel: BillingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCompletedBillingStatementsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOngoingStatements()
        setOnClickListeners()
        setupAdapter()
    }

    private fun setOnClickListeners() = binding.run {
    }
    override fun onItemClicked(data: BillData) {
        val intent = BillingActivity.getIntent(requireActivity(), data.code.toString())
        startActivity(intent)
    }
    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            is BillingViewState.LoadingMyBills -> binding.swipeRefreshLayout.isRefreshing = true
            is BillingViewState.SuccessMyListOfBills -> {
                showList(viewState.pagingData)
            }
            else -> Unit
        }
    }

    private fun showList(sampleData: PagingData<BillData>){
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, sampleData)
    }

    private fun setupAdapter() = binding.run {
        adapter = BillAdapter(requireContext(), this@CompletedBillingStatementsFragment)
        swipeRefreshLayout.setOnRefreshListener(this@CompletedBillingStatementsFragment)
        linearLayoutManager = LinearLayoutManager(context)
        completedBillingRecyclerView.layoutManager = linearLayoutManager
        completedBillingRecyclerView.adapter = adapter

        adapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    placeHolderTextView.isVisible = false
                    skeletonLayout.isVisible = true
                    completedBillingRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.Error -> {
                    placeHolderTextView.isVisible = true
                    skeletonLayout.isVisible = false
                    completedBillingRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.NotLoading && adapter?.hasData() == true -> {
                    placeHolderTextView.isVisible = false
                    skeletonLayout.isVisible = false
                    skeletonLayout.stopShimmer()
                    completedBillingRecyclerView.isVisible = true
                }
            }
        }
    }

    private fun observeOngoingStatements() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.billingSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireContext() }
        _binding = null
    }

    override fun onRefresh() {
        viewModel.refreshMyCompletedBills()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    companion object {
        fun newInstance() = CompletedBillingStatementsFragment()
    }
}