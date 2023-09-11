package com.lionscare.app.ui.register.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.profile.request.UpdateInfoRequest
import com.lionscare.app.data.repositories.profile.response.LOVData
import com.lionscare.app.databinding.FragmentRegistrationCompleteProfileBinding
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.ui.profile.dialog.ProfileConfirmationDialog
import com.lionscare.app.ui.register.activity.RegisterActivity
import com.lionscare.app.ui.register.dialog.BrgyDialog
import com.lionscare.app.ui.register.dialog.CityDialog
import com.lionscare.app.ui.register.dialog.ClusterDialog
import com.lionscare.app.ui.register.dialog.ProvinceDialog
import com.lionscare.app.ui.register.dialog.RegionDialog
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.register.dialog.ZoneDialog
import com.lionscare.app.ui.register.viewmodel.RegisterViewModel
import com.lionscare.app.ui.register.viewmodel.RegisterViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class RegisterCompleteProfileFragment: Fragment() {
    private var _binding: FragmentRegistrationCompleteProfileBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as RegisterActivity }
    private var reference: String = ""
    private var cityCode: String = ""
    private var brgyCode: String = ""
    private var lc_location_id: String = ""
    private var lc_region_id: String = ""
    private var lc_zone_id: String = ""
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
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_SHORT).show()
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
        if (errorsData.email?.get(0)?.isNotEmpty() == true) binding.emailTextInputLayout.error =
            errorsData.email?.get(0)
        if (errorsData.province_name?.get(0)
                ?.isNotEmpty() == true
        ) binding.provinceTextInputLayout.error = errorsData.province_name?.get(0)
        if (errorsData.city_name?.get(0)?.isNotEmpty() == true) binding.cityTextInputLayout.error =
            errorsData.city_name?.get(0)
        if (errorsData.brgy_name?.get(0)
                ?.isNotEmpty() == true
        ) binding.barangayTextInputLayout.error = errorsData.brgy_name?.get(0)
        if (errorsData.street_name?.get(0)
                ?.isNotEmpty() == true
        ) binding.streetTextInputLayout.error = errorsData.street_name?.get(0)
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_reg_complete_profile))
    }

    private fun setView() = binding.run {
        emailEditText.doOnTextChanged { text, start, before, count ->
            emailTextInputLayout.error = ""
        }
        provinceEditText.doOnTextChanged { text, start, before, count ->
            provinceTextInputLayout.error = ""
        }
        cityEditText.doOnTextChanged { text, start, before, count ->
            cityTextInputLayout.error = ""
        }
        barangayEditText.doOnTextChanged { text, start, before, count ->
            barangayTextInputLayout.error = ""
        }
        streetEditText.doOnTextChanged { text, start, before, count ->
            streetTextInputLayout.error = ""
        }

        cityEditText.isClickable = false
        barangayEditText.isClickable = false
    }

    private fun setClickwListeners() = binding.run {


    }

    private fun setClickListeners() = binding.run {
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
            datePicker.show(childFragmentManager, "ProfileUpdateFragment");
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

        barangayEditText.setOnSingleClickListener {
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

//        ===================== LIONS CLUB INTL
        regionEditText.setOnClickListener {
            RegionDialog.newInstance(object : RegionDialog.RegionCallBack {
                override fun onRegionClicked(
                    data: LOVData
                ) {
                    regionEditText.setText(data.value)
                    lc_region_id = data.code.toString()

                    zoneEditText.setText("")
                    clusterEditText.setText("")
                    setClickableLionsClub()
                }
            }).show(childFragmentManager, RegionDialog.TAG)
        }

        zoneEditText.setOnSingleClickListener {
            ZoneDialog.newInstance(object : ZoneDialog.ZoneCallBack {
                override fun onZoneClicked(
                    data: LOVData
                ) {
                    zoneEditText.setText(data.value)
                    lc_zone_id = data.code.toString()

                    clusterEditText.setText("")
                    setClickableLionsClub()
                }
            }, lc_region_id).show(childFragmentManager, CityDialog.TAG)
        }

        clusterEditText.setOnSingleClickListener {
            ClusterDialog.newInstance(object : ClusterDialog.ClusterCallBack {
                override fun onClusterClicked(
                    data: LOVData
                ) {
                    lc_location_id = data.code.toString()
                    clusterEditText.setText(data.value)
                    setClickableLionsClub()
                }
            }, zone= lc_zone_id, region = lc_region_id).show(childFragmentManager, BrgyDialog.TAG)
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
                firstname = activity.requestModel.firstname.orEmpty(),
                lastname = activity.requestModel.lastname.orEmpty(),
                middlename = activity.requestModel.middlename.orEmpty(),
                email = emailEditText.text.toString(),
                birthdate = birthdateEditText.text.toString(),
                lc_region_id = lc_region_id,
                lc_zone_id = lc_zone_id,
                lc_location_id = lc_zone_id,
            )

            CommonLogger.instance.sysLogE("HERE", request)

//            viewModel.doUpdateProfile(request)
        }
    }



    private fun setClickableLionsClub() = binding.run {
        zoneEditText.isClickable = regionEditText.text.toString().isNotEmpty()
        clusterEditText.isClickable= zoneEditText.text.toString().isNotEmpty()
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