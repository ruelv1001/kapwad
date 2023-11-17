package com.lionscare.app.ui.bulletin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.billing.response.BillData
import com.lionscare.app.databinding.FragmentPublicRequestBinding
import com.lionscare.app.ui.bulletin.adapter.BillAdapter
import com.lionscare.app.ui.bulletin.viewmodel.BillViewModel
import com.lionscare.app.ui.bulletin.viewmodel.BillViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PublicRequestFragment : Fragment(), BillAdapter.OnClickCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentPublicRequestBinding? = null
    private val binding get() = _binding!!
    private var adapter: BillAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: BillViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPublicRequestBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        observeBill()
    }

    private fun setupAdapter() = binding.run {
        adapter = BillAdapter(requireContext(), this@PublicRequestFragment)
        swipeRefreshLayout.setOnRefreshListener(this@PublicRequestFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        shimmerLayout.isVisible = true
        shimmerLayout.startShimmer()

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

    private fun observeBill() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getBillSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: BillViewState) {
        when (viewState) {
            is BillViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is BillViewState.SuccessGetAllBillList -> {
                showList(viewState.pagingData)
            }
            is BillViewState.PopupError -> {
                binding.swipeRefreshLayout.isRefreshing = false
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

    private fun showList(billListData: PagingData<BillData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, billListData)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    override fun onItemClicked(data: BillData) {
    }

    override fun onRefresh() {
        viewModel.refreshBills()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    companion object {
        fun newInstance() = PublicRequestFragment()
    }
}