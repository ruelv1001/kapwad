package com.lionscare.app.ui.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.AdapterSearchUserBinding

class MembersAdapter(val context: Context, val clickListener: OnClickCallback) :
    RecyclerView.Adapter<MembersAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<QRData>()

    fun clear() {
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<QRData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<QRData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterSearchUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: QRData) = with(itemView) {

            binding.nameTextView.text = data.name
            binding.idNoTextView.isGone = true


            binding.membersLinearLayout.setOnClickListener {
                clickListener.onItemClickListener(data)
            }

        }
    }

    interface OnClickCallback {
        fun onItemClickListener(data: QRData)
    }

    override fun getItemCount(): Int = adapterData.size
}