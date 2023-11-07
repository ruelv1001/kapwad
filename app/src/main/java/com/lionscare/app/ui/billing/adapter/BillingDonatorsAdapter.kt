package com.lionscare.app.ui.billing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.data.repositories.billing.response.DonatorData
import com.lionscare.app.databinding.AdapterBillingDonatorsBinding
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.currencyFormat

class BillingDonatorsAdapter :   PagingDataAdapter<DonatorData, BillingDonatorsAdapter.AdapterViewHolder>(
    BillingDonatorsAdapter.DIFF_CALLBACK
)  {


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DonatorData>(){
            override fun areItemsTheSame(oldItem: DonatorData, newItem: DonatorData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DonatorData, newItem: DonatorData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterBillingDonatorsBinding.inflate(inflater, parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillingDonatorsAdapter.AdapterViewHolder, position: Int) {
        val donator = getItem(position)
        holder.bind(donator)
    }

    inner class AdapterViewHolder(val binding: AdapterBillingDonatorsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DonatorData?) {
            CommonLogger.instance.sysLogI("bill", data)
            data?.let {
                binding.dateOfDonationText.text = data.date?.date_only_ph
                binding.donatorNameText.text = data.user?.name
                binding.donationAmountText.text = data.amount
            }
        }
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }
}