package com.lionscare.app.ui.wallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.databinding.AdapterInboundOutboundBinding

class InboundOutboundAdapter(val clickListener: InboundOutboundCallback) :
    PagingDataAdapter<TransactionData, InboundOutboundAdapter.InboundOutboundViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionData>() {
            override fun areItemsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TransactionData, newItem: TransactionData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboundOutboundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterInboundOutboundBinding.inflate(inflater, parent, false)
        return InboundOutboundViewHolder(binding)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }

    override fun onBindViewHolder(holder: InboundOutboundViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class InboundOutboundViewHolder(private val binding: AdapterInboundOutboundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: TransactionData?) {
            data?.let {

                if (data.type == "credit") {
                    binding.iconImageView.setImageResource(R.drawable.ic_inbound)
                    binding.amountTextView.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_primary))
                    binding.amountTextView.text = "+${data.value}"
                } else {
                    binding.iconImageView.setImageResource(R.drawable.ic_outbound)
                    binding.amountTextView.setTextColor(ContextCompat.getColor(itemView.context,R.color.red))
                    binding.amountTextView.text = "-${data.value}"
                }

                binding.titleTextView.text = data.title

                val received = data.remarks?.substringBefore("Refid : ")?.trim()
                val refId = data.remarks?.substringAfter("Refid : ")?.substringBefore(" ")?.trim()
                binding.receivedTextView.text = received
                binding.refidTextView.text = refId
                binding.dateTextView.text = data.date_registered?.time_passed

                binding.inboundOutboundLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
            }
        }
    }

    interface InboundOutboundCallback {
        fun onItemClicked(data: TransactionData)
    }
}