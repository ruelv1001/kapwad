package kapwad.reader.app.ui.profile.fragment

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
import kapwad.reader.app.R
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.data.repositories.profile.request.UpdateInfoRequest
import kapwad.reader.app.databinding.FragmentProfileUpdateBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.profile.activity.ProfileActivity
import kapwad.reader.app.ui.profile.dialog.ProfileConfirmationDialog
import kapwad.reader.app.ui.profile.viewmodel.ProfileViewModel
import kapwad.reader.app.ui.profile.viewmodel.ProfileViewState
import kapwad.reader.app.ui.register.dialog.BrgyDialog
import kapwad.reader.app.ui.register.dialog.CityDialog
import kapwad.reader.app.ui.register.dialog.ProvinceDialog
import kapwad.reader.app.ui.register.dialog.RegisterSuccessDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
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

    }
}