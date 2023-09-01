package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentWalletDetailsBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.utils.copyToClipboard
import com.lionscare.app.utils.setOnSingleClickListener

class WalletDetailsFragment : Fragment() {

    private var _binding: FragmentWalletDetailsBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDetails()
        setupClickListener()
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_details_title)
                requestedPointsTextView.text = getString(R.string.wallet_sent_points_text)
                recipientLayout.nameTextView.text = activity.qrData.name
                //TODO to be updated when display id ready
                recipientLayout.idNoTextView.text = activity.qrData.id
            }
            "Scan 2 Pay" -> {
                titleTextView.text = getString(R.string.wallet_scan2pay_details_title)
                requestedPointsTextView.text = getString(R.string.wallet_sent_points_text)
            }
            "Post Request" -> {
                titleTextView.text = getString(R.string.wallet_request_points_details_title)
                recipientTitleTextView.text = getString(R.string.wallet_from_title)
                requestedPointsTextView.text = getString(R.string.request_posted_requested_points_text)

                activity.data.id?.let { recipientLayout.profileImageView.setImageResource(it) }
                recipientLayout.nameTextView.text = activity.data.title
                recipientLayout.idNoTextView.text = activity.data.amount
            }
        }

    }

    private fun setupClickListener() = binding.run{
        closeImageView.setOnSingleClickListener {
            requireActivity().finish()
        }
        copyImageView.setOnSingleClickListener {
            requireContext().copyToClipboard(refidTextView.text)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}