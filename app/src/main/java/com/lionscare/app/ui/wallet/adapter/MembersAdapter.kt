package com.lionscare.app.ui.wallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.AdapterSearchUserBinding

class MembersAdapter(val clickListener: MembersCallback) :
    PagingDataAdapter<QRData, MembersAdapter.MembersViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QRData>() {
            override fun areItemsTheSame(oldItem: QRData, newItem: QRData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: QRData, newItem: QRData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSearchUserBinding.inflate(inflater, parent, false)
        return MembersViewHolder(binding)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class MembersViewHolder(private val binding: AdapterSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: QRData?) {
            data?.let {
                binding.nameTextView.text = data.name
                //binding.idNoTextView.text = data.amount

                binding.membersLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
            }
        }
    }

    interface MembersCallback {
        fun onItemClicked(data: QRData)
    }

}