package com.lionscare.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.data.repositories.member.response.PendingMemberData
import com.lionscare.app.databinding.AdapterGroupAssistanceBinding
import com.lionscare.app.databinding.AdapterMembersBinding
import com.lionscare.app.utils.currencyFormat

class AssistanceAdapter(val clickListener: GroupCallback) :
    PagingDataAdapter<CreateAssistanceData, AssistanceAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CreateAssistanceData>() {
            override fun areItemsTheSame(oldItem: CreateAssistanceData, newItem: CreateAssistanceData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CreateAssistanceData, newItem: CreateAssistanceData): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGroupAssistanceBinding.inflate(inflater, parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val assistanceRequestData = getItem(position)
        holder.bind(assistanceRequestData)
    }

    inner class AdapterViewHolder(val binding: AdapterGroupAssistanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CreateAssistanceData?) {
            data?.let {
                binding.titleTextView.text = data.status.toString().replaceFirstChar (Char :: titlecase)
                binding.requestByTextView.text = data.user?.name
                binding.dateTextView.text = data.date_created?.datetime_ph
                binding.refIDTextView.text = data.reference_id
                binding.amountTextView.text = currencyFormat(data.amount.toString())
//            binding.articleImageView.loadImage(data.image?.thumb_path, context)
                binding.adapterLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
            }
        }

    }

    interface GroupCallback {
        fun onItemClicked(data: CreateAssistanceData)
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }
}