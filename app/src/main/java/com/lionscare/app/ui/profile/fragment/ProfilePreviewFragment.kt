package com.lionscare.app.ui.profile.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.databinding.FragmentProfilePreviewBinding
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.ui.profile.dialog.VerificationSuccessDialog
import com.lionscare.app.ui.profile.viewmodel.ProfileViewModel
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.ui.verify.fragment.ProofOfAddressFragment
import com.lionscare.app.utils.calculateAge
import com.lionscare.app.utils.getFileFromUri
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.loadImage
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class ProfilePreviewFragment : Fragment() {
    private var _binding: FragmentProfilePreviewBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }
    private val viewModel: ProfileViewModel by activityViewModels()

    private var uriFilePath: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfilePreviewBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        setClickListeners()
        viewModel.getProfileDetails()
    }


    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.profileSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState){
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
                viewModel.userModel = viewState.userModel
                setView(viewState.userModel)
            }
            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            is ProfileViewState.InputError -> {
                hideLoadingDialog()
            }

            else -> hideLoadingDialog()
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_my_profile))
        requireActivity().title = getString(R.string.lbl_my_profile)
        hideLoadingDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(userModel: UserModel?) = binding.run {
        nameTextView.text = userModel?.getFullName()
        dateOfBirthTextView.text = userModel?.birthdate?.date_only_ph?.ifEmpty { "Not set "}
        ageTextView.text = userModel?.birthdate?.date_only_ph
            ?.takeIf { it.isNotEmpty() }
            ?.calculateAge()
            ?.toString() ?: "Not set"

        if (userModel?.province_name?.isNotEmpty() == true){
            addressTextView.text = "${userModel?.street_name}, ${userModel?.brgy_name},\n${userModel?.city_name}, ${userModel?.province_name}"
        }else{
            addressTextView.text = "Address Unavailable"
        }

        emailEditText.setText(userModel?.email.orEmpty().ifEmpty { "Check your email for verification" })
        if (userModel?.email_verified == true){
            emailIsVerifiedTextView.text = getString(R.string.lbl_verified)
            emailIsVerifiedTextView.setBackgroundResource(R.drawable.bg_rounded_verified)
        }else{
            emailIsVerifiedTextView.text = getString(R.string.unverified)
            emailIsVerifiedTextView.setBackgroundResource(R.drawable.bg_rounded_pending)
        }

        phoneEditText.setText(userModel?.phone_number)
        if (userModel?.lc_member == true){
            lionsClubTextView.text = "${userModel.lc_group} (${userModel.lc_location_id})"
        }else{
            lionsClubTextView.text = getString(R.string.not_yet_a_member)
        }
    }

    private fun setClickListeners() = binding.run  {
        emailEditText.setOnSingleClickListener {
            findNavController().navigate(ProfilePreviewFragmentDirections.actionNavigationProfilePreviewToProfileEditEmailaddress())
        }
        phoneEditText.setOnSingleClickListener {
            findNavController().navigate(ProfilePreviewFragmentDirections.actionNavigationProfilePreviewToProfileEditNumberFragment())
        }
        editImageView.setOnSingleClickListener {
            findNavController().navigate(ProfilePreviewFragmentDirections.actionNavigationProfileUpdate())
        }
        profileImageView.setOnSingleClickListener {
            openMediaOptionPicker()
        }
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
            // Launch the cropping activity with the captured image URI
            uriFilePath?.let { startCropping(it) }
        }
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let { uri ->
            viewModel.avatarFileHolder = getFileFromUri(requireContext(), uri)
            binding.profileImageView.loadImage(uri.toString(), requireContext())
//            binding.proofOfAddressImageView.loadImage(uri.toString(),requireActivity())
//            viewModel.frontImageFile = getFileFromUri(requireActivity(), uri)
        }
    }

    private fun startCropping(uri: Uri) {
        val intent = CropImage.activity(uri)
            .setAspectRatio(1, 1) // Set the aspect ratio as desired
            .setGuidelines(CropImageView.Guidelines.ON)
            .getIntent(requireContext())

        cropImageLauncher.launch(intent)
    }

    // Register an activity result launcher for cropping
    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = CropImage.getActivityResult(result.data).uri

            // You now have the cropped image URI (croppedUri)
            // Use it as needed, e.g., display or save the cropped image
            viewModel.avatarFileHolder = getFileFromUri(requireContext(), croppedUri)
            binding.profileImageView.loadImage(croppedUri.toString(), requireContext())
        } else if (result.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            // Handle cropping error
            val error = CropImage.getActivityResult(result.data).error
            // Handle the error as needed
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
            ProfilePreviewFragment.REQUEST_IMAGE_CAPTURE
        )
    }

    override fun onRequestPermissionsResult (
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    )  = binding.run {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ProfilePreviewFragment.REQUEST_IMAGE_CAPTURE) {
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
        private const val APPROVED = "APPROVED"
        private const val DECLINED = "DECLINED"
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}