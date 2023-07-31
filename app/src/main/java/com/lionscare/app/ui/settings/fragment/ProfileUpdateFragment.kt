package com.lionscare.app.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentProfileUpdateBinding
import com.lionscare.app.ui.register.activity.RegisterActivity
import com.lionscare.app.ui.register.dialog.BrgyDialog
import com.lionscare.app.ui.register.dialog.CityDialog
import com.lionscare.app.ui.register.dialog.ProvinceDialog
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.settings.activity.ProfileActivity
import com.lionscare.app.ui.settings.dialog.ProfileConfirmationDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileUpdateFragment: Fragment() {
    private var _binding: FragmentProfileUpdateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }
    private var reference:String = ""
    private var cityCode:String = ""

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
        setClickListeners()
        setView()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_reg_complete_profile))
    }

    private fun setView() = binding.run{
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

        cityEditText.isClickable= false
        barangayEditText.isClickable= false
    }

    private fun setClickListeners() = binding.run {
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
                    barangayEditText.setText(cityName)
                    setClickable()
                }
            }, cityCode).show(childFragmentManager, BrgyDialog.TAG)
        }

        saveButton.setOnSingleClickListener {

            if (provinceEditText.text.toString().isEmpty()){
                provinceTextInputLayout.error = "Field is required"
            }
            if (cityEditText.text.toString().isEmpty()){
                cityTextInputLayout.error = "Field is required"
            }
            if (barangayEditText.text.toString().isEmpty()){
                barangayTextInputLayout.error = "Field is required"
            }
            if (streetEditText.text.toString().isEmpty()){
                streetTextInputLayout.error = "Field is required"
            }

            if ( provinceEditText.text.toString().isNotEmpty() &&
                cityEditText.text.toString().isNotEmpty() &&
                barangayEditText.text.toString().isNotEmpty() &&
                streetEditText.text.toString().isNotEmpty()){
                ProfileConfirmationDialog.newInstance().show(childFragmentManager, RegisterSuccessDialog.TAG)
            }
        }
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