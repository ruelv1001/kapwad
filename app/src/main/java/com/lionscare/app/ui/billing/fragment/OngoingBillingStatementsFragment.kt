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
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.billing.response.BillData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.member.response.User
import com.lionscare.app.databinding.FragmentOngoingBillingStatementsBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.ui.bulletin.adapter.BillAdapter
import com.lionscare.app.ui.group.adapter.GroupMembersAdapter
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OngoingBillingStatementsFragment : Fragment(), BillAdapter.OnClickCallback {
    private var _binding: FragmentOngoingBillingStatementsBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : BillAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOngoingBillingStatementsBinding.inflate(
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
                title = "B-0000024",
                amount = "30,000.00",
                remarks = "Ongoing",
                date ="11/23/2023",
                id = 1,
                name = "Von Denuelle Tandoc"
            )
        )
        sampleData.add(
            SampleData(
                title = "B-0000043",
                amount = "30,000.00",
                remarks = "Ongoing",
                date ="11/23/2023",
                id = 2,
                name = "Von Denuelle Tandoc"
            )
        )
        sampleData.add(
            SampleData(
                title = "B-00005354",
                amount = "30,000.00",
                remarks = "Ongoing",
                date ="11/23/2023",
                id = 3,
                name = "Von Denuelle Tandoc"
            )
        )
        val samplePagingData: PagingData<SampleData> = PagingData.from(sampleData)
        //showList(samplePagingData)
    }

    //TODO
    private fun showList(sampleData: PagingData<BillData>){
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, sampleData)
    }

    private fun setupAdapter() = binding.run {
        adapter = BillAdapter(requireContext(), this@OngoingBillingStatementsFragment)
        swipeRefreshLayout.setOnRefreshListener { swipeRefreshLayout.isRefreshing = false }
        linearLayoutManager = LinearLayoutManager(context)
        ongoingBillingRecyclerView.layoutManager = linearLayoutManager
        ongoingBillingRecyclerView.adapter = adapter

        adapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    placeHolderTextView.isVisible = false
                    skeletonLayout.isVisible = true
                    ongoingBillingRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.Error -> {
                    placeHolderTextView.isVisible = true
                    skeletonLayout.isVisible = false
                    ongoingBillingRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.NotLoading && adapter?.hasData() == true -> {
                    placeHolderTextView.isVisible = false
                    skeletonLayout.isVisible = false
                    skeletonLayout.stopShimmer()
                    ongoingBillingRecyclerView.isVisible = true
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
        fun newInstance() = OngoingBillingStatementsFragment()
    }



}