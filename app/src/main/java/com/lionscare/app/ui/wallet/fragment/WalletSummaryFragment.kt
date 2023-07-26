package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentWalletSummaryBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.utils.setOnSingleClickListener

class WalletSummaryFragment : Fragment() {

    private var _binding: FragmentWalletSummaryBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletSummaryBinding.inflate(
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
                titleTextView.text = getString(R.string.wallet_send_points_summary_title)

                activity.data.id?.let { recipientLayout.profileImageView.setImageResource(it) }
                recipientLayout.nameTextView.text = activity.data.title
                recipientLayout.idNoTextView.text = activity.data.amount
            }
            "Scan 2 Pay" -> {
                titleTextView.text = getString(R.string.wallet_points_recipient_summary_title)
            }
            "Post Request" -> {
                titleTextView.text = getString(R.string.wallet_request_points_summary_title)
                recipientTitleTextView.text = getString(R.string.wallet_from_title)

                activity.data.id?.let { recipientLayout.profileImageView.setImageResource(it) }
                recipientLayout.nameTextView.text = activity.data.title
                recipientLayout.idNoTextView.text = activity.data.amount
            }
        }

    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            findNavController().navigate(WalletSummaryFragmentDirections.actionNavigationWalletSummaryToNavigationWalletDetails())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}