package com.ziacare.app.ui.group.fragment

import android.Manifest
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
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.ziacare.app.R
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.profile.request.ProfileAvatarRequest
import com.ziacare.app.databinding.FragmentGroupManageBinding
import com.ziacare.app.ui.group.activity.GroupActivity
import com.ziacare.app.ui.group.dialog.RemoveConfirmationDialog
import com.ziacare.app.ui.group.viewmodel.GroupViewModel
import com.ziacare.app.ui.group.viewmodel.GroupViewState
import com.ziacare.app.ui.group.viewmodel.MemberViewModel
import com.ziacare.app.ui.group.viewmodel.MemberViewState
import com.ziacare.app.ui.main.activity.MainActivity
import com.ziacare.app.ui.profile.activity.GroupNotificationsActivity
import com.ziacare.app.ui.profile.fragment.ProfilePreviewFragment
import com.ziacare.app.utils.convertImageUriToFile
import com.ziacare.app.utils.loadAvatar
import com.ziacare.app.utils.loadGroupAvatar
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.showPopupError
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class GroupManageFragment : Fragment() {
    private var _binding: FragmentGroupManageBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel : MemberViewModel by viewModels()
    private val groupViewModel: GroupViewModel by viewModels()

    private var uriFilePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupManageBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setView()
        observeMember()
        observeGroup()
        setDetails()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_manage_group))
    }

    private fun setView() = binding.run {
//        firstNameEditText.doOnTextChanged {
//                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
//        }
    }

    private fun setDetails() = binding.run {
        titleTextView.text = activity.groupDetails?.name
        referenceTextView.text = activity.groupDetails?.qrcode
        imageView.loadGroupAvatar(activity.groupDetails?.avatar?.thumb_path)
        membersTextView.text = context?.resources?.getQuantityString(
            R.plurals.member_plural,
            activity.groupDetails?.member_count?: 0,
            activity.groupDetails?.member_count?: 0
        )
        membershipNotifRelativeLayout.isVisible = activity.groupDetails?.pending_requests_count !=0
        membershipCountTextView.text = activity.groupDetails?.pending_requests_count.toString()

        if (activity.groupDetails?.type.equals("organization")) {
            typeFamTextView.isVisible = false
            typeComTextView.isVisible = false
            typeOrgTextView.isVisible = true
        } else if (activity.groupDetails?.type.equals("community")) {
            typeFamTextView.isVisible = false
            typeOrgTextView.isVisible = false
            typeComTextView.isVisible = true
        }else{
            typeComTextView.isVisible = false
            typeOrgTextView.isVisible = false
            typeFamTextView.isVisible = true
        }

        editGroupImageView.isVisible = activity.groupDetails?.is_admin == true
        transactionLinearLayout.isVisible = activity.groupDetails?.is_admin == true
    }

    private fun setClickListeners() = binding.run {
        assistanceLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupAssistance())
        }
        membershipLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupMembership())
        }
        statisticsLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupStatistics())
        }
        transactionLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupTransaction())
        }
        editGroupImageView.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupUpdate())
        }
        leaveGroupLinearLayout.setOnSingleClickListener {
            RemoveConfirmationDialog.newInstance(
                object : RemoveConfirmationDialog.ConfirmationCallback {
                    override fun onConfirm(id: String) {
                        viewModel.leaveGroup(id)
                    }
                },
                title = "Are you sure you want to leave ${activity.groupDetails?.name}?",
                groupId = activity.groupDetails?.id
            ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
        }
        imageView.setOnSingleClickListener {
            if(activity.groupDetails?.is_admin == true)
                openMediaOptionPicker()
        }
        notifLinearLayout.setOnSingleClickListener {
            val intent = GroupNotificationsActivity.getIntent(requireActivity(),activity.groupDetails?.id.toString())
            startActivity(intent)
        }
    }

    private fun observeMember() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                memberHandleViewState(viewState)
            }
        }
    }

    private fun memberHandleViewState(viewState: MemberViewState) {
        when (viewState) {
            MemberViewState.Loading -> showLoadingDialog(R.string.loading)
            is MemberViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is MemberViewState.SuccessLeaveGroup -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
                val intent = MainActivity.getIntent(activity)
                startActivity(intent)
                activity.finishAffinity()
            }
            else -> Unit
        }
    }

    private fun observeGroup() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                groupViewModel.groupSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: GroupViewState) {
        when (viewState) {
            is GroupViewState.Loading -> showLoadingDialog(R.string.loading)
            is GroupViewState.InputError -> {
                hideLoadingDialog()
            }
            is GroupViewState.SuccessUploadAvatar -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
                activity.onBackPressedDispatcher.onBackPressed()
            }
            is GroupViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as GroupActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as GroupActivity).hideLoadingDialog()
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
        mBuilder.setNeutralButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
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
            uriFilePath?.let {
                startCropping(it)
            }
        }
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let { uri ->
            startCropping(uri)
        }
    }

    private fun startCropping(uri: Uri) {
        val timestamp = (System.currentTimeMillis() / 100).toString()
        groupViewModel.avatarURIHolder = Uri.fromFile(File(requireActivity().cacheDir, "${CROPPED_IMAGE_NAME}$timestamp"))
        groupViewModel.avatarURIHolder?.let { destinationUri ->
            val crop = UCrop.of(uri,destinationUri )
                .withOptions(UCrop.Options().also {
                    it.setCircleDimmedLayer(true)
                    it.setToolbarColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_primary
                        )
                    )
                    it.setStatusBarColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_primaryDark
                        )
                    )
                    it.setToolbarWidgetColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    it.setActiveControlsWidgetColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_primaryDark
                        )
                    )
                })
            cropImageLauncher.launch(crop.getIntent(requireActivity()))
        }
    }


    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val croppedUri = result.data?.let { UCrop.getOutput(it) }
            groupViewModel.avatarFileHolder = convertImageUriToFile(requireContext(), croppedUri!!)
            groupViewModel.uploadGroupAvatar(
                groupViewModel.avatarFileHolder!!,
                activity.groupDetails?.id.toString()
            )

            groupViewModel.avatarURIHolder = croppedUri

        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val error = result.data?.let { UCrop.getError(it) }
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
        private const val START_MANAGE = "START_MANAGE"
        private const val CROPPED_IMAGE_NAME = "groupAvatar"
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}