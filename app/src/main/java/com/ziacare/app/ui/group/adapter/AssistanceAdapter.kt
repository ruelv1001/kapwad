package com.ziacare.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.R
import com.ziacare.app.data.repositories.article.response.ArticleData
import com.ziacare.app.data.repositories.assistance.response.CreateAssistanceData
import com.ziacare.app.data.repositories.member.response.MemberListData
import com.ziacare.app.data.repositories.member.response.PendingMemberData
import com.ziacare.app.databinding.AdapterGroupAssistanceBinding
import com.ziacare.app.databinding.AdapterMembersBinding
import com.ziacare.app.utils.currencyFormat

class AssistanceAdapter(val context: Context, val clickListener: GroupCallback) :
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
                binding.titleTextView.text = data.reason.toString().replaceFirstChar (Char :: titlecase)
                binding.requestByTextView.text = data.user?.name
                binding.dateTextView.text = data.date_requested?.datetime_ph
                binding.refIDTextView.text = data.status.toString().replaceFirstChar (Char :: titlecase)
                when(data.status){
                    "declined"-> {
                        binding.refIDTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
                    }
                    "cancelled"-> {
                        binding.refIDTextView.setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                    }
                    "approved" ->{
                        binding.refIDTextView.setTextColor(ContextCompat.getColor(context, R.color.blue_text))
                    }
                    else-> { // pending
                        binding.refIDTextView.setTextColor(ContextCompat.getColor(context, R.color.green))
                    }
                }
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