package dswd.ziacare.app.ui.register.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dswd.ziacare.app.R
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.profile.request.UpdateInfoRequest
import dswd.ziacare.app.databinding.FragmentRegistrationCompleteProfileBinding
import dswd.ziacare.app.ui.main.activity.MainActivity
import dswd.ziacare.app.ui.register.activity.RegisterActivity
import dswd.ziacare.app.ui.register.dialog.BrgyDialog
import dswd.ziacare.app.ui.register.dialog.CityDialog
import dswd.ziacare.app.ui.register.dialog.ProvinceDialog
import dswd.ziacare.app.ui.register.viewmodel.RegisterViewModel
import dswd.ziacare.app.ui.register.viewmodel.RegisterViewState
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OnboardingCompleteProfileFragment: Fragment() {
    private var _binding: FragmentRegistrationCompleteProfileBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as RegisterActivity }
    private var reference: String = ""
    private var cityCode: String = ""
    private var brgyCode: String = ""
    private val viewModel: RegisterViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationCompleteProfileBinding.inflate(
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
            is RegisterViewState.SuccessProfileUpdate -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
                val intent = MainActivity.getIntent(requireActivity())
                startActivity(intent)
                requireActivity().finishAffinity()
            }
            is RegisterViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is RegisterViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }

            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.email?.get(0)?.isNotEmpty() == true) binding.emailTextInputLayout.error =errorsData.email?.get(0)
        if (errorsData.birthdate?.get(0)?.isNotEmpty() == true) binding.birthdateTextInputLayout.error = errorsData.birthdate?.get(0)
        if (errorsData.province_name?.get(0)?.isNotEmpty() == true ) binding.provinceTextInputLayout.error = errorsData.province_name?.get(0)
        if (errorsData.city_name?.get(0)?.isNotEmpty() == true) binding.cityTextInputLayout.error = errorsData.city_name?.get(0)
        if (errorsData.brgy_name?.get(0)?.isNotEmpty() == true ) binding.barangayTextInputLayout.error = errorsData.brgy_name?.get(0)
        if (errorsData.street_name?.get(0)?.isNotEmpty() == true) binding.streetTextInputLayout.error = errorsData.street_name?.get(0)
        if (errorsData.zipcode?.get(0)?.isNotEmpty() == true) binding.zipcodeTextInputLayout.error = errorsData.zipcode?.get(0)
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_reg_complete_profile))
    }

    private fun setView() = binding.run {

        reference = activity.onBoardingUserModel.province_sku.toString()
        cityCode = activity.onBoardingUserModel.city_code.toString()
        brgyCode = activity.onBoardingUserModel.brgy_code.toString()

        birthdateEditText.setText(activity.onBoardingUserModel.birthdate?.date_only)
        provinceEditText.setText(activity.onBoardingUserModel.province_name)
        cityEditText.setText(activity.onBoardingUserModel.city_name)
        barangayEditText.setText(activity.onBoardingUserModel.brgy_name)
        zipcodeEditText.setText(activity.onBoardingUserModel.zipcode)
        streetEditText.setText(activity.onBoardingUserModel.street_name)

        emailEditText.doOnTextChanged { text, start, before, count ->
            emailTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        birthdateEditText.doOnTextChanged { text, start, before, count ->
            birthdateTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        provinceEditText.doOnTextChanged { text, start, before, count ->
            provinceTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        cityEditText.doOnTextChanged { text, start, before, count ->
            cityTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        barangayEditText.doOnTextChanged { text, start, before, count ->
            barangayTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        streetEditText.doOnTextChanged { text, start, before, count ->
            streetTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        zipcodeEditText.doOnTextChanged { text, start, before, count ->
            zipcodeTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        cityEditText.isClickable = false
        barangayEditText.isClickable = false
    }


    private fun setClickListeners() = binding.run {
        //remove backbbutton
        activity.removeBackButton()

        //remove hardware back button
        requireActivity().onBackPressedDispatcher.addCallback {
            null
        }
        birthdateEditText.setOnSingleClickListener {
            val constraintsBuilder=CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()


            datePicker.addOnPositiveButtonClickListener {
                // Respond to positive button click.
                val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale("en", "PH")).format(
                    Date(it)
                )
                birthdateEditText.setText(formattedDate)
            }
            datePicker.addOnNegativeButtonClickListener {
                // Respond to negative button click.
                datePicker.dismiss()
            }
            datePicker.addOnCancelListener {
                // Respond to cancel button click.
                datePicker.dismiss()
            }
            datePicker.addOnDismissListener {
                // Respond to dismiss events.
                datePicker.dismiss()
            }
            datePicker.show(childFragmentManager, "ProfileUpdateFragment")
        }

        provinceEditText.setOnSingleClickListener {
            ProvinceDialog.newInstance(object : ProvinceDialog.AddressCallBack {
                override fun onAddressClicked(
                    provinceName: String,
                    provinceSku: String,
                    referencea: String,
                ) {
                    provinceEditText.setText(provinceName)
                    reference = referencea

                    cityEditText.setText("")
                    barangayEditText.setText("")
                    zipcodeEditText.setText("")

                    setClickable()
                }
            }).show(childFragmentManager, ProvinceDialog.TAG)
        }

        cityEditText.setOnSingleClickListener {
            if(provinceEditText.text.toString().isNotEmpty()) {
                CityDialog.newInstance(object : CityDialog.AddressCallBack {
                    override fun onAddressClicked(
                        cityName: String,
                        citySku: String,
                        zipcode: String
                    ) {
                        cityEditText.setText(cityName)
                        cityCode = citySku

                        barangayEditText.setText("")
                        zipcodeEditText.setText("")

                        setClickable()
                    }
                }, reference).show(childFragmentManager, CityDialog.TAG)
            }

        }

        barangayEditText.setOnSingleClickListener {
            if(cityEditText.text.toString().isNotEmpty()) {
                BrgyDialog.newInstance(object : BrgyDialog.AddressCallBack {
                    override fun onAddressClicked(
                        cityName: String,
                        citySku: String,
                        zipCode: String
                    ) {
                        brgyCode = citySku
                        barangayEditText.setText(cityName)
                        zipcodeEditText.setText(zipCode)
                        setClickable()
                    }
                }, cityCode).show(childFragmentManager, BrgyDialog.TAG)
            }
        }

        continueButton.setOnSingleClickListener {
            val request = UpdateInfoRequest(
                province_sku = reference,
                province_name = provinceEditText.text.toString(),
                city_sku = cityCode,
                city_name = cityEditText.text.toString(),
                brgy_sku = brgyCode,
                brgy_name = barangayEditText.text.toString(),
                street_name = streetEditText.text.toString(),
                zipcode = zipcodeEditText.text.toString(),
                firstname = activity.onBoardingUserModel.firstname.orEmpty(),
                lastname = activity.onBoardingUserModel.lastname.orEmpty(),
                middlename = activity.onBoardingUserModel.middlename.orEmpty(),
                email = emailEditText.text.toString(),
                birthdate = birthdateEditText.text.toString()
            )

            viewModel.doUpdateProfile(request)
        }
    }




    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as RegisterActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as RegisterActivity).hideLoadingDialog()
    }

    private fun setClickable() = binding.run {
        cityEditText.isClickable = provinceEditText.text.toString().isNotEmpty()
        barangayEditText.isClickable= cityEditText.text.toString().isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}