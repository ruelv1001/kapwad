package com.lionscare.app.ui.badge.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lionscare.app.R
import com.lionscare.app.databinding.DialogReasonBinding

class ReasonBottomSheetDialog() : BottomSheetDialogFragment() {
    private var _binding: DialogReasonBinding? = null
    private val binding get() = _binding!!

    private var callback: ReasonDialogCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReasonBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }



    private fun setClickListener() {
        binding.removeBadgeButton.setOnClickListener {
            callback?.onRemoveBadgeButtonClicked(this, binding.reasonTextField.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface ReasonDialogCallback {
        fun onRemoveBadgeButtonClicked(dialog: ReasonBottomSheetDialog, reason : String)
    }

    companion object {
        fun newInstance(callback: ReasonDialogCallback? = null) = ReasonBottomSheetDialog()
            .apply {
                this.callback = callback
            }
        val TAG: String = ReasonDialogCallback::class.java.simpleName
    }
}
