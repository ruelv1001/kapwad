package com.lionscare.app.ui.wallet.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.AdapterMembersBinding

class MembersAdapter(val clickListener: MembersCallback) :
    PagingDataAdapter<SampleData, MembersAdapter.MembersViewHolder>(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterMembersBinding.inflate(inflater, parent, false)
        return MembersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class MembersViewHolder(private val binding: AdapterMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SampleData?) {
            data?.let {

                binding.profileImageView.setImageResource(data.id?:0)
                binding.nameTextView.text = data.title
                binding.idNoTextView.text = data.amount

                binding.membersLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
            }
        }
    }

    interface MembersCallback {
        fun onItemClicked(data: SampleData)
    }

}