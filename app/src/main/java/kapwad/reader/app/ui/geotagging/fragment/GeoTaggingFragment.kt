package kapwad.reader.app.ui.geotagging.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.crops.response.CropItemData
import kapwad.reader.app.databinding.FragmentGeoTaggingBinding
import kapwad.reader.app.ui.crops.activity.CropsActivity
import kapwad.reader.app.ui.crops.adapter.DateListAdapter
import kapwad.reader.app.ui.geotagging.viewmodel.GeoTaggingViewModel
import kapwad.reader.app.ui.geotagging.viewmodel.GeoTaggingViewState
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeoTaggingFragment : Fragment(), DateListAdapter.DateCallback {

    private var _binding: FragmentGeoTaggingBinding? = null
    private val binding get() = _binding!!


    private val activity by lazy { requireActivity() as CropsActivity }


    private var adapter: DateListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var loadingDialog: CommonDialog? = null
    private val viewModel: GeoTaggingViewModel by activityViewModels()
    private var geostatus = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGeoTaggingBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeGetStatus()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStatus()
    }

    private fun handleViewState(viewState: GeoTaggingViewState) {
        when (viewState) {
            is GeoTaggingViewState.Loading -> showLoadingDialog(R.string.loading)
            is GeoTaggingViewState.SuccessStatus -> {
                showLoadingDialog(R.string.loading)
                geostatus=viewState.geoTaggingResponse.data?.status.toString()
                binding.statusTextView.setText(viewState.geoTaggingResponse.data?.status.toString())
                if(geostatus.equals("approved")){
                    binding.nextTextView.isGone=true
                }
                else{
                    binding.nextTextView.isVisible=true
                }
                hideLoadingDialog()
            }

            is GeoTaggingViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireContext(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> {
                hideLoadingDialog()
            }
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(childFragmentManager)
        }
    }


    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun observeGetStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.geotaggingSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun setClickListeners() = binding.run {
        nextTextView.setOnClickListener {
            findNavController().navigate(GeoTaggingFragmentDirections.actionGeoMap())
        }
    }


    override fun onItemClicked(data: CropItemData, position: Int) {

    }
}