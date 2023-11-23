package com.ziacare.app.ui.bulletin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.R
import com.ziacare.app.data.repositories.billing.response.BillData
import com.ziacare.app.databinding.AdapterBillBinding
import com.ziacare.app.utils.setOnSingleClickListener

class BillAdapter(val context: Context,
                  val clickListener: OnClickCallback): PagingDataAdapter<BillData, BillAdapter.AdapterViewHolder>(
    DIFF_CALLBACK
)  {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BillData>(){
            override fun areItemsTheSame(oldItem: BillData, newItem: BillData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BillData, newItem: BillData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterBillBinding.inflate(inflater, parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val bill = getItem(position)
        holder.bind(bill)
    }

    inner class AdapterViewHolder(val binding: AdapterBillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BillData?) {
            data?.let {
                binding.adapterLinearLayout.setOnSingleClickListener {
                    clickListener.onItemClicked(data)
                }

                when(data.status?.lowercase()){
                    "completed" -> {
                        binding.statusTextView.backgroundTintList =
                            ContextCompat.getColorStateList(context,R.color.approved)
                    }
                    "ongoing" -> {
                        binding.statusTextView.backgroundTintList =
                            ContextCompat.getColorStateList(context,R.color.ongoing)
                    }
                    "cancelled" -> {
                        binding.statusTextView.backgroundTintList =
                            ContextCompat.getColorStateList(context,R.color.declined)
                    }
                }
                binding.billNumberTextView.text = data.code
                binding.nameTextView.text = data.name
                binding.amountDueTextView.text = data.display_amount
                binding.donatedAmountTextView.text = data.display_donated_amount
                binding.dueDateTextView.text = data.due_date?.date_only_ph
                binding.statusTextView.text = data.status

            }
        }
    }

    interface OnClickCallback {
        fun onItemClicked(data: BillData)
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }
}