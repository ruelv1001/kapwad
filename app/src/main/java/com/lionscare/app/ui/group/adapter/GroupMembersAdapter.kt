package com.lionscare.app.ui.group.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.AdapterMembersBinding
import com.lionscare.app.ui.billing.viewstate.CustomMemberListDataModel
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.loadAvatar

class GroupMembersAdapter(
    val context: Context,
    val clickListener: MembersCallback,
    val id: String? = null,
    val isInMemberList: Boolean? = true,
    val shouldShowCheckbox: Boolean = false,
    val shouldShowRemoveTextButton: Boolean = false,
) :
    PagingDataAdapter<MemberListData, GroupMembersAdapter.MembersViewHolder>(
        DIFF_CALLBACK
    ) {
    var customMemberListDataModel: MutableList<CustomMemberListDataModel>? = null
        private set

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
                binding.removeTextButton.isVisible = shouldShowRemoveTextButton
                binding.checkBox.isVisible = shouldShowCheckbox
                binding.checkBox.isChecked =
                    customMemberListDataModel?.get(position)?.isChecked == true //if should show checkbox

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
                binding.removeTextButton.setOnClickListener {
                    clickListener.onRemoveButtonClicked(data)
                }
                //to get which data is checked or not
                customMemberListDataModel?.add(
                    CustomMemberListDataModel(
                        memberListData = data,
                        isChecked = binding.checkBox.isChecked
                    )
                )

            }
        }
    }

    fun getCustomData(): List<CustomMemberListDataModel>? {
        return customMemberListDataModel
    }
    fun setCustomData(data: MutableList<CustomMemberListDataModel>) {
        customMemberListDataModel = data
    }

    interface MembersCallback {
        fun onItemClicked(data: MemberListData)
        fun onRemoveButtonClicked(data: MemberListData)
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }
}