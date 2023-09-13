package com.lionscare.app.ui.group.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.AdapterMembersBinding
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.setOnSingleClickListener

class GroupMembersAdapter(val context: Context, val clickListener: MembersCallback, val id: String? = null, var isUpdating: Boolean = false) :
    PagingDataAdapter<MemberListData, GroupMembersAdapter.MembersViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MemberListData>() {
            override fun areItemsTheSame(oldItem: MemberListData, newItem: MemberListData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MemberListData, newItem: MemberListData): Boolean {
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
        holder.bind(article)
    }

    inner class MembersViewHolder(private val binding: AdapterMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: MemberListData?) {
            data?.let {

                if(id == data.user?.id){
                    binding.youTextView.isVisible = true
                    binding.youTextView.text = "You"
                } else if(id != data.user?.id && data.role == "admin"){
                    binding.youTextView.isVisible = true
                    binding.youTextView.text = "Admin"
                }else{
                    binding.youTextView.isGone = true
                }

                binding.profileImageView.loadAvatar(data.user?.avatar?.thumb_path, context)
                binding.nameTextView.text = data.user?.name
                binding.idNoTextView.text = data.user?.qrcode
                //binding.profileImageView.loadAvatar(data.user?.avatar?.thumb_path )

                if (data.role != "member"){
                    if (isUpdating){
                        //binding.checkImageView.visibility = View.GONE
                        binding.removeImageView.visibility = View.VISIBLE
                        binding.removeImageView.setOnSingleClickListener {
                            clickListener.onRemoveClicked(data)
                        }
                    }
                } else {
                    binding.membersLinearLayout.setOnClickListener {
                        clickListener.onItemClicked(data)
                    }
                }

            }
        }
    }

    interface MembersCallback {
        fun onItemClicked(data: MemberListData)
        fun onRemoveClicked(data: MemberListData)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }
}