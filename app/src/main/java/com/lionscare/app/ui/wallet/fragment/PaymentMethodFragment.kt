package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lionscare.app.databinding.FragmentPaymentMethodBinding
import com.lionscare.app.ui.wallet.activity.TopUpPointsActivity
import com.lionscare.app.utils.setOnSingleClickListener

class PaymentMethodFragment : Fragment() {

    private var _binding: FragmentPaymentMethodBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as TopUpPointsActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentMethodBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
        continueButton.text = "Top Up ${activity.amount} Points"
        //balanceTextView.text = "₱${activity.amount}"

        backImageView.setOnSingleClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            activity.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}