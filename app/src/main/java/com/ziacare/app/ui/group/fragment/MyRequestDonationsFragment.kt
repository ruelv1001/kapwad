package com.ziacare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ziacare.app.data.model.SampleData
import com.ziacare.app.data.repositories.billing.response.BillData
import com.ziacare.app.databinding.FragmentMyRequestDonationsBinding
import com.ziacare.app.ui.bulletin.adapter.BillAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRequestDonationsFragment : Fragment(), BillAdapter.OnClickCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentMyRequestDonationsBinding? = null
    private val binding get() = _binding!!
    private var adapter: BillAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyRequestDonationsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchLinearLayout.isGone = true
        setupAdapter()
    }

    private fun setupAdapter() = binding.run {
        adapter = BillAdapter(requireContext(), this@MyRequestDonationsFragment)
        swipeRefreshLayout.setOnRefreshListener(this@MyRequestDonationsFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: BillData) {
    }

    override fun onRefresh() {

    }

    companion object {
        fun newInstance() = MyRequestDonationsFragment()
    }
}