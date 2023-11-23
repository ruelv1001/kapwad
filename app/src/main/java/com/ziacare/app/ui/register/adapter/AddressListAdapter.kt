package com.ziacare.app.ui.register.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.data.repositories.address.response.AddressData
import com.ziacare.app.databinding.AdapterAddressBinding


class AddressListAdapter (val context: Context, val clickListener: AddressCallback) :
    RecyclerView.Adapter<AddressListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<AddressData>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<AddressData>) {
        val startAt = adapterData.size
        for(a : AddressData in newData){
            if(!a.name.equals("N/A")){
                adapterData.add(a)
            }
        }
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<AddressData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterAddressBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: AddressData, position: Int) = with(itemView) {
            binding.addressTitleTextView.text = data.name
            binding.addressTitleTextView.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface AddressCallback{
        fun onItemClicked(data: AddressData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}