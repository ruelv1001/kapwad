package kapwad.reader.app.ui.crops.fragment

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import kapwad.reader.app.R
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.crops.request.CropDetailsRequest
import kapwad.reader.app.data.repositories.crops.request.UploadImageRequest
import kapwad.reader.app.data.repositories.crops.request.UploadVideRequest
import kapwad.reader.app.data.repositories.crops.response.CropDetailsResponse
import kapwad.reader.app.data.repositories.crops.response.CropItemData
import kapwad.reader.app.databinding.FragmentCropsPictureBinding
import kapwad.reader.app.ui.crops.activity.CropsActivity
import kapwad.reader.app.ui.crops.adapter.DateListAdapter
import kapwad.reader.app.ui.crops.dialog.SubmitUploadImageDialog
import kapwad.reader.app.ui.crops.dialog.SubmitUploadVideoDialog
import kapwad.reader.app.ui.crops.viemodel.CropsViewModel
import kapwad.reader.app.ui.crops.viemodel.CropsViewState

import kapwad.reader.app.utils.CommonLogger
import kapwad.reader.app.utils.PopupErrorState
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.getFileFromUri
import kapwad.reader.app.utils.showPopupError
import kapwad.reader.app.utils.showToastSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CropsPictureFragment : Fragment(), DateListAdapter.DateCallback {

    private var _binding: FragmentCropsPictureBinding? = null
    private val binding get() = _binding!!


    private val activity by lazy { requireActivity() as CropsActivity }
    private var uriFilePath: Uri? = null
    private val ImgUrl: String? = null
    private val viewModel: CropsViewModel by activityViewModels()

    private val args: CropsMenuFragmentArgs by this.navArgs()
    private var adapter: DateListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var loadingDialog: CommonDialog? = null
    private var cropsData: CropDetailsResponse? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCropsPictureBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeUploadVideo()

    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val request = CropDetailsRequest(
            item_id = args.cropsId
        )
        viewModel.getCropDetails(request)
    }


    private fun setClickListeners() = binding.run {
        nextTextView.setOnClickListener {
            findNavController().navigate(CropsPictureFragmentDirections.actionSubmittedCrops())
        }
        firstPictureTextView.setOnClickListener {
            SubmitUploadImageDialog.newInstance(object :
                SubmitUploadImageDialog.SuccessCallBack {
                override fun onSuccess(imageUri: Uri) {
                    firstPictureTextView
                    viewModel.image = getFileFromUri(requireActivity(), imageUri)
                    if (viewModel.image != null) {
                        viewModel.doUploadImage(
                            UploadImageRequest(
                                item_id = args.cropsId,
                                type = "left",
                                image = viewModel.image,
                            )
                        )
                    } else {
                        requireActivity().toastError(
                            getString(R.string.image_error),
                            CpmToast.LONG_DURATION
                        )
                    }
                }

                override fun onCancel(dialog: SubmitUploadImageDialog) {

                }
            }, "Left Corner of the Farm").show(childFragmentManager, SubmitUploadImageDialog.TAG)
        }
        secondPictureTextView.setOnClickListener {
            SubmitUploadImageDialog.newInstance(object :
                SubmitUploadImageDialog.SuccessCallBack {
                override fun onSuccess(imageUri: Uri) {
                    firstPictureTextView
                    viewModel.image = getFileFromUri(requireActivity(), imageUri)
                    if (viewModel.image != null) {
                        viewModel.doUploadImage(
                            UploadImageRequest(
                                item_id = args.cropsId,
                                type = "middle",
                                image = viewModel.image,
                            )
                        )
                    } else {
                        requireActivity().toastError(
                            getString(R.string.image_error),
                            CpmToast.LONG_DURATION
                        )
                    }
                }

                override fun onCancel(dialog: SubmitUploadImageDialog) {

                }
            }, "Middle Corner of the Farm").show(childFragmentManager, SubmitUploadImageDialog.TAG)
        }

        thirdPictureTextView.setOnClickListener {
            SubmitUploadImageDialog.newInstance(object :
                SubmitUploadImageDialog.SuccessCallBack {
                override fun onSuccess(imageUri: Uri) {
                    firstPictureTextView
                    viewModel.image = getFileFromUri(requireActivity(), imageUri)
                    if (viewModel.image != null) {
                        viewModel.doUploadImage(
                            UploadImageRequest(
                                item_id = args.cropsId,
                                type = "right",
                                image = viewModel.image,
                            )
                        )
                    } else {
                        requireActivity().toastError(
                            getString(R.string.image_error),
                            CpmToast.LONG_DURATION
                        )
                    }
                }

                override fun onCancel(dialog: SubmitUploadImageDialog) {

                }
            }, "Right Corner of the Farm").show(childFragmentManager, SubmitUploadImageDialog.TAG)
        }

        leftTextView.setOnClickListener {
            SubmitUploadVideoDialog.newInstance(object :
                SubmitUploadVideoDialog.SuccessCallBack {
                override fun onSuccess(videoUri: Uri) {
                    viewModel.video = getFileFromUri(requireActivity(), videoUri)
                    if (viewModel.video != null) {
                        viewModel.doUploadVideo(
                            UploadVideRequest(
                                item_id = args.cropsId,
                                type = "left",
                                video = viewModel.video,
                            )
                        )
                    } else {
                        requireActivity().toastError(
                            getString(R.string.video_error),
                            CpmToast.LONG_DURATION
                        )
                    }
                }

                override fun onCancel(dialog: SubmitUploadVideoDialog) {

                }
            }, "Left Corner of the Farm").show(childFragmentManager, SubmitUploadVideoDialog.TAG)
        }
        middleTextView.setOnClickListener {
            SubmitUploadVideoDialog.newInstance(object :
                SubmitUploadVideoDialog.SuccessCallBack {
                override fun onSuccess(videoUri: Uri) {

                    viewModel.video = getFileFromUri(requireActivity(), videoUri)
                    if (viewModel.video != null) {
                        viewModel.doUploadVideo(
                            UploadVideRequest(
                                item_id = args.cropsId,
                                type = "middle",
                                video = viewModel.video,
                            )
                        )
                    } else {
                        requireActivity().toastError(
                            getString(R.string.video_error),
                            CpmToast.LONG_DURATION
                        )
                    }
                }

                override fun onCancel(dialog: SubmitUploadVideoDialog) {

                }
            }, "Middle Corner of the Farm").show(childFragmentManager, SubmitUploadVideoDialog.TAG)
        }
        rightTextView.setOnClickListener {
            SubmitUploadVideoDialog.newInstance(object :
                SubmitUploadVideoDialog.SuccessCallBack {
                override fun onSuccess(videoUri: Uri) {

                    viewModel.video = getFileFromUri(requireActivity(), videoUri)
                    if (viewModel.video != null) {
                        viewModel.doUploadVideo(
                            UploadVideRequest(
                                item_id = args.cropsId,
                                type = "right",
                                video = viewModel.video,
                            )
                        )
                    } else {
                        requireActivity().toastError(
                            getString(R.string.video_error),
                            CpmToast.LONG_DURATION
                        )
                    }
                }

                override fun onCancel(dialog: SubmitUploadVideoDialog) {

                }
            }, "Right Corner of the Farm").show(childFragmentManager, SubmitUploadVideoDialog.TAG)
        }


    }

    private fun observeUploadVideo() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cropsSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: CropsViewState) {
        when (viewState) {
            is CropsViewState.Loading -> showLoadingDialog(R.string.loading)
            is CropsViewState.SuccessUploadVideo -> {
                showLoadingDialog(R.string.loading)
                showToastSuccess(requireActivity(), description = viewState.message)
                refresh()
            }

            is CropsViewState.SuccessCropDetails -> {
                showLoadingDialog(R.string.loading)
                setupDetails(viewState.cropDetailsResponse)
                hideLoadingDialog()
            }

            is CropsViewState.InputError -> {
                hideLoadingDialog()
                val errorData = viewState.errorData
                if (errorData != null) {
                    handleInputError(errorData)
                }
            }

            is CropsViewState.PopupError -> {
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

    private fun setupDetails(cropDetailsResponse: CropDetailsResponse) = binding.run {
        leftTextView.text = "Left Video Corner of the Farm: ${cropDetailsResponse.data.left_video_status}"
        middleTextView.text = "Middle Video Corner of the Farm: ${cropDetailsResponse.data.middle_video_status}"
        rightTextView.text = "Right Video Corner of the Farm: ${cropDetailsResponse.data.right_video_status}"
        firstPictureTextView.text = "Left Image Corner of the Farm: ${cropDetailsResponse.data.left_image_status}"
        secondPictureTextView.text = "Middle Image Corner of the Farm: ${cropDetailsResponse.data.middle_image_status}"
        thirdPictureTextView.text = "Right Image Corner of the Farm: ${cropDetailsResponse.data.right_image_status}"
    }

    private fun handleInputError(errorsData: ErrorsData) {
        CommonLogger.instance.sysLogE("HERE", errorsData)
        if (errorsData.video?.get(0)?.isNotEmpty() == true) {
            showPopupError(
                requireContext(),
                childFragmentManager,
                PopupErrorState.HttpError,
                errorsData.video?.get(0).toString()
            )
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

    companion object {
        private const val CROPPED_IMAGE_NAME = "lionscare_avatar01"
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onItemClicked(data: CropItemData, position: Int) {

    }
}