package kapwad.reader.app.ui.profile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogProfileConfirmationBinding
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileConfirmationDialog : BottomSheetDialogFragment(){

    private var viewBinding: DialogProfileConfirmationBinding? = null
    private var callback: ProfileSaveDialogCallBack? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_profile_confirmation,
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
        viewBinding = DialogProfileConfirmationBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {

    }

    private fun setClickListener() {
        viewBinding?.cancelledButton?.setOnSingleClickListener {
            dismiss()
        }
        viewBinding?.confirmButton?.setOnSingleClickListener {
            callback?.onMyAccountClicked(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface ProfileSaveDialogCallBack {
        fun onMyAccountClicked(dialog: ProfileConfirmationDialog)
    }

    companion object {
        fun newInstance(callback: ProfileSaveDialogCallBack? = null) = ProfileConfirmationDialog()
            .apply {
                this.callback = callback
            }

        val TAG: String = ProfileConfirmationDialog::class.java.simpleName
    }
}