package com.lionscare.app.ui.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.data.repositories.profile.response.LOVData
import com.lionscare.app.databinding.FragmentProfileUpdateBinding
import com.lionscare.app.ui.register.dialog.BrgyDialog
import com.lionscare.app.ui.register.dialog.CityDialog
import com.lionscare.app.ui.register.dialog.ProvinceDialog
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.ui.profile.dialog.ProfileConfirmationDialog
import com.lionscare.app.ui.profile.viewmodel.ProfileViewModel
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.ui.register.dialog.ClusterDialog
import com.lionscare.app.ui.register.dialog.RegionDialog
import com.lionscare.app.ui.register.dialog.ZoneDialog
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
                Toast.makeText(requireActivity(),viewState.message,Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            is ProfileViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
//                setView(viewState.userModel)
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
        activity.setTitlee(getString(R.string.lbl_update_profile))
        hideLoadingDialog()
        setView(viewModel.userModel)
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    private fun setView(userModel: UserModel?) = binding.run{
        provinceEditText.doOnTextChanged {
                text, start, before, count ->
            provinceTextInputLayout.error = ""
        }
        cityEditText.doOnTextChanged {
                text, start, before, count ->
            cityTextInputLayout.error = ""
        }
        barangayEditText.doOnTextChanged {
                text, start, before, count ->
            barangayTextInputLayout.error = ""
        }
        streetEditText.doOnTextChanged {
                text, start, before, count ->
            streetTextInputLayout.error = ""
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
        setClickable()
    }

    private fun setClickListeners() = binding.run {
        birthdateEditText.setOnSingleClickListener {
            val constraintsBuilder=CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setCalendarConstraints(constraintsBuilder.build())
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
            datePicker.show(childFragmentManager, "ProfileUpdateFragment");
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
                    setClickable()
                }
            }).show(childFragmentManager, ProvinceDialog.TAG)
        }

        cityEditText.setOnSingleClickListener {
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
                    setClickable()
                }
            },  reference = viewModel.userModel?.province_sku.toString()).show(childFragmentManager, CityDialog.TAG)
        }

        barangayEditText.setOnSingleClickListener {
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
                    setClickable()
                }
            }, viewModel.userModel?.city_code.toString()).show(childFragmentManager, BrgyDialog.TAG)
        }

//        ===================== LIONS CLUB INTL
        regionEditText.setOnClickListener {
            RegionDialog.newInstance(object : RegionDialog.RegionCallBack {
                override fun onRegionClicked(
                    data: LOVData
                ) {
                    regionEditText.setText(data.value)
                    viewModel.userModel?.lc_region_id = data.code

                    zoneEditText.setText("")
                    clusterEditText.setText("")
                    setClickableLionsClub()
                }
            }).show(childFragmentManager, RegionDialog.TAG)
        }

        zoneEditText.setOnSingleClickListener {
            CommonLogger.instance.sysLogE("gege",    viewModel.userModel?.lc_region_id)

            ZoneDialog.newInstance(object : ZoneDialog.ZoneCallBack {
                override fun onZoneClicked(
                    data: LOVData
                ) {
                    zoneEditText.setText(data.value)
                    viewModel.userModel?.lc_zone_id = data.code

                    clusterEditText.setText("")
                    setClickableLionsClub()
                }
            }, region = viewModel.userModel?.lc_region_id.toString()).show(childFragmentManager, CityDialog.TAG)
        }

        clusterEditText.setOnSingleClickListener {
            ClusterDialog.newInstance(object : ClusterDialog.ClusterCallBack {
                override fun onClusterClicked(
                    data: LOVData
                ) {
                    viewModel.userModel?.lc_location_id = data.code
                    clusterEditText.setText(data.value)
                    setClickableLionsClub()
                }
            },region = viewModel.userModel?.lc_region_id.toString(), zone = viewModel.userModel?.lc_zone_id.toString()).show(childFragmentManager, BrgyDialog.TAG)
        }

        saveButton.setOnSingleClickListener {
                ProfileConfirmationDialog.newInstance(this@ProfileUpdateFragment)
                    .show(childFragmentManager, RegisterSuccessDialog.TAG)
        }
    }

    private fun setClickable() = binding.run {
        cityEditText.isClickable = provinceEditText.text.toString().isNotEmpty()
        barangayEditText.isClickable= cityEditText.text.toString().isNotEmpty()
    }

    private fun setClickableLionsClub() = binding.run {
        zoneEditText.isClickable = regionEditText.text.toString().isNotEmpty()
        clusterEditText.isClickable= zoneEditText.text.toString().isNotEmpty()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMyAccountClicked(dialog: ProfileConfirmationDialog)=binding.run {
        dialog.dismiss()
        val request = UpdateInfoRequest(
            province_sku =  viewModel.userModel?.province_sku.orEmpty(),
            province_name = provinceEditText.text.toString(),
            city_sku =viewModel.userModel?.city_code.orEmpty(),
            city_name = cityEditText.text.toString(),
            brgy_sku = viewModel.userModel?.brgy_code.orEmpty(),
            brgy_name = barangayEditText.text.toString(),
            street_name =  streetEditText.text.toString(),
            zipcode = viewModel.userModel?.zipcode.orEmpty(),
            firstname = firstNameEditText.text.toString(),
            lastname = lastNameEditText.text.toString(),
            middlename = middleNameEditText.text.toString(),
            email = null,
            birthdate = birthdateEditText.text.toString(),
            lc_region_id = viewModel.userModel?.lc_region_id.orEmpty(),
            lc_zone_id = viewModel.userModel?.lc_zone_id.orEmpty(),
            lc_location_id = viewModel.userModel?.lc_location_id.orEmpty(),
        )
        viewModel.doUpdateProfile(request)
    }
}