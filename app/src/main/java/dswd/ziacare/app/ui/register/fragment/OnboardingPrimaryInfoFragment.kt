package dswd.ziacare.app.ui.register.fragment

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
import dswd.ziacare.app.R
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.registration.request.OTPRequest
import dswd.ziacare.app.data.repositories.registration.request.RegistrationRequest
import dswd.ziacare.app.data.repositories.registration.response.OnboardingScanQRData
import dswd.ziacare.app.databinding.FragmentRegistrationPrimaryInfoBinding
import dswd.ziacare.app.ui.register.activity.RegisterActivity
import dswd.ziacare.app.ui.register.dialog.CountryDialog
import dswd.ziacare.app.ui.register.viewmodel.RegisterViewModel
import dswd.ziacare.app.ui.register.viewmodel.RegisterViewState
import dswd.ziacare.app.utils.dialog.ScannerDialog
import dswd.ziacare.app.utils.isPhoneNumberValid
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class OnboardingPrimaryInfoFragment: Fragment() {
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
        setDetails(activity.onBoardingData)
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
                activity.password = binding.passwordEditText.text.toString()
                activity.passwordConfirm = binding.confirmPasswordEditText.text.toString()
                findNavController().navigate(OnboardingPrimaryInfoFragmentDirections.actionNavigationOnboardingOtp())
            }
            is RegisterViewState.SuccessScanQR -> {
                hideLoadingDialog()
                viewState.onboardingScanQRResponse.data?.let { setDetails(it) }
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

    private fun setDetails(data : OnboardingScanQRData) = binding.run {
        firstNameEditText.setText(data.firstname)
        firstNameEditText.isEnabled = false

        middleNameEditText.setText(data.middlename)
        middleNameEditText.isEnabled = false

        lastNameEditText.setText(data.lastname)
        lastNameEditText.isEnabled = false

        contactEditText.setText(data.phone_number?.replace(data.phone_country_code.toString(),""))
        contactEditText.isEnabled = false

        countryCodeTextView.text = data.phone_country_code
        countryCodeTextView.isEnabled = false
    }

    private fun setClickListeners() = binding.run {
        continueButton.setOnSingleClickListener {
           viewModel.doPrevalidatePassword(password = passwordEditText.text.toString(),
               passwordConfirmation = confirmPasswordEditText.text.toString())
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

        activity.getScanQRImageView().setOnSingleClickListener {
            ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
                override fun onScannerSuccess(qrValue: String) {
                    val jsonObject: JSONObject
                    val res = qrValue.replace("\\", "")
                    try {
                        jsonObject = JSONObject(res)
                        val type = jsonObject.getString("type")
                        val value = jsonObject.getString("value")
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