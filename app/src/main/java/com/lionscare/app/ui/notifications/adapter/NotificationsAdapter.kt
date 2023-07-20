package com.lionscare.app.ui.notifications.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.AdapterNotificationsBinding

class NotificationsAdapter (val clickListener: NotificationsCallback) :
    PagingDataAdapter<SampleData, NotificationsAdapter.NotificationsViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SampleData>() {
            override fun areItemsTheSame(oldItem: SampleData, newItem: SampleData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SampleData, newItem: SampleData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterNotificationsBinding.inflate(inflater, parent, false)
        return NotificationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class NotificationsViewHolder(private val binding: AdapterNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SampleData?) {
            data?.let {

                if (data.title?.contains("badge") == true) {
                    binding.iconImageView.setImageResource(R.drawable.ic_notif_badge)
                } else {
                    binding.iconImageView.setImageResource(R.drawable.ic_notif_group)
                }
                binding.titleTextView.text = data.title
                binding.dateTextView.text = data.date

                binding.inboundOutboundLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
            }
        }
    }

    interface NotificationsCallback {
        fun onItemClicked(data: SampleData)
    }
}