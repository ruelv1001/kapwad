package com.lionscare.app.ui.group.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lionscare.app.R
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.databinding.DialogJoinGroupConfirmationBinding
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinGroupConfirmationDialog : BottomSheetDialogFragment() {

    private var viewBinding: DialogJoinGroupConfirmationBinding? = null
    private var callback: ConfirmationCallback? = null
    private var title = ""
    private var data = GroupData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_join_group_confirmation,
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
        viewBinding = DialogJoinGroupConfirmationBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        titleTextView.text = title
        privateLinearLayout.isVisible = data.privacy == "private"
    }

    private fun setClickListener() {
        viewBinding?.cancelledButton?.setOnSingleClickListener {
            dismiss()
        }
        viewBinding?.confirmButton?.setOnSingleClickListener {
            callback?.onConfirm(
                group_id = data.id.toString(),
                privacy = data.privacy.toString(),
                passcode = viewBinding?.passcodeEditText?.text.toString()
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface ConfirmationCallback {
        fun onConfirm(group_id: String, privacy: String, passcode: String)
    }

    companion object {
        fun newInstance(
            callback: ConfirmationCallback? = null,
            title: String,
            data: GroupData = GroupData()
        ) = JoinGroupConfirmationDialog()
            .apply {
                this.callback = callback
                this.title = title
                this.data = data
            }

        val TAG: String = JoinGroupConfirmationDialog::class.java.simpleName
    }
}