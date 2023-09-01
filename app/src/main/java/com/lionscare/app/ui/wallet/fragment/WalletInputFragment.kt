package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentTopUpBinding
import com.lionscare.app.databinding.FragmentWalletInputBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletInputFragment : Fragment() {

    private var _binding: FragmentWalletInputBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletInputBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        setupDetails()
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_title)
                recipientEditText.setText(activity.qrData.name)
            }
            "Scan 2 Pay" -> {
                titleTextView.text = getString(R.string.wallet_points_recipient_title)
            }
            "Post Request" -> {
                titleTextView.text = getString(R.string.wallet_request_points_title)
                recipientTitleTextView.text = getString(R.string.wallet_from_title)
            }
        }

    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if(amountEditText.text.toString().isNotEmpty() && amountEditText.text.toString().replace(",","").toDouble() != 0.0){
                activity.amount = amountEditText.text.toString()
                activity.message = messageEditText.text.toString()
                findNavController().navigate(WalletInputFragmentDirections.actionNavigationWalletInputToNavigationWalletSummary())
            }else{
             amountEditText.error = "Enter amount"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}