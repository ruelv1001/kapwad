package com.ziacare.app.ui.billing.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ziacare.app.R
import com.ziacare.app.databinding.DialogDonateBinding
import com.ziacare.app.utils.setAmountFormat
import com.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonateDialog : DialogFragment() {

    private var viewBinding: DialogDonateBinding? = null
    private var callback: DonateAmountCallback? = null
    private var walletBalance = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_donate,
            container,
            false
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogDonateBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        walletBalanceTextView.text = walletBalance
        amountEditText.setAmountFormat()
    }

    private fun setClickListener() = viewBinding?.run  {
        backImageView.setOnSingleClickListener {
            dismiss()
        }
        sendButton.setOnSingleClickListener {
            callback?.onSend(amountEditText.text.toString(),anonymousCheckBox.isChecked, messageEditText.text.toString())
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface DonateAmountCallback {
        fun onSend(amount: String, isAnonymous: Boolean, message: String)
    }

    companion object {
        fun newInstance(
            callback: DonateAmountCallback? = null,
            walletBalance: String
        ) = DonateDialog()
            .apply {
                this.callback = callback
                this.walletBalance = walletBalance
            }
        val TAG: String = DonateDialog::class.java.simpleName
    }
}