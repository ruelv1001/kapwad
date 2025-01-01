package kapwad.reader.app.ui.crops.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.crops.response.CropsData
import kapwad.reader.app.databinding.FragmentCropsListBinding
import kapwad.reader.app.ui.crops.activity.CropsActivity
import kapwad.reader.app.ui.crops.adapter.CropsListAdapter
import kapwad.reader.app.ui.crops.viemodel.CropsViewModel
import kapwad.reader.app.ui.crops.viemodel.CropsViewState
import kapwad.reader.app.utils.dialog.CommonDialog

import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CropsListFragment : Fragment(), CropsListAdapter.CropsListCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentCropsListBinding? = null
    private val binding get() = _binding!!


    private val activity by lazy { requireActivity() as CropsActivity }
    private var loadingDialog: CommonDialog? = null
    private var adapter: CropsListAdapter? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: CropsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCropsListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setUpAdapter()
        observeCrops()
    }

    private fun observeCrops() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cropsSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }


    private fun handleViewState(viewState: CropsViewState) {
        when (viewState) {
            is CropsViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is CropsViewState.LoadingScan -> activity.showLoadingDialog(R.string.loading)
            is CropsViewState.Success -> {
                if (adapter?.getData()?.size == 0) {
                    adapter?.clear()
                    adapter?.appendData(viewState.cropsResponse?.data.orEmpty())
                    hideLoadingDialog()
                    binding.swipeRefreshLayout.isRefreshing = false
                } else {

                }
            }


            is CropsViewState.PopupError -> {
                activity.hideLoadingDialog()
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

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun setUpAdapter() = binding.run {
        swipeRefreshLayout.setOnRefreshListener(this@CropsListFragment)
        adapter = CropsListAdapter(requireActivity(), this@CropsListFragment)
        linearLayoutManager = LinearLayoutManager(context)
        cropsListRecyclerView.layoutManager = linearLayoutManager
        cropsListRecyclerView.adapter = adapter


    }


    private fun setClickListeners() = binding.run {


    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {

        viewModel.getCropsList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: CropsData, position: Int) {
        findNavController().navigate(CropsListFragmentDirections.actionMenu(data.id.toString()))
    }


}