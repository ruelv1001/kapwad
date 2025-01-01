package kapwad.reader.app.ui.group.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.repositories.member.response.MemberListData
import kapwad.reader.app.databinding.AdapterMembersBinding
import kapwad.reader.app.utils.CommonLogger
import kapwad.reader.app.utils.loadAvatar

class GroupMembersAdapter(
    val context: Context,
    val clickListener: MembersCallback,
    val id: String? = null,
    val isInMemberList: Boolean? = true,
    val shouldShowDonationRequestsViews: Boolean? = null,
) :
    PagingDataAdapter<MemberListData, GroupMembersAdapter.MembersViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemberListData>() {
            override fun areItemsTheSame(
                oldItem: MemberListData,
                newItem: MemberListData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MemberListData,
                newItem: MemberListData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterMembersBinding.inflate(inflater, parent, false)
        return MembersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article, position)
    }

    inner class MembersViewHolder(private val binding: AdapterMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: MemberListData?, position: Int) {
            data?.let {
                binding.checkBox.isVisible = shouldShowDonationRequestsViews != null
                CommonLogger.sysLog("CLICKED", data.id)
                if (id == data.user?.id) {
                    binding.youTextView.isVisible = true
                    binding.youTextView.text = "You"
                } else if (id != data.user?.id && data.role == "owner") {
                    binding.youTextView.isVisible = true
                    binding.youTextView.text = "Owner"
                } else if (id != data.user?.id && data.role == "admin" && isInMemberList == true) {
                    binding.youTextView.isVisible = true
                    binding.youTextView.text = "Co-Admin"
                } else {
                    binding.youTextView.isGone = true
                }

                binding.profileImageView.loadAvatar(data.user?.avatar?.thumb_path, context)
                binding.nameTextView.text = data.user?.name
                binding.idNoTextView.text = data.user?.qrcode
                //binding.profileImageView.loadAvatar(data.user?.avatar?.thumb_path )


                binding.membersLinearLayout.setOnClickListener {
                    if (id != data.user?.id) { // the user must not be able to click himself/herself
                        clickListener.onItemClicked(data)
                    }
                }
            }
        }
    }

    interface MembersCallback {
        fun onItemClicked(data: MemberListData)
    }

    private var onRemoveButtonClicked: ((MemberListData) -> Unit)? = null
    fun setOnRemoveButtonClicked(listener : (MemberListData)-> Unit){
        onRemoveButtonClicked = listener
    }
    fun hasData(): Boolean {
        return itemCount != 0
    }
}