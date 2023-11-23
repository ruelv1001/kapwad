package com.ziacare.app.ui.group.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ziacare.app.R
import com.ziacare.app.databinding.DialogSaveSuccessBinding
import com.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveSuccessDialog : BottomSheetDialogFragment(){

    private var viewBinding: DialogSaveSuccessBinding? = null
    private var callback: RegisterSuccessCallBack? = null
    private var title = ""
    private var content = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_save_success,
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
        viewBinding = DialogSaveSuccessBinding.bind(view)
        setView()
    }

    private fun setView() = viewBinding?.run {

        titleTextView.text = title
        contentTextView.text = content

        doneButton.setOnSingleClickListener {
            callback?.onMyAccountClicked()
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface RegisterSuccessCallBack {
        fun onMyAccountClicked()
    }

    companion object {
        fun newInstance(callback: RegisterSuccessCallBack? = null, title: String? = null, content: String? = null) = SaveSuccessDialog()
            .apply {
                this.callback = callback
                this.title = title.toString()
                this.content = content.toString()
            }

        val TAG: String = SaveSuccessDialog::class.java.simpleName
    }
}