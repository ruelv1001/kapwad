package kapwad.reader.app.ui.group.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogRemoveConfirmationBinding
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveConfirmationDialog : BottomSheetDialogFragment(){

    private var viewBinding: DialogRemoveConfirmationBinding? = null
    private var callback: ConfirmationCallback? = null
    private var title = ""
    private var groupId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_remove_confirmation,
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
        viewBinding = DialogRemoveConfirmationBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        titleTextView.text = title
    }

    private fun setClickListener() {
        viewBinding?.cancelledButton?.setOnSingleClickListener {
            dismiss()
        }
        viewBinding?.confirmButton?.setOnSingleClickListener {
            callback?.onConfirm(groupId)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface ConfirmationCallback {
        fun onConfirm(id: String)
    }

    companion object {
        fun newInstance(callback: ConfirmationCallback? = null, title: String, groupId: String? = null) = RemoveConfirmationDialog()
            .apply {
                this.callback = callback
                this.title = title
                this.groupId = groupId.toString()
            }

        val TAG: String = RemoveConfirmationDialog::class.java.simpleName
    }
}