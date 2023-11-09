package com.lionscare.app.ui.bulletin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.FragmentAskedForDonationBinding
import com.lionscare.app.ui.bulletin.adapter.BillAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskedForDonationFragment : Fragment(), BillAdapter.OnClickCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentAskedForDonationBinding? = null
    private val binding get() = _binding!!
    private var adapter: BillAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: SampleData) {
    }

    override fun onRefresh() {

    }

    companion object {
        fun newInstance() = AskedForDonationFragment()
    }
}