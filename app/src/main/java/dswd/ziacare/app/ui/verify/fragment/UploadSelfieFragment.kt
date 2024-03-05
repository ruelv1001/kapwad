package dswd.ziacare.app.ui.verify.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.profile.request.FaceIDRequest
import dswd.ziacare.app.databinding.FragmentUploadSelfieBinding
import dswd.ziacare.app.ui.profile.viewmodel.ProfileViewState
import dswd.ziacare.app.utils.facedetect.ImageManager
import dswd.ziacare.app.utils.facedetect.facecentercircleview.FaceCenterCrop
import dswd.ziacare.app.utils.facedetect.utils.Imageutils
import dswd.ziacare.app.utils.facedetect.utils.Imageutils.GALEERY_REQUEST_CODE
import dswd.ziacare.app.utils.facedetect.utils.Imageutils.SCANNER_REQUEST_CODE
import dswd.ziacare.app.utils.facedetect.utils.progressbarutil.ProgressBarData
import dswd.ziacare.app.utils.facedetect.utils.progressbarutil.ProgressUtils
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dswd.ziacare.app.ui.verify.VerifyViewModel
import dswd.ziacare.app.ui.verify.activity.AccountVerificationActivity
import dswd.ziacare.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class UploadSelfieFragment : Fragment(), Imageutils.ImageAttachmentListener {
    private var _binding: FragmentUploadSelfieBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerifyViewModel by viewModels()
    private var imageutils: Imageutils? = null
    private var faceCenterCrop: FaceCenterCrop? = null
    private var faceCenterCropListener: FaceCenterCrop.FaceCenterCropListener? = null
    private var num = 0
    private var progressUtils: ProgressUtils? = null
    private val activity by lazy { requireActivity() as AccountVerificationActivity }
    private var loadingDialog: CommonDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUploadSelfieBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setupFaceScanner()
        observeUploadSelfie()
    }

    private fun setupFaceScanner(){

        //FOR FACE DETECTION
        imageutils = Imageutils(activity, this, true)
        imageutils?.imageAttachment_callBack = this
        progressUtils = ProgressUtils(activity)
        faceCenterCrop = FaceCenterCrop(activity, 100, 100, 1)
    }

    private fun setClickListeners() = binding.run {
        rightImageView.setOnSingleClickListener {
            num = 3
            openStoragePermissionChecker(num)
        }
        frontImageView.setOnSingleClickListener {
            num = 1
            openStoragePermissionChecker(num)
        }
        leftImageView.setOnSingleClickListener {
            num = 2
            openStoragePermissionChecker(num)
        }
        continueButton.setOnSingleClickListener {
            when{
                viewModel.leftImageFile == null -> {
                    Toast.makeText(requireActivity(), getString(R.string.kyc_upload_left_selfie_error), Toast.LENGTH_LONG).show()
                }
                viewModel.frontImageFile == null -> {
                    Toast.makeText(requireActivity(), getString(R.string.kyc_upload_front_selfie_error), Toast.LENGTH_LONG).show()
                }
                viewModel.rightImageFile == null -> {
                    Toast.makeText(requireActivity(), getString(R.string.kyc_upload_right_selfie_error), Toast.LENGTH_LONG).show()
                }
                else ->{
                    val request = FaceIDRequest(
                        front_image = viewModel.frontImageFile!!,
                        left_image = viewModel.leftImageFile!!,
                        right_image = viewModel.rightImageFile!!,
                    )
                    viewModel.doUploadFacialIds(request)
                }
            }
        }
    }

    private fun openStoragePermissionChecker(num: Int) {
        if (Build.VERSION.SDK_INT <= 28){
            if (hasStoragePermissions(requireActivity())) {
                imageutils?.imagepicker(1, num)
            } else {
                requestPermission()
            }
        }else{
            imageutils?.imagepicker(1, num)
        }
    }
    private fun hasStoragePermissions(context: Activity): Boolean {
        return !(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_READ_WRITE_STORAGE
        )
    }

    private fun getFaceCropResult(): FaceCenterCrop.FaceCenterCropListener? {
        if (faceCenterCropListener == null) faceCenterCropListener =
            object : FaceCenterCrop.FaceCenterCropListener {
                override fun onTransform(updatedBitmap: Bitmap?) {
                    Log.d("Time log", "Output is set")
                    binding.frontImageView.setImageBitmap(updatedBitmap)
                    Toast.makeText(activity, "We detected a face", Toast.LENGTH_SHORT).show()
                    progressUtils?.dismissDialog()
                }

                override fun onFailure() {
                    Toast.makeText(activity, "No face was detected", Toast.LENGTH_SHORT).show()
                    progressUtils?.dismissDialog()
                }
            }
        return faceCenterCropListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            imageutils!!.onActivityResult(requestCode, resultCode, data, num)
            if (requestCode == SCANNER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: $SCANNER_REQUEST_CODE")
            } else if (requestCode == GALEERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: $GALEERY_REQUEST_CODE")
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.toString(),
                Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun image_attachment(
        from: Int,
        filename: String?,
        file: Bitmap?,
        uri: Uri?,
        num: Int,
    ) {
        if (from == SCANNER_REQUEST_CODE) {
            if (num == 1) {
                binding.frontImageView.setImageBitmap(file)
                val bitmap =  binding.frontImageView.drawable.toBitmap()
                val u = imageutils!!.getImageUri(activity, bitmap)
                val bit: Bitmap
                try {
                    bit = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, u)
                    val nh = (bit.height * (512.0 / bit.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(bit, 512, nh, true)
                    ImageManager.getFileFromBitmap(context, scaled, "smile", object : ImageManager.Callback {
                        override fun success(file: File?) {
                            viewModel.frontImageFile = file
                        }

                        override fun success(file1: File?, file2: File?) {}
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (num == 2) {
                binding.leftImageView.setImageBitmap(file)
                val bitmap =  binding.leftImageView.drawable.toBitmap()
                val u = imageutils!!.getImageUri(activity, bitmap)
                val bit: Bitmap
                try {
                    bit = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, u)
                    val nh = (bit.height * (512.0 / bit.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(bit, 512, nh, true)
                    ImageManager.getFileFromBitmap(context, scaled, "smile", object : ImageManager.Callback {
                        override fun success(file: File?) {
                            viewModel.leftImageFile = file
                        }

                        override fun success(file1: File?, file2: File?) {}
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (num == 3) {
                binding.rightImageView.setImageBitmap(file)
                val bitmap = binding.rightImageView.drawable.toBitmap()
                val u = imageutils!!.getImageUri(activity, bitmap)
                val bit: Bitmap
                try {
                    bit = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, u)
                    val nh = (bit.height * (512.0 / bit.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(bit, 512, nh, true)
                    ImageManager.getFileFromBitmap(context, scaled, "smile", object : ImageManager.Callback {
                        override fun success(file: File?) {
                            viewModel.rightImageFile = file
                        }

                        override fun success(file1: File?, file2: File?) {}
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (from == GALEERY_REQUEST_CODE) {
            Log.d("Time log", "IA callback triggered")
            val progressBarData: ProgressBarData = ProgressBarData.ProgressBarBuilder()
                .setCancelable(true)
                .setProgressMessage("Processing")
                .setProgressMessageColor(Color.parseColor("#4A4A4A"))
                .setBackgroundViewColor(Color.parseColor("#FFFFFF"))
                .setProgressbarTintColor(Color.parseColor("#FAC42A")).build()
            progressUtils?.showDialog(progressBarData)
            faceCenterCrop?.detectFace(file, getFaceCropResult())
        }
    }

    private fun observeUploadSelfie() {
        lifecycleScope.launch {
            viewModel.kycSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.kyc_upload_selfie_loading)
            is ProfileViewState.SuccessUploadFacialId -> {
                hideLoadingDialog()
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_LONG).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }
            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null){

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
        fun newInstance() = UploadSelfieFragment()
        private const val REQUEST_READ_WRITE_STORAGE = 1
        val TAG: String = UploadSelfieFragment::class.java.name
    }

}