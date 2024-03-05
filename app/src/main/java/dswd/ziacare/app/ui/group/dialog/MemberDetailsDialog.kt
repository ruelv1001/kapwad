package dswd.ziacare.app.ui.group.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.member.response.MemberListData
import dswd.ziacare.app.databinding.DialogUserDetailsBinding
import dswd.ziacare.app.utils.loadAvatar
import dswd.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberDetailsDialog : BottomSheetDialogFragment() {

    private var viewBinding: DialogUserDetailsBinding? = null
    private var callback: MembershipCallback? = null
    private var data = MemberListData()
    private var isUserAdmin = false
    private var isUserOwner = false
    private var ownerId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_user_details,
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
        viewBinding = DialogUserDetailsBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {
        avatarImageView.loadAvatar(data.user?.avatar?.thumb_path, requireContext())
        nameTextView.text = data.user?.name
        idTextView.text = data.user?.qrcode?.replace("....".toRegex(), "$0 ")
        avatarImageView.loadAvatar(data.user?.avatar?.full_path.orEmpty(), requireActivity())
        membersLinearLayout.isVisible = ((isUserAdmin && data.role != "admin") || isUserOwner) && data.user?.id != ownerId
        transferOwnershipButton.isVisible = isUserOwner
    }

    private fun setClickListener() {
        viewBinding?.removeButton?.setOnSingleClickListener {
            callback?.onRemoveMember(data)
            dismiss()
        }
        viewBinding?.sendPointsButton?.setOnSingleClickListener {
            callback?.onSendPoint(data)
            dismiss()
        }
        viewBinding?.transferOwnershipButton?.setOnSingleClickListener {
            callback?.onTransferOwnership(data)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface MembershipCallback {
        fun onRemoveMember(memberListData: MemberListData)
        fun onSendPoint(memberListData: MemberListData)
        fun onTransferOwnership(memberListData: MemberListData)
    }

    companion object {
        fun newInstance(
            callback: MembershipCallback? = null,
            data: MemberListData = MemberListData(),
            isUserAdmin : Boolean,
            isUserOwner: Boolean,
            ownerId : String
        ) = MemberDetailsDialog()
            .apply {
                this.callback = callback
                this.data = data
                this.isUserAdmin = isUserAdmin
                this.isUserOwner = isUserOwner
                this.ownerId = ownerId
            }

        val TAG: String = MemberDetailsDialog::class.java.simpleName
    }
}