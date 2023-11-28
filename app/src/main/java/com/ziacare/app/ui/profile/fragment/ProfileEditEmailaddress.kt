package com.ziacare.app.ui.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.ziacare.app.R
import com.ziacare.app.data.model.ErrorsData
import com.ziacare.app.data.repositories.profile.request.UpdateInfoRequest
import com.ziacare.app.databinding.FragmentProfileEditEmailaddressBinding
import com.ziacare.app.ui.profile.activity.ProfileActivity
import com.ziacare.app.ui.profile.viewmodel.ProfileViewModel
import com.ziacare.app.ui.profile.viewmodel.ProfileViewState
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditEmailaddress: Fragment(){
    private var _binding: FragmentProfileEditEmailaddressBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileEditEmailaddressBinding.inflate(
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

        if (viewModel.email != getString(R.string.no_email_has_been_set_yet)){
            binding.emailEditText.setText(viewModel.email)
        }
        activity.setTitlee(getString(R.string.update_email_address))
    }

    private fun setClickListeners() = binding.run  {
        confirmButton.setOnSingleClickListener {
            if (emailEditText.text?.isEmpty() == true){
                requireActivity().toastError(getString(R.string.fields_cannot_be_empty), CpmToast.LONG_DURATION)
            }else{
                    val request = UpdateInfoRequest(
                        province_sku =  viewModel.userModel?.province_sku.orEmpty(),
                        province_name = viewModel.userModel?.province_name.orEmpty(),
                        city_sku =viewModel.userModel?.city_code.orEmpty(),
                        city_name = viewModel.userModel?.city_name.orEmpty(),
                        brgy_sku = viewModel.userModel?.brgy_code.orEmpty(),
                        brgy_name = viewModel.userModel?.brgy_name.orEmpty(),
                        street_name = viewModel.userModel?.street_name.orEmpty(),
                        zipcode = viewModel.userModel?.zipcode.orEmpty(),
                        firstname = viewModel.userModel?.firstname.orEmpty(),
                        lastname = viewModel.userModel?.lastname.orEmpty(),
                        middlename =viewModel.userModel?.middlename.orEmpty(),
                        email = emailEditText.text.toString(), //THIS IS CHANGED HERE IN THIS FRAGMENT, REST IS FROM INITIAL GET PROFILE
                        birthdate = viewModel.userModel?.birthdate?.date_only_ph.orEmpty()
                    )

                    viewModel.doUpdateProfile(request) //the way to get email address is to pass email address in update profile
             }
        }
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
                binding.verificationTextView.visibility  = View.VISIBLE
                binding.verificationDescTextView.visibility  = View.VISIBLE
                binding.confirmButton.isClickable    = false
                val backgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.color_primaryLight)
                // Set the background tint
                binding.confirmButton.backgroundTintList = backgroundColor
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
        if (errorsData.email?.get(0)?.isNotEmpty() == true){
            requireActivity().toastError(errorsData.email?.get(0).toString(), CpmToast.LONG_DURATION)
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