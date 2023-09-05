package com.lionscare.app.ui.settings.fragment

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.databinding.FragmentProfileUpdateBinding
import com.lionscare.app.ui.register.dialog.BrgyDialog
import com.lionscare.app.ui.register.dialog.CityDialog
import com.lionscare.app.ui.register.dialog.ProvinceDialog
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.settings.activity.ProfileActivity
import com.lionscare.app.ui.settings.dialog.ProfileConfirmationDialog
import com.lionscare.app.ui.settings.viewmodel.ProfileViewModel
import com.lionscare.app.ui.settings.viewmodel.ProfileViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileUpdateFragment: Fragment(), ProfileConfirmationDialog.ProfileSaveDialogCallBack {
    private var _binding: FragmentProfileUpdateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }
    private var reference:String = ""
    private var cityCode:String = ""
    private var brgyCode:String = ""
    private var zipCode:String = ""
    private val viewModel: ProfileViewModel by viewModels()
    private var userModelz:UserModel? = null

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
        viewModel.getProfileDetails()
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState){
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.Success -> {
                hideLoadingDialog()
                Toast.makeText(requireActivity(),viewState.message,Toast.LENGTH_SHORT).show()
                activity.onBackPressed()
            }
            is ProfileViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
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
        if (errorsData.province_name?.get(0)?.isNotEmpty() == true) binding.provinceTextInputLayout.error = errorsData.province_name?.get(0)
        if (errorsData.city_name?.get(0)?.isNotEmpty() == true) binding.cityTextInputLayout.error = errorsData.city_name?.get(0)
        if (errorsData.brgy_name?.get(0)?.isNotEmpty() == true) binding.barangayTextInputLayout.error = errorsData.brgy_name?.get(0)
        if (errorsData.street_name?.get(0)?.isNotEmpty() == true) binding.streetTextInputLayout.error = errorsData.street_name?.get(0)
    }


    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_update_profile))
        hideLoadingDialog()
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    private fun setView(userModel: UserModel?) = binding.run{
        userModelz = userModel
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
        emailEditText.setText(userModel?.email)
        provinceEditText.setText(userModel?.province_name)
        cityEditText.setText(userModel?.city_name)
        barangayEditText.setText(userModel?.brgy_name)
        streetEditText.setText(userModel?.street_name)
        phoneEditText.setText(userModel?.phone_number)
        reference = userModel?.province_sku.toString()
        cityCode = userModel?.city_code.toString()
        brgyCode = userModel?.brgy_code.toString()
        zipCode = userModel?.zipcode.toString()

        setClickable()
    }

    private fun setClickListeners() = binding.run {
        phoneEditText.setOnSingleClickListener {
            // Inflate the custom dialog layout
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_change_phone_number, null)

            //create custom dialog
            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setMessage(resources.getString(R.string.set_new_phone_number))
                .setNegativeButton(resources.getString(R.string.lbl_cancel)) { dialog, _ ->
                   dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.pending_accept_text)) { dialog , _ ->
                    val number =  dialogView.findViewById<TextView>(R.id.inputPhoneEditText).text.toString()
                    if (number.isEmpty()){
                        Toast.makeText(requireContext(),
                            getString(R.string.field_is_required), Toast.LENGTH_SHORT).show()
                    }else{
                        phoneEditText.setText(number)
                        dialog.dismiss()

                        val bundle = Bundle().apply {
                            putString("phone", number)
                        }
                        val action =
                            ProfileUpdateFragmentDirections.actionNavigationProfileUpdateToProfileOTPFragment(number)

                        findNavController().navigate(action.actionId, bundle)
                    }

                }
                .show()
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
                    zipCodes: String
                ) {

                    brgyCode = citySku
                    zipCode = zipCodes
                    barangayEditText.setText(cityName)
                    setClickable()
                }
            }, cityCode).show(childFragmentManager, BrgyDialog.TAG)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMyAccountClicked(dialog: ProfileConfirmationDialog)=binding.run {
        dialog.dismiss()
        viewModel.doUpdateProfile(
            reference,
            provinceEditText.text.toString(),
            cityCode,
            cityEditText.text.toString(),
            brgyCode,
            barangayEditText.text.toString(),
            streetEditText.text.toString(),
            zipCode,
            firstNameEditText.text.toString(),
            lastNameEditText.text.toString(),
            middleNameEditText.text.toString(),
            emailEditText.text.toString()
        )
    }
}