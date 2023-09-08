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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.databinding.FragmentProfileUpdateBinding
import com.lionscare.app.ui.register.dialog.BrgyDialog
import com.lionscare.app.ui.register.dialog.CityDialog
import com.lionscare.app.ui.register.dialog.ProvinceDialog
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.ui.profile.dialog.ProfileConfirmationDialog
import com.lionscare.app.ui.profile.viewmodel.ProfileViewModel
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
//        viewModel.getProfileDetails()
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
            is ProfileViewState.SuccessUpdatePhoneNumber -> {
//                hideLoadingDialog()
//                val snackbar = Snackbar.make(binding.root, viewState.response.msg.toString(), Snackbar.LENGTH_LONG)
//                snackbar.setTextMaxLines(3)
//                snackbar.view.translationY = -(binding.saveButton.height + snackbar.view.height).toFloat()
//                snackbar.show()
//
//                val bundle = Bundle().apply {
//                    putString("phone", binding.phoneEditText.text.toString() )
//                }
//                val action =
//                    ProfileUpdateFragmentDirections.actionNavigationProfileUpdateToProfileOTPFragment( binding.phoneEditText.text.toString())
//
//                findNavController().navigate(action.actionId, bundle)
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
        if (errorsData.province_name?.get(0)?.isNotEmpty() == true) binding.provinceTextInputLayout.error = errorsData.province_name?.get(0)
        if (errorsData.city_name?.get(0)?.isNotEmpty() == true) binding.cityTextInputLayout.error = errorsData.city_name?.get(0)
        if (errorsData.brgy_name?.get(0)?.isNotEmpty() == true) binding.barangayTextInputLayout.error = errorsData.brgy_name?.get(0)
        if (errorsData.street_name?.get(0)?.isNotEmpty() == true) binding.streetTextInputLayout.error = errorsData.street_name?.get(0)
        if (errorsData.phone_number?.get(0)?.isNotEmpty() == true){
            Toast.makeText(requireActivity(),errorsData.phone_number?.get(0),Toast.LENGTH_SHORT).show()
        }
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

        setClickable()
    }

    private fun setClickListeners() = binding.run {
//        phoneEditText.setOnSingleClickListener {
//            // Inflate the custom dialog layout
//            val inflater = requireActivity().layoutInflater
//            val dialogView = inflater.inflate(R.layout.dialog_change_phone_number, null)
//
//            //create custom dialog
//            MaterialAlertDialogBuilder(requireContext())
//                .setView(dialogView)
//                .setMessage(resources.getString(R.string.set_new_phone_number))
//                .setNegativeButton(resources.getString(R.string.lbl_cancel)) { dialog, _ ->
//                   dialog.dismiss()
//                }
//                .setPositiveButton(resources.getString(R.string.pending_accept_text)) { dialog , _ ->
//                    val number =  dialogView.findViewById<TextView>(R.id.inputPhoneEditText).text.toString()
//                    if (number.isEmpty()){
//                        Toast.makeText(requireContext(),
//                            getString(R.string.field_is_required), Toast.LENGTH_SHORT).show()
//                    }else{
//                        binding.phoneEditText.setText(number)
//                        //send the otp immediately
//                        viewModel.changePhoneNumber(UpdatePhoneNumberRequest(phone_number = number))
//                    }
//
//                }
//                .show()
//        }

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
            },  viewModel.userModel?.province_sku.orEmpty()).show(childFragmentManager, CityDialog.TAG)
        }

        barangayEditText.setOnSingleClickListener {
            BrgyDialog.newInstance(object : BrgyDialog.AddressCallBack {
                override fun onAddressClicked(
                    cityName: String,
                    citySku: String,
                    zipCodes: String
                ) {

//                    brgyCode = citySku
                    viewModel.userModel?.brgy_code = citySku
                    viewModel.userModel?.zipcode = zipCodes
//                    zipCode = zipCodes
                    barangayEditText.setText(cityName)
                    zipcodeEditText.setText(zipCodes)
                    setClickable()
                }
            }, viewModel.userModel?.city_code.orEmpty()).show(childFragmentManager, BrgyDialog.TAG)
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
            viewModel.userModel?.province_sku.orEmpty(),
            provinceEditText.text.toString(),
            viewModel.userModel?.city_code.orEmpty(),
            cityEditText.text.toString(),
            viewModel.userModel?.brgy_code.orEmpty(),
            barangayEditText.text.toString(),
            streetEditText.text.toString(),
            viewModel.userModel?.zipcode.orEmpty(),
            firstNameEditText.text.toString(),
            lastNameEditText.text.toString(),
            middleNameEditText.text.toString()
        )
    }
}