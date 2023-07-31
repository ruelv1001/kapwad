package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentSettingsBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.onboarding.activity.LoginActivity
import com.lionscare.app.ui.settings.activity.UpdatePasswordActivity
import com.lionscare.app.ui.verify.activity.AccountVerificationActivity
import com.lionscare.app.utils.setOnSingleClickListener

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var isBadgeVerified : Boolean? = false
    private var isAccountVerified : Boolean? = false

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
        setClickListeners()
        setDetails()
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
            val intent = LoginActivity.getIntent(requireActivity())
            startActivity(intent)
            requireActivity().finishAffinity()
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

}