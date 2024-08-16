package da.farmer.app.ui.verify.fragment

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import da.farmer.app.R
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.profile.request.KYCRequest
import da.farmer.app.data.repositories.profile.response.LOVData
import da.farmer.app.data.repositories.profile.response.LOVResponse
import da.farmer.app.databinding.FragmentProofOfAddressBinding
import da.farmer.app.ui.profile.viewmodel.ProfileViewState
import da.farmer.app.ui.verify.VerifyViewModel
import da.farmer.app.ui.verify.dialog.LOVListDialog
import da.farmer.app.utils.PopupErrorState
import da.farmer.app.utils.dialog.CommonDialog
import da.farmer.app.utils.getFileFromUri
import da.farmer.app.utils.loadImage
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.showPopupError
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class ProofOfAddressFragment : Fragment() {

    private var _binding: FragmentProofOfAddressBinding? = null
    private val binding get() = _binding!!

    private var uriFilePath: Uri? = null
    private var isBackImage = false
    private var selectedIdType : String = ""

    private var loadingDialog: CommonDialog? = null
    private var dialogListInstance: LOVListDialog? = null

    private val viewModel : VerifyViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProofOfAddressBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUploadProofOfAddress()
        setupClickListener()
        viewModel.getProofOfAddressLists()
    }

    private fun setupClickListener() = binding.run {
        idTypeSpinner.setOnSingleClickListener {
            dialogListInstance?.show(childFragmentManager, LOVListDialog.TAG)
        }
        proofOfAddressRelativeLayout.setOnSingleClickListener {
            openMediaOptionPicker()
        }
        continueButton.setOnSingleClickListener {
            //reset to empty string if default string is still selected
            if(selectedIdType == getString(R.string.select_proof_of_address)){
                selectedIdType = ""
            }
            if (viewModel.frontImageFile != null){
                viewModel.doUploadProofOfAddress(
                    KYCRequest(
                    idType = selectedIdType,
                    frontImageFile = viewModel.frontImageFile!!,
                    )
                )
            }else{
                requireActivity().toastError( getString(R.string.kyc_upload_proof_of_address_error), CpmToast.LONG_DURATION)
            }
        }
    }


private fun observeUploadProofOfAddress() {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED){
            viewModel.kycSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }
}


    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.SuccessUploadAddress -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
                findNavController().popBackStack()
            }
            is ProfileViewState.SuccessLoadLOVProofOfAddress -> {
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
        if (errorsData.image?.get(0)?.isNotEmpty() == true) {
            showPopupError(requireContext(),
                childFragmentManager,
                PopupErrorState.HttpError,
                errorsData.image?.get(0).toString())
        }
        if (errorsData.type?.get(0)?.isNotEmpty() == true) {
            showPopupError(requireContext(),
                childFragmentManager,
                PopupErrorState.HttpError,
                getString(R.string.please_select_proof_of_address))
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

    private fun setSpinner(lovResponse: LOVResponse) = binding.run {
        dialogListInstance = LOVListDialog.newInstance(title = "Proof of Address Types List" ,lovData = lovResponse.data.orEmpty(), callback = object : LOVListDialog.LOVListDialogCallBack {
            @SuppressLint("SetTextI18n")
            override fun onLovDataClicked(
                dialog: LOVListDialog,
                lovData: LOVData
            ) {
                selectedIdType =  lovData.code.toString()
                binding.idTypeSpinner.text = lovData.name.toString()
                dialog.dismiss()
            }
        })
    }


    private fun openMediaOptionPicker() {
        val choices = arrayOf("Camera", "Gallery")
        val mBuilder = AlertDialog.Builder(requireActivity())
        mBuilder.setTitle("Choose an action")
        mBuilder.setSingleChoiceItems(choices, -1) { dialogInterface: DialogInterface, i: Int ->
            if (choices[i].equals("Camera")) {
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
            binding.proofOfAddressImageView.loadImage(uriFilePath.toString(),requireActivity())
            viewModel.frontImageFile = getFileFromUri(requireActivity(), uriFilePath)
        }
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let { uri ->
            binding.proofOfAddressImageView.loadImage(uri.toString(),requireActivity())
            viewModel.frontImageFile = getFileFromUri(requireActivity(), uri)
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