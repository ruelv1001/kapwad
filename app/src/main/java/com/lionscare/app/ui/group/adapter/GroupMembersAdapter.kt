package com.lionscare.app.ui.group.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.AdapterMembersBinding
import com.lionscare.app.utils.loadAvatar

class GroupMembersAdapter(val clickListener: MembersCallback, val id: String? = null) :
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

                binding.youTextView.isVisible = id == data.user?.id
                binding.nameTextView.text = data.user?.name
                binding.idNoTextView.text = data.user?.qrcode
               // binding.profileImageView.loadAvatar(data.user?.avatar?.thumb_path)

                binding.membersLinearLayout.setOnClickListener {
                    if (binding.checkImageView.isVisible){
                        binding.checkImageView.visibility = View.GONE
                    } else {
                        binding.checkImageView.visibility = View.VISIBLE
                    }
                    clickListener.onItemClicked(data)
                }
            }
        }
    }

    interface MembersCallback {
        fun onItemClicked(data: MemberListData)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }
}