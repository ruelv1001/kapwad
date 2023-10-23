package com.lionscare.app.ui.group.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lionscare.app.R
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.DialogInviteUserBinding
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteMemberDetailsDialog : BottomSheetDialogFragment() {

    private var viewBinding: DialogInviteUserBinding? = null
    private var callback: MembershipCallback? = null
    private var data = QRData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_invite_user,
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
        viewBinding = DialogInviteUserBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        nameTextView.text = data.name
        contactTextView.text = data.phone_number
        avatarImageView.loadAvatar(data.avatar?.thumb_path, requireContext())
    }

    private fun setClickListener() {
        viewBinding?.inviteButton?.setOnSingleClickListener {
            callback?.onInviteMember(data)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface MembershipCallback {
        fun onInviteMember(memberListData: QRData)
    }

    companion object {
        fun newInstance(
            callback: MembershipCallback? = null,
            data: QRData = QRData()
        ) = InviteMemberDetailsDialog()
            .apply {
                this.callback = callback
                this.data = data
            }

        val TAG: String = InviteMemberDetailsDialog::class.java.simpleName
    }
}