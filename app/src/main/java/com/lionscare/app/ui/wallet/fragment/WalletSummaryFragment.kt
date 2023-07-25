package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lionscare.app.databinding.FragmentWalletSummaryBinding

class WalletSummaryFragment : Fragment() {

    private var _binding: FragmentWalletSummaryBinding? = null
    private val binding get() = _binding!!

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
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
        /*backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            findNavController().navigate(TopUpFragmentDirections.actionNavigationTopUpToNavigationPaymentMethod())
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}