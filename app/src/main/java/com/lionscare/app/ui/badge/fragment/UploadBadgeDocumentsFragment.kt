package com.lionscare.app.ui.badge.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.lionscare.app.databinding.FragmentUploadBadgeDocumentsBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.setOnSingleClickListener

class UploadBadgeDocumentsFragment : Fragment() {

    private var _binding: FragmentUploadBadgeDocumentsBinding? = null
    private val binding get() = _binding!!
    private var focusedEditTextId: Int = 0
    private val activity by lazy { requireActivity() as VerifiedBadgeActivity }

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
        setupClickListener()
    }

    private fun setupClickListener() = binding.run {
        accountTypeEditText.setText(activity.accountType)
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        supporting1EditText.setOnSingleClickListener {
            openFilePicker(supporting1EditText)
        }
        supporting2EditText.setOnSingleClickListener {
            openFilePicker(supporting2EditText)
        }
        continueButton.setOnSingleClickListener {
            val intent = MainActivity.getIntent(requireActivity())
            startActivity(intent)
            requireActivity().finishAffinity()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFilePicker(editText: TextInputEditText) {
        focusedEditTextId = editText.id
        editText.clearFocus()
        filePickerLauncher.launch("*/*")
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val selectedFileName = getFileNameFromUri(uri)
                val focusedEditText = requireActivity().findViewById<TextInputEditText>(focusedEditTextId)
                focusedEditText.setText(selectedFileName)
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

}