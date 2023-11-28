package com.ziacare.app.ui.profile.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.ziacare.app.R
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.baseresponse.UserModel
import com.ziacare.app.data.repositories.profile.request.UpdateInfoRequest
import com.ziacare.app.databinding.FragmentProfileUpdateBinding
import com.ziacare.app.ui.main.activity.MainActivity
import com.ziacare.app.ui.profile.activity.ProfileActivity
import com.ziacare.app.ui.profile.dialog.ProfileConfirmationDialog
import com.ziacare.app.ui.profile.viewmodel.ProfileViewModel
import com.ziacare.app.ui.profile.viewmodel.ProfileViewState
import com.ziacare.app.ui.register.dialog.BrgyDialog
import com.ziacare.app.ui.register.dialog.CityDialog
import com.ziacare.app.ui.register.dialog.ProvinceDialog
import com.ziacare.app.ui.register.dialog.RegisterSuccessDialog
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ProfileUpdateFragment: Fragment(), ProfileConfirmationDialog.ProfileSaveDialogCallBack {
    private var _binding: FragmentProfileUpdateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileUpdateBinding.inflate(
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
        onResume()
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
            is ProfileViewState.Success -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                if(activity.isFromLogin){
                    val intent = MainActivity.getIntent(activity)
                    startActivity(intent)
                    activity.finishAffinity()
                }else{
                    findNavController().popBackStack()
                }
            }
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
                handleInputError(viewState.errorData?: ErrorsData())
            }

            else -> hideLoadingDialog()
        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.firstname?.get(0)?.isNotEmpty() == true) binding.firstNameTextInputLayout.error = errorsData.firstname?.get(0)
        if (errorsData.lastname?.get(0)?.isNotEmpty() == true) binding.lastNameTextInputLayout.error = errorsData.lastname?.get(0)
        if (errorsData.birthdate?.get(0)?.isNotEmpty() == true) binding.birthdateTextInputLayout.error = errorsData.birthdate?.get(0)
        if (errorsData.zipcode?.get(0)?.isNotEmpty() == true) binding.zipcodeTextInputLayout.error = errorsData.zipcode?.get(0)
        if (errorsData.province_name?.get(0)?.isNotEmpty() == true) binding.provinceTextInputLayout.error = errorsData.province_name?.get(0)
        if (errorsData.city_name?.get(0)?.isNotEmpty() == true) binding.cityTextInputLayout.error = errorsData.city_name?.get(0)
        if (errorsData.brgy_name?.get(0)?.isNotEmpty() == true) binding.barangayTextInputLayout.error = errorsData.brgy_name?.get(0)
        if (errorsData.street_name?.get(0)?.isNotEmpty() == true) binding.streetTextInputLayout.error = errorsData.street_name?.get(0)
    }


    override fun onResume() {
        super.onResume()
        if(activity.isFromLogin){
            activity.setTitlee(getString(R.string.lbl_reg_complete_profile))
            viewModel.getProfileDetails()

        }else{
            activity.setTitlee(getString(R.string.lbl_update_profile))
            hideLoadingDialog()
            setView(viewModel.userModel)
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(userModel: UserModel?) = binding.run{
        firstNameEditText.doOnTextChanged { text, start, before, count ->
            firstNameTextInputLayout.isErrorEnabled = false //to not take up space after removing error
        }
        middleNameEditText.doOnTextChanged { text, start, before, count ->
            middleNameTextInputLayout.isErrorEnabled = false
        }
        lastNameEditText.doOnTextChanged { text, start, before, count ->
            lastNameTextInputLayout.isErrorEnabled = false
        }

        birthdateEditText.doOnTextChanged { text, start, before, count ->
            birthdateTextInputLayout.isErrorEnabled = false
        }
        provinceEditText.doOnTextChanged {
                text, start, before, count ->
            provinceTextInputLayout.isErrorEnabled = false
        }
        cityEditText.doOnTextChanged {
                text, start, before, count ->
            cityTextInputLayout.isErrorEnabled = false
        }
        barangayEditText.doOnTextChanged {
                text, start, before, count ->
            barangayTextInputLayout.isErrorEnabled = false
        }
        streetEditText.doOnTextChanged {
                text, start, before, count ->
            streetTextInputLayout.isErrorEnabled = false
        }
        zipcodeEditText.doOnTextChanged { text, start, before, count ->
            zipcodeTextInputLayout.isErrorEnabled = false
        }


        firstNameEditText.setText(userModel?.firstname)
        middleNameEditText.setText(userModel?.middlename)
        lastNameEditText.setText(userModel?.lastname)
        provinceEditText.setText(userModel?.province_name)
        cityEditText.setText(userModel?.city_name)
        barangayEditText.setText(userModel?.brgy_name)
        streetEditText.setText(userModel?.street_name)
        zipcodeEditText.setText(userModel?.zipcode)
        birthdateEditText.setText(userModel?.birthdate?.date_only_ph)
    }

    private fun setClickListeners() = binding.run {
        birthdateEditText.setOnSingleClickListener {
            val constraintsBuilder=CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(constraintsBuilder.build())
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText("Select date")
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
                    reference: String,
                ) {
                    provinceEditText.setText(provinceName)
                    viewModel.userModel?.province_sku = reference
                    cityEditText.setText("")
                    barangayEditText.setText("")
                    zipcodeEditText.setText("")
                }
            }).show(childFragmentManager, ProvinceDialog.TAG)
        }

        cityEditText.setOnSingleClickListener{
            if(provinceEditText.text.toString().isNotEmpty()) {
                CityDialog.newInstance(object : CityDialog.AddressCallBack {
                    override fun onAddressClicked(
                        cityName: String,
                        citySku: String,
                        zipcode: String
                    ) {
                        cityEditText.setText(cityName)
                        viewModel.userModel?.city_code = citySku

                        barangayEditText.setText("")
                        zipcodeEditText.setText("")
                    }
                },  reference = viewModel.userModel?.province_sku.toString()).show(childFragmentManager, CityDialog.TAG)
            }
        }

        barangayEditText.setOnSingleClickListener {
            if(cityEditText.text.toString().isNotEmpty()){
                BrgyDialog.newInstance(object : BrgyDialog.AddressCallBack {
                    override fun onAddressClicked(
                        brgyName: String,
                        brgySku: String,
                        zipCode: String
                    ) {

//                    brgyCode = citySku
                        viewModel.userModel?.brgy_code = brgySku
                        viewModel.userModel?.zipcode = zipCode
//                    zipCode = zipCodes
                        barangayEditText.setText(brgyName)
                        zipcodeEditText.setText(zipCode)
                    }
                }, viewModel.userModel?.city_code.toString()).show(childFragmentManager, BrgyDialog.TAG)
            }
        }

        saveButton.setOnSingleClickListener {
                ProfileConfirmationDialog.newInstance(this@ProfileUpdateFragment)
                    .show(childFragmentManager, RegisterSuccessDialog.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMyAccountClicked(dialog: ProfileConfirmationDialog)=binding.run {
        dialog.dismiss()
        val request = UpdateInfoRequest (
            province_sku =  viewModel.userModel?.province_sku.orEmpty(),
            province_name = provinceEditText.text.toString(),
            city_sku =viewModel.userModel?.city_code.orEmpty(),
            city_name = cityEditText.text.toString(),
            brgy_sku = viewModel.userModel?.brgy_code.orEmpty(),
            brgy_name = barangayEditText.text.toString(),
            street_name =  streetEditText.text.toString(),
            zipcode = zipcodeEditText.text.toString(),
            firstname = firstNameEditText.text.toString(),
            lastname = lastNameEditText.text.toString(),
            middlename = middleNameEditText.text.toString(),
            email = null,
            birthdate = birthdateEditText.text.toString()
        )
        viewModel.doUpdateProfile(request)
    }
}