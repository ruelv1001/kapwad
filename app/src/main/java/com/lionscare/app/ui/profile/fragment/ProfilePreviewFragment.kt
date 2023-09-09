package com.lionscare.app.ui.profile.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.lionscare.app.utils.calculateAge
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfilePreviewFragment : Fragment() {
    private var _binding: FragmentProfilePreviewBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }
    private val viewModel: ProfileViewModel by activityViewModels()

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
        requireActivity().title = getString(R.string.lbl_my_profile)
        hideLoadingDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(userModel: UserModel?) = binding.run {
        nameTextView.text = userModel?.getFullName()
        dateOfBirthTextView.text = userModel?.birthdate?.date_only_ph
        ageTextView.text = userModel?.birthdate?.date_only_ph?.calculateAge().toString()
        addressTextView.text = "${userModel?.street_name}, ${userModel?.brgy_name},\n${userModel?.city_name}, ${userModel?.province_name.orEmpty().ifEmpty { "Address unavailable" }}"

        emailEditText.setText(userModel?.email)
        if (userModel?.email_verified == true){
            emailIsVerifiedTextView.text = getString(R.string.lbl_verified)
            emailIsVerifiedTextView.setBackgroundResource(R.drawable.bg_rounded_verified)
        }else{
            emailIsVerifiedTextView.text = getString(R.string.unverified)
            emailIsVerifiedTextView.setBackgroundResource(R.drawable.bg_rounded_pending)
        }

        phoneEditText.setText(userModel?.phone_number)
        if (userModel?.lc_member == true){
            lionsClubTextView.text = "${userModel.lc_group} (${userModel.lc_location_id})"
        }else{
            lionsClubTextView.text = getString(R.string.not_yet_a_member)
        }
    }

    private fun setClickListeners() = binding.run {
        emailEditText.setOnSingleClickListener {

        }
        phoneEditText.setOnSingleClickListener {

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