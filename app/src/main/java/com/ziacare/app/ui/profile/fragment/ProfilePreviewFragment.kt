package com.ziacare.app.ui.profile.fragment

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
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.yalantis.ucrop.UCrop
import com.ziacare.app.R
import com.ziacare.app.data.repositories.baseresponse.UserModel
import com.ziacare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.ziacare.app.databinding.FragmentProfilePreviewBinding
import com.ziacare.app.ui.profile.activity.ProfileActivity
import com.ziacare.app.ui.profile.viewmodel.ProfileViewModel
import com.ziacare.app.ui.profile.viewmodel.ProfileViewState
import com.ziacare.app.utils.calculateAge
import com.ziacare.app.utils.convertImageUriToFile
import com.ziacare.app.utils.loadAvatar
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.showPopupError
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
            is ProfileViewState.LoadingAvatar -> Unit // do not show anyloading

            is ProfileViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
                viewModel.userModel = viewState.userModel
                setView(viewState.userModel)
            }
            is ProfileViewState.SuccessUploadAvatar -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
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
        viewModel.getProfileDetails()
        activity.setTitlee(getString(R.string.lbl_my_profile))
        requireActivity().title = getString(R.string.lbl_my_profile)
        hideLoadingDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(userModel: UserModel?) = binding.run {
        if (viewModel.avatarURIHolder != null){
            profileImageView.loadAvatar(viewModel.avatarURIHolder.toString(), requireContext())
        }else{
            profileImageView.loadAvatar(userModel?.avatar?.thumb_path,requireContext())
        }

        nameTextView.text = userModel?.getFullName()
        dateOfBirthTextView.text = userModel?.birthdate?.date_only_ph?.ifEmpty { "Not set "}
        ageTextView.text = userModel?.birthdate?.date_only_ph
            ?.takeIf { it.isNotEmpty() }
            ?.calculateAge()
            ?.toString() ?: "Not set"

        if (userModel?.province_name?.isNotEmpty() == true){
            addressTextView.text =
                "${userModel.street_name}, ${userModel.brgy_name},\n${userModel.city_name}, ${userModel.province_name}"
        }else{
            addressTextView.text = "Address Unavailable"
        }

        emailEditText.setText(userModel?.email.orEmpty().ifEmpty {  getString(R.string.no_email_has_been_set_yet)})
        if (userModel?.email_verified == true){
            emailIsVerifiedTextView.text = getString(R.string.lbl_verified)
            emailIsVerifiedTextView.setBackgroundResource(R.drawable.bg_rounded_verified)
        }else{
            emailIsVerifiedTextView.text = getString(R.string.unverified)
            emailIsVerifiedTextView.setBackgroundResource(R.drawable.bg_rounded_pending)
        }

        phoneEditText.setText(userModel?.phone_number)
    }

    private fun setClickListeners() = binding.run  {
        emailEditText.setOnSingleClickListener {
            viewModel.email = emailEditText.text.toString()
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
            uriFilePath?.let {
                startCropping(it)
            }
        }
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let { uri ->
            // Launch the cropping activity with the ptured image URI
            uri.let {
                startCropping(it)
            }
        }
    }

    private fun startCropping(uri: Uri) {
        val timestamp = (System.currentTimeMillis() / 100).toString()
        viewModel.avatarURIHolder = Uri.fromFile(File(requireActivity().cacheDir, "$CROPPED_IMAGE_NAME$timestamp"))
        viewModel.avatarURIHolder?.let { destinationUri ->
            val crop = UCrop.of(uri,destinationUri )
                .withOptions(UCrop.Options().also {
                    it.setCircleDimmedLayer(true)
                    it.setToolbarColor(getColor(requireContext(), R.color.color_primary))
                    it.setStatusBarColor(getColor(requireContext(), R.color.color_primaryDark))
                    it.setToolbarWidgetColor(getColor(requireContext(), R.color.white))
                    it.setActiveControlsWidgetColor(getColor(requireContext(), R.color.color_primaryDark))
                })
//            crop.start(requireActivity())
            //same with crop.start
            cropImageLauncher.launch(crop.getIntent(requireActivity()))
        }
    }


    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val croppedUri = result.data?.let { UCrop.getOutput(it) }
            // Use it as needed, e.g., display or save the cropped image
            //just save it in viewmodel in case
            viewModel.avatarFileHolder = convertImageUriToFile(requireContext(), croppedUri!!)
            viewModel.uploadAvatar(ProfileAvatarRequest(viewModel.avatarFileHolder!!)) // call api

            viewModel.avatarURIHolder = croppedUri

        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            // Handle cropping error
            val error = result.data?.let { UCrop.getError(it) }
            // Handle the error as needed
            requireActivity().toastError(error?.message.toString(), CpmToast.SHORT_DURATION)
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
        private const val APPROVED = "APPROVED"
        private const val DECLINED = "DECLINED"
        private const val CROPPED_IMAGE_NAME = "lionscare_avatar01"
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}