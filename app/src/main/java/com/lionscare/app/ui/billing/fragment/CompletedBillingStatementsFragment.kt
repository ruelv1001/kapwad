package com.lionscare.app.ui.billing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.billing.response.BillData
import com.lionscare.app.databinding.FragmentCompletedBillingStatementsBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.ui.bulletin.adapter.BillAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompletedBillingStatementsFragment() : Fragment(), BillAdapter.OnClickCallback {
    private var _binding: FragmentCompletedBillingStatementsBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : BillAdapter? = null
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
        setContentViews()
    }

    private fun setOnClickListeners() = binding.run {
    }
    override fun onItemClicked(data: BillData) {
        val intent = BillingActivity.getIntent(requireActivity())
        startActivity(intent)
    }
    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            else -> Unit
        }
    }

    private fun setContentViews(){  //TODO
        //TODO change api
        val sampleData = mutableListOf<SampleData>()
        sampleData.add(
            SampleData(
                title = "B-0000004",
                amount = "30,000.00",
                remarks = "Cancelled",
                date ="11/23/2023",
                id = 1,
                name = "Von Denuelle Tandoc"
            )
        )
        sampleData.add(
            SampleData(
                title = "B-0000043",
                amount = "30,000.00",
                remarks = "Completed",
                date ="11/23/2023",
                id = 2,
                name = "Von Denuelle Tandoc"
            )
        )
        sampleData.add(
            SampleData(
                title = "B-00005354",
                amount = "30,000.00",
                remarks = "Completed",
                date ="11/23/2023",
                id = 3,
                name = "Von Denuelle Tandoc"
            )
        )
        val samplePagingData: PagingData<SampleData> = PagingData.from(sampleData)
       // showList(samplePagingData)
    }

    //TODO
    private fun showList(sampleData: PagingData<BillData>){
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, sampleData)
    }

    private fun setupAdapter() = binding.run {
        adapter = BillAdapter(requireContext(), this@CompletedBillingStatementsFragment)
        swipeRefreshLayout.setOnRefreshListener { swipeRefreshLayout.isRefreshing = false }
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
//                viewModel.memberSharedFlow.collectLatest { viewState ->
//                    handleViewState(viewState)
//                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireContext() }
        _binding = null
    }

    companion object {
        fun newInstance() = CompletedBillingStatementsFragment()
    }
}