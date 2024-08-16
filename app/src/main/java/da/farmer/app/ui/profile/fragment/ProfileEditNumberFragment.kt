package da.farmer.app.ui.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.google.android.material.snackbar.Snackbar
import da.farmer.app.R
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import da.farmer.app.databinding.FragmentProfileEditNumberBinding
import da.farmer.app.databinding.FragmentProfilePreviewBinding
import da.farmer.app.ui.profile.activity.ProfileActivity
import da.farmer.app.ui.profile.viewmodel.ProfileViewModel
import da.farmer.app.ui.profile.viewmodel.ProfileViewState
import da.farmer.app.ui.register.dialog.CountryDialog
import da.farmer.app.utils.isPhoneNumberValid
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditNumberFragment : Fragment() {
    private var _binding: FragmentProfileEditNumberBinding? = null
    private val binding get() = _binding!!

    private val activity by lazy { requireActivity() as ProfileActivity }
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileEditNumberBinding.inflate(
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
        activity.setTitlee(getString(R.string.update_contact_number))
        viewModel.getProfileDetails()
    }

    private fun setClickListeners() = binding.run {
        confirmButton.setOnSingleClickListener {
            val phoneNumber = "${viewModel.countryCode}${phoneNumberEditText.text.toString()}"
            if (isPhoneNumberValid(phoneNumber, viewModel.countryIso)) {
                viewModel.changePhoneNumber(
                    UpdatePhoneNumberRequest(
                        phone_number = phoneNumber,
                        phone_number_country_code = viewModel.countryCode
                    )
                )
            } else {
                phoneNumberTextInputLayout.error = getString(R.string.phone_number_is_invalid)
            }
        }

        countryCodeTextView.setOnSingleClickListener {
            CountryDialog.newInstance(object : CountryDialog.AddressCallBack {
                override fun onAddressClicked(countryName: String, code: String, phone_code: String) {
                    countryCodeTextView.text = phone_code
                    viewModel.countryCode = phone_code
                    viewModel.countryIso = code
                }
            }, displayCountryCode = true).show(childFragmentManager, CountryDialog.TAG)
        }
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.SuccessUpdatePhoneNumber -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(
                    viewState.response.msg.toString(),
                    CpmToast.LONG_DURATION
                )
                viewModel.phoneNumber = "${viewModel.countryCode}${binding.phoneNumberEditText.text.toString()}"
                findNavController().navigate(ProfileEditNumberFragmentDirections.actionProfileEditNumberFragmentToProfileOTPFragment())
            }

            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is ProfileViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }

            else -> hideLoadingDialog()
        }
    }


    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.otp?.get(0)?.isNotEmpty() == true) {
            requireActivity().toastError(errorsData.otp?.get(0).toString(), CpmToast.SHORT_DURATION)
        }
        if (errorsData.phone_number?.get(0)?.isNotEmpty() == true) {
            requireActivity().toastError(
                errorsData.phone_number?.get(0).toString(),
                CpmToast.SHORT_DURATION
            )
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
