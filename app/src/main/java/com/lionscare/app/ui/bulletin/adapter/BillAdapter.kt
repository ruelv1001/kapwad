package com.lionscare.app.ui.bulletin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.AdapterBillBinding

class BillAdapter: PagingDataAdapter<SampleData, BillAdapter.AdapterViewHolder>(
    BillAdapter.DIFF_CALLBACK
)  {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SampleData>(){
            override fun areItemsTheSame(oldItem: SampleData, newItem: SampleData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SampleData, newItem: SampleData): Boolean {
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
        fun bind(data: SampleData?) {
            data?.let {
               /* binding.billNumberTextView.text = bill number
                binding.nameTextView.text = name
                binding.amountDueTextView.text = data.amount
                binding.donatedAmountTextView.text = donated amount
                binding.dueDateTextView.text = due date
                binding.statusTextView.text = status

                if(binding.statusTextView.equals("Completed")){
                binding.statusTextView.setTextColor(context.getColor(R.color.color_primary))
                }else if(binding.statusTextView.equals("Cancelled")){
                binding.statusTextView.setTextColor(context.getColor(R.color.declined))
                }else{
                binding.statusTextView.setTextColor(context.getColor(R.color.pending))
                }
                */
            }
        }
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }
}