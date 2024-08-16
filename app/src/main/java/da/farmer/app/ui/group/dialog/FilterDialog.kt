package da.farmer.app.ui.group.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import da.farmer.app.R
import da.farmer.app.databinding.DialogFilterBinding
import da.farmer.app.utils.setOnSingleClickListener

class FilterDialog : DialogFragment()  {

    private var viewBinding: DialogFilterBinding? = null
    private var callback: FilterDialogListener? = null
    private var selectedCheckboxes: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_filter,
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
        viewBinding = DialogFilterBinding.bind(view)
        setClickListener()
    }

    private fun setClickListener() = viewBinding?.run {
        okButton.setOnSingleClickListener {
            if(approvedCheckBox.isChecked){
                selectedCheckboxes.add(approvedCheckBox.text.toString().replaceFirstChar(Char::lowercase))
            }
            if(cancelledCheckBox.isChecked){
                selectedCheckboxes.add(cancelledCheckBox.text.toString().replaceFirstChar(Char::lowercase))
            }
            if(declinedCheckBox.isChecked){
                selectedCheckboxes.add(declinedCheckBox.text.toString().replaceFirstChar(Char::lowercase))
            }
            if(pendingCheckBox.isChecked){
                selectedCheckboxes.add(pendingCheckBox.text.toString().replaceFirstChar(Char::lowercase))
            }
            callback?.onFilter(selectedCheckboxes)
            dismiss()
        }
        closeImageView.setOnSingleClickListener {
            callback?.onCancel()
            dismiss()
        }
    }

    interface FilterDialogListener {
        fun onFilter(filter: ArrayList<String>)
        fun onCancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    companion object {
        fun newInstance(
            callback: FilterDialogListener? = null
        ) = FilterDialog().apply {
            this.callback = callback
        }
        val TAG: String = FilterDialog::class.java.simpleName
    }
}