package com.lionscare.app.ui.badge.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.google.android.material.textfield.TextInputEditText
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.profile.request.BadgeRequest
import com.lionscare.app.databinding.FragmentUploadBadgeDocumentsBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.badge.viewmodel.BadgeViewModel
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.getFileFromUri
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UploadBadgeDocumentsFragment : Fragment() {

    private var _binding: FragmentUploadBadgeDocumentsBinding? = null
    private val binding get() = _binding!!
    private var focusedEditTextId: Int = 0
    private val activity by lazy { requireActivity() as VerifiedBadgeActivity }

    private val viewModel : BadgeViewModel by viewModels()

    private var loadingDialog: CommonDialog? = null

    private var selectedFile : String = "doc1"
    private var doc1: File? = null
    private var doc2: File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBadgeDocumentsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        setupClickListener()
    }

    private fun setupClickListener() = binding.run {
        supporting1EditText.doOnTextChanged { text, start, before, count ->
            supporting1TextInputLayout.error = ""
        }
        supporting2EditText.doOnTextChanged { text, start, before, count ->
            supporting2TextInputLayout.error = ""
        }

        accountTypeEditText.setText(activity.accountType)
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        supporting1EditText.setOnSingleClickListener {
            openFilePicker(supporting1EditText, DOC1)
        }
        supporting2EditText.setOnSingleClickListener {
            openFilePicker(supporting2EditText, DOC2)
        }
        continueButton.setOnSingleClickListener {
            //format account type for api request

            var accountTypeFormmatted = ""
            when(activity.accountType){
                getString(R.string.account_type_influencer_text) -> {
                    accountTypeFormmatted = "influencer"
                }
                getString(R.string.account_type_public_servant_text) -> {
                    accountTypeFormmatted = "public_servant"
                }
                getString(R.string.account_type_npo_text) -> {
                    accountTypeFormmatted = "non_government_organization"
                }
            }

//            viewModel.getBadgeStatus()
            if (supporting1EditText.text.toString().isNotEmpty() && supporting2EditText.text.toString().isNotEmpty()){
                viewModel.doRequestBadge(
                    BadgeRequest(
                        doc1 = doc1!!,
                        doc2 = doc2!!,
                        type = accountTypeFormmatted
                    )
                )
            } else {
                if (supporting1EditText.text.toString().isEmpty()){
                    supporting1TextInputLayout.error = "This field is required!"
                }
                if (supporting2EditText.text.toString().isEmpty()){
                    supporting2TextInputLayout.error = "This field is required!"
                }
            }

        }
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.badgeSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.SuccessBadgeRequest -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
                val intent = MainActivity.getIntent(requireActivity())
                startActivity(intent)
                requireActivity().finishAffinity()
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
                CommonLogger.sysLogE("here", viewState.errorCode)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFilePicker(editText: TextInputEditText, file: String) = binding.run {
        focusedEditTextId = editText.id
        selectedFile = file

        filePickerLauncher.launch(  arrayOf(
            "application/pdf",
            "application/msword",
            "application/ms-doc",
            "application/doc",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ))
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                val selectedFileName = getFileNameFromUri(uri)
                val focusedEditText =
                    requireActivity().findViewById<TextInputEditText>(focusedEditTextId)
                focusedEditText.setText(selectedFileName)

                    when (selectedFile) {
                        DOC1 -> {
                            doc1 = getFileFromUri(requireContext(), uri)
                        }

                        DOC2 -> {
                            doc2 = getFileFromUri(requireContext(), uri)
                        }
                    }
            }
        }



    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("_display_name"))
            }
        }
        return uri.lastPathSegment ?: ""
    }



    companion object {
        const val DOC1 = "doc1"
        const val DOC2 = "doc2"
    }
}