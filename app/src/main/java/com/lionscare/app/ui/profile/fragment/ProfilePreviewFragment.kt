package com.lionscare.app.ui.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.databinding.FragmentProfilePreviewBinding
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.ui.profile.dialog.VerificationSuccessDialog
import com.lionscare.app.ui.profile.viewmodel.ProfileViewModel
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePreviewFragment : Fragment() {
    private var _binding: FragmentProfilePreviewBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfilePreviewBinding.inflate(
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
//                handleInputError(viewState.errorData?: ErrorsData())
            }

            else -> hideLoadingDialog()
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_my_profile))
        hideLoadingDialog()
    }

    private fun setView(userModel: UserModel?) = binding.run {
        firstNameTextView.text = userModel?.firstname
        middleNameTextView.text = userModel?.middlename
        lastNameTextView.text = userModel?.lastname
        contactTextView.text = userModel?.phone_number
        emailTextView.text = userModel?.email
        provinceTextView.text = userModel?.province_name
        cityTextView.text = userModel?.city_name
        barangayTextView.text = userModel?.brgy_name
        streetTextView.text = userModel?.street_name
    }

    private fun setClickListeners() = binding.run {
        emailVerifyTextView.setOnSingleClickListener {
            VerificationSuccessDialog.newInstance().show(childFragmentManager, RegisterSuccessDialog.TAG)
        }
        editImageView.setOnSingleClickListener {
            findNavController().navigate(ProfilePreviewFragmentDirections.actionNavigationProfileUpdate())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val APPROVED = "APPROVED"
        private const val DECLINED = "DECLINED"
    }
}