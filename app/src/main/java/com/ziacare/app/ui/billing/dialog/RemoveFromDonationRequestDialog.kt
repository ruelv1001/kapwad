package com.ziacare.app.ui.billing.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ziacare.app.R
import com.ziacare.app.databinding.DialogRemoveFromDonationRequestBinding
import com.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoveFromDonationRequestDialog : BottomSheetDialogFragment() {

    private var callback: RemoveFromDonationRequestCallback? = null
    private var _binding: DialogRemoveFromDonationRequestBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogRemoveFromDonationRequestBinding.inflate(
            inflater,
            container,
            false
        )
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
        binding.yesButton.setOnSingleClickListener {
            callback?.onRemoveFromRequest(this)
        }
        binding.noButton.setOnSingleClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface RemoveFromDonationRequestCallback {
        fun onRemoveFromRequest(dialog: RemoveFromDonationRequestDialog)
    }

    companion object {
        fun newInstance(callback: RemoveFromDonationRequestCallback? = null) =
            RemoveFromDonationRequestDialog()
                .apply {
                    this.callback = callback
                }

        val TAG: String = RemoveFromDonationRequestDialog::class.java.simpleName
    }
}