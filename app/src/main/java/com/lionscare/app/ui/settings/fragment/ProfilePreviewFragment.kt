package com.lionscare.app.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentProfilePreviewBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.settings.activity.ProfileActivity
import com.lionscare.app.ui.settings.dialog.VerificationSuccessDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePreviewFragment : Fragment() {
    private var _binding: FragmentProfilePreviewBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as ProfileActivity }

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
        setClickListeners()
        setView()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_my_profile))
    }

    private fun setView() = binding.run {

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