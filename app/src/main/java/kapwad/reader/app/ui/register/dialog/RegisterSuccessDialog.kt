package kapwad.reader.app.ui.register.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogRegisterSuccessBinding
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterSuccessDialog : BottomSheetDialogFragment(){

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
    }

    private fun setClickListener() {
        viewBinding?.myAcctButton?.setOnSingleClickListener {
            dismiss()
            callback?.onMyAccountClicked("","","")
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
        fun newInstance(callback: RegisterSuccessCallBack? = null) = RegisterSuccessDialog()
            .apply {
                this.callback = callback
            }

        val TAG: String = RegisterSuccessDialog::class.java.simpleName
    }
}