package com.lionscare.app.ui.verify.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.profile.request.KYCRequest
import com.lionscare.app.data.repositories.profile.response.LOVResponse
import com.lionscare.app.databinding.FragmentUploadIdBinding
import com.lionscare.app.ui.settings.viewmodel.ProfileViewState
import com.lionscare.app.ui.verify.VerifyViewModel
import com.lionscare.app.utils.PopupErrorState
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.getFileFromUri
import com.lionscare.app.utils.loadImage
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class UploadIDFragment : Fragment() {

    private var _binding: FragmentUploadIdBinding? = null
    private val binding get() = _binding!!

    private var uriFilePath: Uri? = null
    private var isBackImage = false
    private var selectedIdType : String = ""

    private var loadingDialog: CommonDialog? = null

    private val viewModel : VerifyViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadIdBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUploadId()
        setupClickListener()
        viewModel.getIDLists()
    }

    private fun setupClickListener() = binding.run {
        frontIdRelativeLayout.setOnSingleClickListener {
            isBackImage = false
            openMediaOptionPicker()
        }
        backIdRelativeLayout.setOnSingleClickListener {
            isBackImage = true
            openMediaOptionPicker()
        }
        continueButton.setOnSingleClickListener {
            if (viewModel.frontImageFile != null && viewModel.backImageFile != null){
                viewModel.doUploadId(KYCRequest(
                    idType = selectedIdType,
                    frontImageFile = viewModel.frontImageFile!!,
                    backImageFile = viewModel.backImageFile,
                ))
            }else{
                Toast.makeText(requireActivity(),
                    getString(R.string.kyc_upload_id_error), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun observeUploadId() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.kycSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.SuccessUploadId -> {
                hideLoadingDialog()
                Toast.makeText(requireContext(),
                    viewState.message, Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
            is ProfileViewState.SuccessLoadLOVIds -> {
                hideLoadingDialog()
                viewState.lovResponse?.let { setSpinner(it) }
            }
            is ProfileViewState.InputError -> {
                hideLoadingDialog()
                val errorData = viewState.errorData
                if (errorData != null){
                    handleInputError(errorData)
                }
            }
            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireContext(), childFragmentManager, viewState.errorCode, viewState.message)
            }
            else -> {
                hideLoadingDialog()
            }
        }
    }

    private fun handleInputError(errorsData: ErrorsData){
        if (errorsData.image?.get(0)?.isNotEmpty() == true) showPopupError(requireContext(),
            childFragmentManager,
            PopupErrorState.UnknownError,
            errorsData.image?.get(0).toString())
        if (errorsData.type?.get(0)?.isNotEmpty() == true) showPopupError(requireContext(),
            childFragmentManager,
            PopupErrorState.UnknownError,
            errorsData.type?.get(0).toString())
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

    private fun setSpinner(lovResponse: LOVResponse) = binding.run {
        val adapter = ArrayAdapter<String>(requireActivity(),  android.R.layout.simple_spinner_item, lovResponse.data?.map { it.name }.orEmpty().toMutableList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        idTypeSpinner.adapter = adapter
        idTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedIdType =  parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


    private fun openMediaOptionPicker() {
        val choices = arrayOf("Camera", "Gallery")
        val mBuilder = AlertDialog.Builder(requireActivity())
        mBuilder.setTitle("Choose an action")
        mBuilder.setSingleChoiceItems(choices, -1) { dialogInterface: DialogInterface, i: Int ->
            if (choices[i] == "Camera") {
                openCameraChecker()
            } else {
                openPhotoPicker()
            }
            dialogInterface.dismiss()
        }
        mBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, which: Int ->
            dialogInterface.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun openPhotoPicker(){
        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        if (isSaved)
        {
            if (isBackImage){
                binding.backIdImageView.loadImage(uriFilePath.toString(),requireActivity())
                viewModel.backImageFile = getFileFromUri(requireActivity(), uriFilePath)
            }else {
                binding.frontIdImageView.loadImage(uriFilePath.toString(),requireActivity())
                viewModel.frontImageFile = getFileFromUri(requireActivity(), uriFilePath)
            }
        }
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let { uri ->
            if (isBackImage){
                binding.backIdImageView.loadImage(uri.toString(),requireActivity())
                viewModel.backImageFile = getFileFromUri(requireActivity(), uri)
            }else{
                binding.frontIdImageView.loadImage(uri.toString(),requireActivity())
                viewModel.frontImageFile = getFileFromUri(requireActivity(), uri)
            }
        }
    }

    private fun openCameraChecker() {
        if (hasImageCapturePermissions(requireActivity())) {
            val calendar = Calendar.getInstance()
            val fileName = File.createTempFile(
                "IMG_" + calendar.timeInMillis,
                ".jpg",
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            uriFilePath = FileProvider.getUriForFile(
                requireActivity(),
                "${requireActivity().packageName}.provider",
                fileName
            )

            takePicture.launch(uriFilePath)

        } else {
            requestPermission()
        }
    }

    private fun hasImageCapturePermissions(context: Activity): Boolean {
        return !(ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_IMAGE_CAPTURE
        )
    }

    override fun onRequestPermissionsResult (
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    )  = binding.run {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == 0) {
                openCameraChecker()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}