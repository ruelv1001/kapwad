package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentWalletInputBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.utils.getDecimalFormat
import com.lionscare.app.utils.removeCommas
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.StringTokenizer


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
        val editText = binding.amountEditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed when text changes
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    editText.removeTextChangedListener(this)
                    val value = editText.text.toString()
                    if (value.isNotEmpty()) {
                        val str = value.replace(",", "")
                        if (str.isNotEmpty()) {
                            // Check if there is already a dot and if the characters after it are 2 or more
                            val dotIndex = str.indexOf(".")
                            if (dotIndex != -1 && str.length - dotIndex > 3) {
                                // Remove the last entered character
                                editText.setText(value.substring(0, value.length - 1))
                                editText.setSelection(editText.text.toString().length)
                            } else {
                                editText.setText(getDecimalFormat(str))
                                editText.setSelection(editText.text.toString().length)
                            }
                        }
                    }
                    editText.addTextChangedListener(this)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                    editText.addTextChangedListener(this)
                }
            }
        })


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