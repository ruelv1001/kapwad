package com.lionscare.app.ui.billing.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lionscare.app.R
import com.lionscare.app.databinding.DialogDonateBinding
import com.lionscare.app.utils.setAmountFormat
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DonateDialog : BottomSheetDialogFragment() {

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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
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
        sendButton.setOnSingleClickListener {
            callback?.onSend(amountEditText.text.toString(),anonymousCheckBox.isChecked)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface DonateAmountCallback {
        fun onSend(amount: String, isAnonymous: Boolean)
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