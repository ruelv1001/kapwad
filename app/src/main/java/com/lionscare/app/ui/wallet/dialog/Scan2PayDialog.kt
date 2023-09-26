package com.lionscare.app.ui.wallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.lionscare.app.R
import com.lionscare.app.databinding.DialogSendPointsBinding
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Scan2PayDialog : DialogFragment() {

    private var viewBinding: DialogSendPointsBinding? = null
    private var businessName = ""
    private var amount = ""
    private var remarks = ""
    private var callback: Scan2PayListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_send_points,
            container,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            setLayout(width, height)
            setBackgroundDrawableResource(R.color.my_card_transparent)
            setGravity(Gravity.CENTER)
            isCancelable = true
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogSendPointsBinding.bind(view)
        setClickListener()
        setupViews()
    }

    private fun setupViews() = viewBinding?.run {
        establishmentTextView.text = businessName
        if (amount.isNotEmpty()){
            textViewLinearLayout.isVisible = true
            editTextLinearLayout.isGone = true
            amountTextView.text = amount
            remarksTextView.text = remarks
        }else{
            textViewLinearLayout.isGone = true
            editTextLinearLayout.isVisible = true
        }
    }

    private fun setClickListener() {
        viewBinding?.dismissImageView?.setOnSingleClickListener {
            dismiss()
        }
        viewBinding?.sendButton?.setOnSingleClickListener {
            if (amount.isEmpty()){
                amount = viewBinding?.amountEditText?.text.toString()
                remarks = viewBinding?.remarksEditText?.text.toString()
            }
            callback?.onProceed(amount, remarks)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface Scan2PayListener {
        fun onProceed(amount: String, remarks: String)
    }

    companion object {
        fun newInstance(
            businessName: String,
            amount:String,
            remarks: String,
            callback: Scan2PayListener? = null
        ) = Scan2PayDialog()
            .apply {
                this.businessName = businessName
                this.amount = amount
                this.remarks = remarks
                this.callback = callback
            }
        val TAG: String = Scan2PayDialog::class.java.simpleName
    }
}