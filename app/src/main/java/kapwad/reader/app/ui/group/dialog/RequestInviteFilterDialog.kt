package kapwad.reader.app.ui.group.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogRequestInviteFilterBinding
import kapwad.reader.app.utils.setOnSingleClickListener

class RequestInviteFilterDialog : DialogFragment()  {

    private var viewBinding: DialogRequestInviteFilterBinding? = null
    private var callback: RequestInviteFilterDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_request_invite_filter,
            container,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            val width = WindowManager.LayoutParams.MATCH_PARENT
            val height = WindowManager.LayoutParams.MATCH_PARENT
            setLayout(width, height)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogRequestInviteFilterBinding.bind(view)
        setClickListener()
    }

    private fun setClickListener() = viewBinding?.run {
        okButton.setOnSingleClickListener {
            if(requestRadioButton.isChecked){
                callback?.onFilter(requestRadioButton.text.toString())
            }
            if(inviteRadioButton.isChecked){
                callback?.onFilter(inviteRadioButton.text.toString())
            }
            dismiss()
        }
        closeImageView.setOnSingleClickListener {
            callback?.onCancel()
            dismiss()
        }
    }

    interface RequestInviteFilterDialogListener {
        fun onFilter(filter: String)
        fun onCancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    companion object {
        fun newInstance(
            callback: RequestInviteFilterDialogListener? = null
        ) = RequestInviteFilterDialog().apply {
            this.callback = callback
        }
        val TAG: String = RequestInviteFilterDialog::class.java.simpleName
    }
}