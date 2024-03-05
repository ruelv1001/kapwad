package dswd.ziacare.app.ui.wallet.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dswd.ziacare.app.R
import dswd.ziacare.app.databinding.DialogConfirmRequestPointsBinding
import dswd.ziacare.app.utils.setOnSingleClickListener

class ConfirmRequestPointsDialog  : BottomSheetDialogFragment(){

    private var viewBinding: DialogConfirmRequestPointsBinding? = null
    private var callback: ConfirmRequestPointsCallBack? = null
    private var points: String? = null
    private var to: String? = null
    private var reason: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_confirm_request_points,
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
        viewBinding = DialogConfirmRequestPointsBinding.bind(view)
        setClickListener()
        setDetails()
    }

    private fun setClickListener() {
        viewBinding?.postRequestButton?.setOnSingleClickListener {
            dismiss()
        }
    }

    private fun setDetails() = viewBinding?.run {
        pointsTextView.text = points.toString()
        toTextView.text = to.toString()
        reasonTextView.text = reason.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface ConfirmRequestPointsCallBack {
        fun onButtonClicked(points: String, to: String)
    }

    companion object {
        fun newInstance(points: String, to: String, reason: String,callback: ConfirmRequestPointsCallBack? = null) = ConfirmRequestPointsDialog()
            .apply {
                this.callback = callback
                this.points = points
                this.to = to
                this.reason = reason
            }

        val TAG: String = ConfirmRequestPointsDialog::class.java.simpleName
    }
}