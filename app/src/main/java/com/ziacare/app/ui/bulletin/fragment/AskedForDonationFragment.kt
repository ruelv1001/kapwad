package com.ziacare.app.ui.bulletin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ziacare.app.data.model.SampleData
import com.ziacare.app.data.repositories.billing.response.BillData
import com.ziacare.app.databinding.FragmentAskedForDonationBinding
import com.ziacare.app.ui.bulletin.adapter.BillAdapter
import com.ziacare.app.ui.bulletin.viewmodel.BillViewModel
import com.ziacare.app.ui.bulletin.viewmodel.BillViewState
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AskedForDonationFragment : Fragment(), BillAdapter.OnClickCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentAskedForDonationBinding? = null
    private val binding get() = _binding!!
    private var adapter: BillAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: BillViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAskedForDonationBinding.inflate(
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
        adapter = BillAdapter(requireContext(), this@AskedForDonationFragment)
        swipeRefreshLayout.setOnRefreshListener(this@AskedForDonationFragment)
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
            is BillViewState.SuccessGetAskForDonationList -> {
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

    private fun showList(donationListData: PagingData<BillData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, donationListData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    override fun onItemClicked(data: BillData) {
    }

    override fun onRefresh() {
        viewModel.refreshDonations()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    companion object {
        fun newInstance() = AskedForDonationFragment()
    }
}