package da.farmer.app.ui.group.fragment

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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import da.farmer.app.data.repositories.assistance.response.CreateAssistanceData
import da.farmer.app.databinding.FragmentGroupAssistanceBinding
import da.farmer.app.ui.group.activity.GroupActivity
import da.farmer.app.ui.group.adapter.AssistanceAdapter
import da.farmer.app.ui.group.viewmodel.AssistanceViewModel
import da.farmer.app.ui.group.viewmodel.AssistanceViewState
import da.farmer.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AssistanceRequestFragment : Fragment(), AssistanceAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener, FilterSelectedListener {

    private var _binding: FragmentGroupAssistanceBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AssistanceAdapter? = null
    private var actionNavigationGroupAssistanceReqDetails: NavDirections? = null
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
        adapter = AssistanceAdapter(activity,this@AssistanceRequestFragment)
        swipeRefreshLayout.setOnRefreshListener(this@AssistanceRequestFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

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
            is AssistanceViewState.SuccessGetMyListOfAssistance -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showList(viewState.pagingData)
            }

            is AssistanceViewState.PopupError -> {
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

    private fun showList(createAssistanceData: PagingData<CreateAssistanceData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, createAssistanceData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    companion object {
        private const val REQUEST = "REQUEST"
        fun newInstance(direction: NavDirections): AssistanceRequestFragment {
            val fragment = AssistanceRequestFragment()
            fragment.actionNavigationGroupAssistanceReqDetails = direction
            return fragment
        }
    }

    override fun onItemClicked(data: CreateAssistanceData) {
        activity.referenceId = data.reference_id.toString()
        findNavController().navigate(actionNavigationGroupAssistanceReqDetails!!)
    }

    override fun onRefresh() {
        clearList()
        viewModel.refreshMyList(activity.groupDetails?.id.toString(), emptyList())
    }

    private fun clearList() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onFilterSelected(filter: List<String>) {
        clearList()
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.refreshMyList(activity.groupDetails?.id.toString(), filter)
    }

}