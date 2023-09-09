package com.lionscare.app.ui.profile.fragment

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
import com.google.android.material.snackbar.Snackbar
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.databinding.FragmentProfileEditNumberBinding
import com.lionscare.app.databinding.FragmentProfilePreviewBinding
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.ui.profile.viewmodel.ProfileViewModel
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditNumberFragment: Fragment() {
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

    private fun setClickListeners() = binding.run  {
        confirmButton.setOnSingleClickListener {
            viewModel.changePhoneNumber(UpdatePhoneNumberRequest(phone_number = binding.phoneEditText.text.toString()))
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
            is ProfileViewState.SuccessUpdatePhoneNumber -> {
                hideLoadingDialog()
                val snackbar = Snackbar.make(binding.root, viewState.response.msg.toString(), Snackbar.LENGTH_LONG)
                snackbar.setTextMaxLines(3)
                snackbar.view.translationY = -(binding.confirmButton.height + snackbar.view.height).toFloat()
                snackbar.show()
                viewModel.phoneNumber = binding.phoneEditText.text.toString()
                findNavController().navigate(ProfileEditNumberFragmentDirections.actionProfileEditNumberFragmentToProfileOTPFragment())
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
        if (errorsData.otp?.get(0)?.isNotEmpty() == true){
            Toast.makeText(requireActivity(),errorsData.otp?.get(0), Toast.LENGTH_SHORT).show()
        }
        if (errorsData.phone_number?.get(0)?.isNotEmpty() == true){
            Toast.makeText(requireActivity(),errorsData.phone_number?.get(0), Toast.LENGTH_SHORT).show()
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
