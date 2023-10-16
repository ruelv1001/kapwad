package com.lionscare.app.ui.register.fragment

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.registration.request.OTPRequest
import com.lionscare.app.data.repositories.registration.request.RegistrationRequest
import com.lionscare.app.databinding.FragmentRegistrationPrimaryInfoBinding
import com.lionscare.app.ui.register.activity.RegisterActivity
import com.lionscare.app.ui.register.dialog.CountryDialog
import com.lionscare.app.ui.register.viewmodel.RegisterViewModel
import com.lionscare.app.ui.register.viewmodel.RegisterViewState
import com.lionscare.app.utils.dialog.ScannerDialog
import com.lionscare.app.utils.isPhoneNumberValid
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class RegisterPrimaryInfoFragment: Fragment() {
    private var _binding: FragmentRegistrationPrimaryInfoBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as RegisterActivity }
    private val viewModel: RegisterViewModel by viewModels()

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
        observeRegisterAccount()
        setClickListeners()
        setView()
        onResume()
    }

    private fun observeRegisterAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: RegisterViewState) {
        when (viewState) {
            is RegisterViewState.Loading -> showLoadingDialog(R.string.loading)
            is RegisterViewState.Success -> {
                hideLoadingDialog()
                findNavController().navigate(RegisterPrimaryInfoFragmentDirections.actionNavigationOtp())
            }
            is RegisterViewState.SuccessScanQR -> {
                hideLoadingDialog()
                activity.onBoardingData = viewState.onboardingScanQRResponse.data!!
                findNavController().navigate(RegisterPrimaryInfoFragmentDirections.actionNavigationOnboardingPrimaryInfo())
            }
            is RegisterViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            is RegisterViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }

            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.firstname?.get(0)?.isNotEmpty() == true) binding.firstNameTextInputLayout.error = errorsData.firstname?.get(0)
        if (errorsData.lastname?.get(0)?.isNotEmpty() == true) binding.lastNameTextInputLayout.error = errorsData.lastname?.get(0)
        if (errorsData.middlename?.get(0)?.isNotEmpty() == true) binding.middleNameTextInputLayout.error = errorsData.middlename?.get(0)
        if (errorsData.password?.get(0)?.isNotEmpty() == true) binding.passwordTextInputLayout.error = errorsData.password?.get(0)
        if (errorsData.password_confirmation?.get(0)?.isNotEmpty() == true) binding.confirmPasswordTextInputLayout.error = errorsData.password_confirmation?.get(0)
        if (errorsData.phone_number?.get(0)?.isNotEmpty() == true) binding.contactTextInputLayout.error = errorsData.phone_number?.get(0)
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_primary_info))
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as RegisterActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as RegisterActivity).hideLoadingDialog()
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
        middleNameEditText.doOnTextChanged {
                text, start, before, count ->
            middleNameTextInputLayout.error = ""
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
            val phoneNumber = "${viewModel.countryCode}${contactEditText.text.toString()}"
            if (isPhoneNumberValid(phoneNumber, viewModel.countryIso)){
                val data = RegistrationRequest()
                data.firstname = firstNameEditText.text.toString()
                data.middlename = middleNameEditText.text.toString()
                data.lastname = lastNameEditText.text.toString()
                data.phone_number = phoneNumber
                data.phone_number_country_code = viewModel.countryCode
                data.password = passwordEditText.text.toString()
                data.password_confirmation = confirmPasswordEditText.text.toString()
                activity.requestModel = data
                val otpRequest = OTPRequest()
                otpRequest.phone_number = phoneNumber
                activity.otpModel = otpRequest
                viewModel.doPreReg(data)
            }else{
                contactTextInputLayout.error = getString(R.string.phone_number_is_invalid)
            }
        }

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // remove the error  on focus so toggle password visibility would show
                passwordTextInputLayout.error = null
            }
        }
        passwordEditText.setOnSingleClickListener {
            // remove the error  on click so toggle password visibility would show
            passwordTextInputLayout.error = null
        }

        confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // remove the error  on focus so toggle password visibility would show
                confirmPasswordTextInputLayout.error = null
            }
        }
        confirmPasswordEditText.setOnSingleClickListener {
            // remove the error  on click so toggle password visibility would show
            confirmPasswordTextInputLayout.error = null
        }

        countryCodeTextView.setOnSingleClickListener {
            CountryDialog.newInstance(object : CountryDialog.AddressCallBack {
                override fun onAddressClicked(countryName: String, code: String, phone_code: String) {
                    countryCodeTextView.text = phone_code
                    viewModel.countryCode = phone_code //ex. +63
                    viewModel.countryIso = code //ex. PH, US, VN, etc.
                }
            }, displayCountryCode = true).show(childFragmentManager, CountryDialog.TAG)
        }

        activity.getScanQRImageView().setOnSingleClickListener {
            ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
                override fun onScannerSuccess(qrValue: String) {
                    val jsonObject: JSONObject
                    val res = qrValue.replace("\\", "")
                    try {
                        jsonObject = JSONObject(res)
                        val type = jsonObject.getString("type")
                        val value = jsonObject.getString("value")
                        activity.code = value
                        viewModel.doScanQR(value)
                    } catch (e: JSONException) {
                        requireActivity().toastError(getString(R.string.invalid_qr_code_msg), CpmToast.SHORT_DURATION)
                        e.printStackTrace()
                    }
                }
            }, "Scan QR").show(childFragmentManager, ScannerDialog.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}