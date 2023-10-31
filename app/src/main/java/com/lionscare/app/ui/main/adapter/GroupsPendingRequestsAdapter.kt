package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.data.repositories.group.response.PendingGroupRequestData
import com.lionscare.app.databinding.AdapterGroupPendingRequestBinding
import com.lionscare.app.databinding.AdapterGroupYourGroupBinding
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.loadGroupAvatar

class GroupsPendingRequestsAdapter (val context: Context, val clickListener: GroupCallback) :
    PagingDataAdapter<PendingGroupRequestData, GroupsPendingRequestsAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    private var groupRequestCount = 0

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PendingGroupRequestData>() {
            override fun areItemsTheSame(oldItem: PendingGroupRequestData, newItem: PendingGroupRequestData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PendingGroupRequestData, newItem: PendingGroupRequestData): Boolean {
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
        val data = getItem(position)
        holder.bind(data)
    }

    inner class AdapterViewHolder(val binding: AdapterGroupPendingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PendingGroupRequestData?){
            data?.let {
                if(data.type == "request"){
                    groupRequestCount++
                }
                binding.titleTextView.text = data.group?.name
                binding.membersTextView.text = context.resources.getQuantityString(
                    R.plurals.member_plural, //plural from strings.xml file
                    data.group?.member_count?: 0, //quantity
                    data.group?.member_count?: 0 //var arg
                )
                binding.referenceTextView.text = data.group?.qrcode
                binding.imageView.loadGroupAvatar(data.group?.avatar?.thumb_path)
                
                binding.cancelTextView.text = context.getString(R.string.lbl_cancel_join_request)

                binding.cancelTextView.isVisible = data.type == "request"
                binding.acceptTextView.isVisible = data.type == "invite"
                binding.declineTextView.isVisible = data.type == "invite"

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

    interface GroupCallback{
        fun onItemClicked(data: PendingGroupRequestData)
        fun onAcceptClicked(data: PendingGroupRequestData)
        fun onDeclineClicked(data: PendingGroupRequestData)
        fun onCancelClicked(data: PendingGroupRequestData)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }

    fun getSelfGroupRequestCount() : Int {
        return groupRequestCount
    }
}