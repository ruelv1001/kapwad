package com.lionscare.app.ui.register.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentRegistrationPrimaryInfoBinding
import com.lionscare.app.ui.register.activity.RegisterActivity
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterPrimaryInfoFragment: Fragment() {
    private var _binding: FragmentRegistrationPrimaryInfoBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as RegisterActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationPrimaryInfoBinding.inflate(
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
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitle(R.string.lbl_primary_info)
    }

    private fun setView() = binding.run{
        firstNameEditText.doOnTextChanged {
                text, start, before, count ->
            firstNameTextInputLayout.error = ""
        }
        lastNameEditText.doOnTextChanged {
                text, start, before, count ->
            lastNameTextInputLayout.error = ""
        }
        contactEditText.doOnTextChanged {
                text, start, before, count ->
            contactTextInputLayout.error = ""
        }
        passwordEditText.doOnTextChanged {
                text, start, before, count ->
            passwordTextInputLayout.error = ""
        }
        confirmPasswordEditText.doOnTextChanged {
                text, start, before, count ->
            confirmPasswordTextInputLayout.error = ""
        }
    }

    private fun setClickListeners() = binding.run {
        continueButton.setOnSingleClickListener {
            if (firstNameEditText.text.toString().isEmpty()){
                firstNameTextInputLayout.error = "Field is required"
            }
            if (lastNameEditText.text.toString().isEmpty()){
                lastNameTextInputLayout.error = "Field is required"
            }
            if (contactEditText.text.toString().isEmpty()){
                contactTextInputLayout.error = "Field is required"
            }
            if (passwordEditText.text.toString().isEmpty()){
                passwordTextInputLayout.error = "Field is required"
            }
            if (confirmPasswordEditText.text.toString().isEmpty()){
                confirmPasswordTextInputLayout.error = "Field is required"
            }

            if (firstNameEditText.text.toString().isNotEmpty() &&
                lastNameEditText.text.toString().isNotEmpty() &&
                contactEditText.text.toString().isNotEmpty() &&
                passwordEditText.text.toString().isNotEmpty() &&
                confirmPasswordEditText.text.toString().isNotEmpty()){
                findNavController().navigate(RegisterPrimaryInfoFragmentDirections.actionNavigationOtp())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}