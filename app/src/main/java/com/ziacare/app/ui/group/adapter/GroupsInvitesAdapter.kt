package com.ziacare.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.data.model.SampleData
import com.ziacare.app.data.repositories.member.response.PendingMemberData
import com.ziacare.app.data.repositories.member.response.PendingMemberResponse
import com.ziacare.app.databinding.AdapterGroupPendingRequestBinding
import com.ziacare.app.databinding.AdapterMembersBinding
import com.ziacare.app.utils.loadAvatar

class GroupsInvitesAdapter(val context: Context, val clickListener: GroupCallback, val isAdmin: Boolean? = false) :
    PagingDataAdapter<PendingMemberData, GroupsInvitesAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PendingMemberData>() {
            override fun areItemsTheSame(
                oldItem: PendingMemberData,
                newItem: PendingMemberData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PendingMemberData,
                newItem: PendingMemberData
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGroupPendingRequestBinding.inflate(inflater, parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val pendingMemberData = getItem(position)
        holder.bind(pendingMemberData)
    }

    inner class AdapterViewHolder(val binding: AdapterGroupPendingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PendingMemberData?) {
            data?.let {
                binding.imageView.loadAvatar(data.user?.avatar?.thumb_path, context)
                binding.titleTextView.text = data.user?.name
                binding.membersTextView.text = data.user?.qrcode
                binding.referenceTextView.visibility = View.GONE
                // binding.imageView.loadImage(data.user?.avatar?.thumb_path, context)

                if (data.type == "request" && isAdmin == true) {
                    binding.acceptTextView.visibility = View.VISIBLE
                    binding.declineTextView.visibility = View.VISIBLE
                    binding.cancelTextView.visibility = View.GONE
                } else if (data.type == "invite" && isAdmin == true){
                    binding.acceptTextView.visibility = View.GONE
                    binding.declineTextView.visibility = View.GONE
                    binding.cancelTextView.visibility = View.VISIBLE
                }else{
                    binding.acceptTextView.visibility = View.GONE
                    binding.declineTextView.visibility = View.GONE
                    binding.cancelTextView.visibility = View.GONE
                }

                binding.adapterLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
                binding.acceptTextView.setOnClickListener {
                    clickListener.onAcceptClicked(data)
                }
                binding.declineTextView.setOnClickListener {
                    clickListener.onDeclineClicked(data)
                }
                binding.cancelTextView.setOnClickListener {
                    clickListener.onCancelClicked(data)
                }
            }
        }
    }

    interface GroupCallback {
        fun onItemClicked(data: PendingMemberData)
        fun onAcceptClicked(data: PendingMemberData)
        fun onDeclineClicked(data: PendingMemberData)
        fun onCancelClicked(data: PendingMemberData)
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }

}