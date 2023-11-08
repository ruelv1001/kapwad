package com.lionscare.app.ui.billing.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lionscare.app.R
import com.lionscare.app.databinding.DialogOptionDonateBinding
import com.lionscare.app.utils.setAmountFormat
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionDonateDialog : BottomSheetDialogFragment() {

    private var viewBinding: DialogOptionDonateBinding? = null
    private var callback: OptionDonateAmountCallback? = null
    private var personalWallet = ""
    private var groupWallet = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_option_donate,
            container,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogOptionDonateBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        amountEditText.setAmountFormat()
        personalWalletTextView.text = personalWallet
        groupWalletTextView.text = groupWallet
    }

    private fun setClickListener() = viewBinding?.run  {
        sendButton.setOnSingleClickListener {
            callback?.onSend(amountEditText.text.toString())
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface OptionDonateAmountCallback {
        fun onSend(amount: String)
    }

    companion object {
        fun newInstance(
            callback: OptionDonateAmountCallback? = null,
            personalWallet: String,
            groupWallet: String
        ) = OptionDonateDialog()
            .apply {
                this.callback = callback
                this.personalWallet = personalWallet
                this.groupWallet = groupWallet
            }
        val TAG: String = OptionDonateDialog::class.java.simpleName
    }
}