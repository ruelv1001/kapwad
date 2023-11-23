package com.ziacare.app.ui.billing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ziacare.app.R
import com.ziacare.app.databinding.FragmentBillingDonationQrBinding
import com.ziacare.app.ui.billing.activity.BillingActivity
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.setQR


class ScanBillingStatementFragment : Fragment() {
    private var _binding: FragmentBillingDonationQrBinding? = null
    private val binding get() = _binding!!

    private val activity by lazy { requireActivity() as BillingActivity }
    private lateinit var title: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingDonationQrBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = activity.getToolbarTitle()

        activity.toggleToolbar(false, R.color.color_accent, "")

        binding.qrImageView.setImageBitmap(setQR(requireActivity(), "sasacs sas  as "))
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.toggleToolbar(true, R.color.white, title)
        _binding = null
    }
}