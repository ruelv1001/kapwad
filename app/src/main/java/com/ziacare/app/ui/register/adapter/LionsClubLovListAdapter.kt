package com.ziacare.app.ui.register.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.R
import com.ziacare.app.data.repositories.address.response.AddressData
import com.ziacare.app.data.repositories.profile.response.LOVData
import com.ziacare.app.databinding.AdapterAddressBinding


class LionsClubLovListAdapter(val clickListener: LionsClubLovListAdapter.RegionCallback):
    RecyclerView.Adapter<LionsClubLovListAdapter.AdapterViewHolder>(){

    inner class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<LOVData>(){
        override fun areItemsTheSame(oldItem: LOVData, newItem: LOVData): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: LOVData, newItem: LOVData): Boolean {
            return oldItem == newItem
        }
    }


    //for efficiency
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LionsClubLovListAdapter.AdapterViewHolder {
        return AdapterViewHolder(
            AdapterAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: LionsClubLovListAdapter.AdapterViewHolder, position: Int) {
        AdapterAddressBinding.bind(holder.itemView).apply {
            val lov = differ.currentList[position]
            if(lov.code == "Deselect"){
                addressTitleTextView.setTextColor(ContextCompat.getColor(this.root.context, R.color.red))
            }
            addressTitleTextView.text = lov.value

            this.root.setOnClickListener {
                clickListener.onItemClicked(lov, position)
            }
        }
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface RegionCallback{
        fun onItemClicked(data: LOVData, position: Int)
    }


}