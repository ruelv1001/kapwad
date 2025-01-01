package kapwad.reader.app.ui.geotagging.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.crops.response.CropItemData
import kapwad.reader.app.data.repositories.geotagging.request.GeotaggingUploadRequest
import kapwad.reader.app.databinding.FragmentSubmitAreaBinding
import kapwad.reader.app.ui.crops.activity.CropsActivity
import kapwad.reader.app.ui.crops.adapter.DateListAdapter

import kapwad.reader.app.ui.geotagging.dialog.SubmitImageDialog
import kapwad.reader.app.ui.geotagging.viewmodel.GeoTaggingViewModel
import kapwad.reader.app.ui.geotagging.viewmodel.GeoTaggingViewState
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.getFileFromUri
import kapwad.reader.app.utils.showPopupError
import kapwad.reader.app.utils.showToastSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubbmitAreaFragment : Fragment(), DateListAdapter.DateCallback {

    private var _binding: FragmentSubmitAreaBinding? = null
    private val binding get() = _binding!!


    private val activity by lazy { requireActivity() as CropsActivity }
    private var geoArea = ""
    private val viewModel: GeoTaggingViewModel by activityViewModels()
    private var adapter: DateListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val args: SubbmitAreaFragmentArgs by this.navArgs()
    private var loadingDialog: CommonDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSubmitAreaBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeUploadImage()
    }

    private fun observeUploadImage() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.geotaggingSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }


    private fun handleViewState(viewState: GeoTaggingViewState) {
        when (viewState) {
            is GeoTaggingViewState.Loading -> showLoadingDialog(R.string.loading)
            is GeoTaggingViewState.SuccessUploadImage -> {
                showLoadingDialog(R.string.loading)
                showToastSuccess(requireActivity(), description = viewState.message)
                hideLoadingDialog()
                val intent = MainActivity.getIntent(requireActivity())
                startActivity(intent)
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

    private fun setClickListeners() = binding.run {
        areaTextView.text=args.area
        nextTextView.setOnClickListener {
            if (viewModel.image != null||remarksTextView.text.equals("")) {
                viewModel.doUploadImageArea(
                    GeotaggingUploadRequest(
                        land_area = args.area,
                        remarks =binding.remarksTextView.text.toString(),
                        image = viewModel.image,
                    )
                )
            } else {
                requireActivity().toastError(
                    getString(R.string.submit_error),
                    CpmToast.LONG_DURATION
                )
            }
        }
        imageTextView.setOnClickListener {
            SubmitImageDialog.newInstance(object :
                SubmitImageDialog.SuccessCallBack {
                override fun onSuccess(imageUri: Uri) {
                    imageTextView.setText(imageUri.path)
                    viewModel.image = getFileFromUri(requireActivity(), imageUri)

                }

                override fun onCancel(dialog: SubmitImageDialog) {

                }
            }, "Select Image").show(childFragmentManager, SubmitImageDialog.TAG)
        }
    }


    override fun onItemClicked(data: CropItemData, position: Int) {

    }
}