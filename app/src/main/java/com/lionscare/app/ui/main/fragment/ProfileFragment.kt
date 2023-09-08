package com.lionscare.app.ui.main.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lionscare.app.R
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.databinding.FragmentSettingsBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.ui.main.viewmodel.SettingsViewModel
import com.lionscare.app.ui.main.viewmodel.SettingsViewState
import com.lionscare.app.ui.onboarding.activity.SplashScreenActivity
import com.lionscare.app.ui.profile.activity.NotificationsActivity
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.ui.profile.activity.UpdatePasswordActivity
import com.lionscare.app.ui.verify.activity.AccountVerificationActivity
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var isBadgeVerified : Boolean? = false
    private var isAccountVerified : Boolean? = false
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLogoutAccount()
        setClickListeners()
        setDetails()
        viewModel.getProfileDetails()
    }

    private fun observeLogoutAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: SettingsViewState) {
        when (viewState) {
            is SettingsViewState.Loading -> showLoadingDialog(R.string.logout_loading)
            is SettingsViewState.Success -> {
                hideLoadingDialog()
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_SHORT).show()
                val intent = SplashScreenActivity.getIntent(requireActivity())
                startActivity(intent)
                requireActivity().finishAffinity()
            }

            is SettingsViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is SettingsViewState.InputError -> Unit
            is SettingsViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
                setView(viewState.userModel)
            }
            else -> Unit
        }
    }

    private fun setView(userModel: UserModel?)=binding.run {
        nameTextView.text = userModel?.name
        badgeTextView.isVisible = false
        idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }

    private fun setClickListeners() = binding.run {
        requestBadgeLinearLayout.setOnSingleClickListener {
            val intent = VerifiedBadgeActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        verifyAccountLinearLayout.setOnSingleClickListener {
            val intent = AccountVerificationActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        changePasswordLinearLayout.setOnSingleClickListener {
            val intent = UpdatePasswordActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        logoutLinearLayout.setOnSingleClickListener {
            openLogoutConfirmation()
        }

        profileLinearLayout.setOnSingleClickListener {
            val intent = ProfileActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        notifLinearLayout.setOnSingleClickListener {
            val intent = NotificationsActivity.getIntent(requireActivity())
            startActivity(intent)
        }
    }

    private fun setDetails() = binding.run {
        if(isBadgeVerified == true){
            requestBadgeImageView.setImageResource(R.drawable.ic_settings_request_badge_gray)
            verifiedRBTextView.visibility = View.VISIBLE
            arrowRBImageView.visibility = View.GONE
        }else{
            requestBadgeImageView.setImageResource(R.drawable.ic_settings_request_badge)
            verifiedRBTextView.visibility = View.GONE
            arrowRBImageView.visibility = View.VISIBLE
        }

        if(isAccountVerified == true){
            verifyAccountImageView.setImageResource(R.drawable.ic_settings_verify_account_gray)
            verifiedVATextView.visibility = View.VISIBLE
            arrowVAImageView.visibility = View.GONE
        }else{
            verifyAccountImageView.setImageResource(R.drawable.ic_settings_verify_account)
            verifiedVATextView.visibility = View.GONE
            arrowVAImageView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openLogoutConfirmation() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.logout_title_lbl))
        builder.setMessage(getString(R.string.logout_desc_lbl))
        builder.setPositiveButton(getString(R.string.logout_btn)) { _, _ ->
            viewModel.doLogoutAccount()
        }
        builder.setNegativeButton(getString(R.string.logout_cancel_btn), null)
        builder.show()
    }

}