package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentTopUpBinding
import com.lionscare.app.databinding.FragmentWalletInputBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.utils.formatCurrency
import com.lionscare.app.utils.removeCommas
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException

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

                recipientEditText.setText(
                    if (activity.isGroupId){
                        activity.groupData.name
                    }else{
                        activity.qrData.name
                    }
                )
            }
        }

    }

    private fun setupClickListener() = binding.run{
        // Define a TextWatcher
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                        val text = s.toString()
                        val selectionStart = binding.amountEditText.selectionStart
                        val selectionEnd = binding.amountEditText.selectionEnd
                        if (text.isNotEmpty()) {
                            try {
                                val formattedText = formatCurrency(text)
                                val formattedLength = formattedText.length
                                if (text != formattedText) {
                                    binding.amountEditText.setText(formattedText)
                                    val newCursorPosition = selectionStart +
                                            (formattedLength - text.length)
                                    // Adjust cursor position for deleted characters
                                    val decimalSeparatorPosition = formattedText.indexOf('.')
                                    val cursorPosition =
                                        if (selectionStart <= decimalSeparatorPosition) {
                                            // Keep the cursor before the decimal separator
                                            newCursorPosition.coerceIn(0, decimalSeparatorPosition)
                                        } else {
                                            // Keep the cursor after the decimal separator
                                            newCursorPosition.coerceIn(
                                                decimalSeparatorPosition,
                                                formattedLength
                                            )
                                        }
                                    binding.amountEditText.setSelection(cursorPosition)

                                    //lastly add the .00 at the end

                                }
                            } catch (e: ParseException) {
                                // Handle invalid input here if needed
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.invalid_number_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
        }
        binding.amountEditText.addTextChangedListener(textWatcher)

        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if(amountEditText.text.toString().isNotEmpty() && amountEditText.text.toString().replace(",","").toDouble() != 0.0){
                activity.amount = amountEditText.text.toString().removeCommas()
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