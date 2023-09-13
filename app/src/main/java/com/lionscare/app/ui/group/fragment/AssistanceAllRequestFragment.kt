package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.databinding.FragmentGroupAssistanceBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.AssistanceAdapter
import com.lionscare.app.ui.group.dialog.FilterDialog
import com.lionscare.app.ui.group.viewmodel.AssistanceViewModel
import com.lionscare.app.ui.group.viewmodel.AssistanceViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AssistanceAllRequestFragment : Fragment(), AssistanceAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener, FilterSelectedListener {

    private var _binding: FragmentGroupAssistanceBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AssistanceAdapter? = null
    private var direction: NavDirections? = null
    private val viewModel: AssistanceViewModel by viewModels()
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupAssistanceBinding.inflate(
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
        observeAssistance()
    }

    private fun setupAdapter() = binding.run {
        adapter = AssistanceAdapter(activity,this@AssistanceAllRequestFragment)
        swipeRefreshLayout.setOnRefreshListener(this@AssistanceAllRequestFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter?.addLoadStateListener {
            if (adapter?.hasData() == true) {
                placeHolderTextView.isVisible = false
                recyclerView.isVisible = true
            } else {
                placeHolderTextView.isVisible = true
                recyclerView.isVisible = false
            }
        }
    }

    private fun setClickListeners() = binding.run {

    }

    private fun observeAssistance() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assistanceSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: AssistanceViewState) {
        when (viewState) {
            is AssistanceViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is AssistanceViewState.SuccessGetAllListOfAssistance -> {
                binding.swipeRefreshLayout.isRefreshing = false
                clearList()
                showList(viewState.pagingData)
            }
            is AssistanceViewState.PopupError -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            else -> Unit
        }
    }

    private fun showList(createAssistanceData: PagingData<CreateAssistanceData>){
        adapter?.submitData(viewLifecycleOwner.lifecycle, createAssistanceData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    override fun onItemClicked(data: CreateAssistanceData) {
        activity.referenceId = data.reference_id.toString()
        findNavController().navigate(direction!!)
    }

    private fun clearList() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

    override fun onRefresh() {
        viewModel.refresh(activity.groupDetails?.id.toString(), emptyList())
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onFilterSelected(filter: List<String>) {
        clearList()
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.refresh(activity.groupDetails?.id.toString(), filter)
    }

    companion object {
        fun newInstance(direction: NavDirections): AssistanceAllRequestFragment {
            val fragment = AssistanceAllRequestFragment()
            fragment.direction = direction
            return fragment
        }
    }
}