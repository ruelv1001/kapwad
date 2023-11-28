package com.ziacare.app.ui.profile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ziacare.app.R
import com.ziacare.app.databinding.DialogRegisterSuccessBinding
import com.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationSuccessDialog : BottomSheetDialogFragment(){

    private var viewBinding: DialogRegisterSuccessBinding? = null
    private var callback: RegisterSuccessCallBack? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_register_success,
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
        viewBinding = DialogRegisterSuccessBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        titleTextView.text = "Verification Sent"
        myAcctButton.text = "Done"
        descriptionTextView.text = "In order to complete your profile, please check and go to the email we sent and verify your account."
    }

    private fun setClickListener() {
        viewBinding?.myAcctButton?.setOnSingleClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface RegisterSuccessCallBack {
        fun onMyAccountClicked(cityName: String, citySku: String, zipCode: String)
    }

    companion object {
        fun newInstance(callback: RegisterSuccessCallBack? = null) = VerificationSuccessDialog()
            .apply {
                this.callback = callback
            }

        val TAG: String = VerificationSuccessDialog::class.java.simpleName
    }
}