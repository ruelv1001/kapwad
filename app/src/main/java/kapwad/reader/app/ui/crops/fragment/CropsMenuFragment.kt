package kapwad.reader.app.ui.crops.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.crops.response.CropItemData
import kapwad.reader.app.databinding.FragmentCropsMenuBinding
import kapwad.reader.app.ui.crops.activity.CropsActivity
import kapwad.reader.app.ui.crops.adapter.DateListAdapter
import kapwad.reader.app.ui.crops.viemodel.CropsViewModel
import kapwad.reader.app.ui.crops.viemodel.CropsViewState
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CropsMenuFragment : Fragment(), DateListAdapter.DateCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentCropsMenuBinding? = null
    private val binding get() = _binding!!


    private val activity by lazy { requireActivity() as CropsActivity }
    private var loadingDialog: CommonDialog? = null
    private val viewModel: CropsViewModel by viewModels()
    private var adapter: DateListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val args: CropsMenuFragmentArgs by this.navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCropsMenuBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        setupList()
        observeCropsList()
    }
    private fun observeCropsList() {
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.cropsSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }
    private fun handleViewState(viewState: CropsViewState) {
        when (viewState) {
            is CropsViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is CropsViewState.LoadingScan -> activity.showLoadingDialog(R.string.loading)
            is CropsViewState.SuccessCropItem -> {
                if (adapter?.getData()?.size == 0) {
                    adapter?.clear()
                    adapter?.appendData(viewState.cropItemListResponse?.data.orEmpty())
                    hideLoadingDialog()
                    binding.swipeRefreshLayout.isRefreshing = false

                } else {

                }
            }


            is CropsViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }
    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun setupList() {
        binding?.apply {
            swipeRefreshLayout.setOnRefreshListener(this@CropsMenuFragment)
            adapter = DateListAdapter(requireActivity(), this@CropsMenuFragment)
            layoutManager = LinearLayoutManager(context)
            dateRecyclerView.layoutManager = layoutManager
            dateRecyclerView.adapter = adapter

        }
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {
        viewModel.getCropsItemList(args.cropsId.toString())
    }

    private fun setClickListeners() = binding.run {


    }


    override fun onItemClicked(data: CropItemData, position: Int) {
        findNavController().navigate(CropsMenuFragmentDirections.actionPictureCrops(data.id.toString()))
    }


}