package com.lionscare.app.ui.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.AdapterSearchGroupBinding

class GroupsAdapter(val context: Context, val clickListener: OnClickCallback) :
    RecyclerView.Adapter<GroupsAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<GroupData>()

    fun clear() {
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<GroupData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<GroupData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterSearchGroupBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterSearchGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: GroupData) = with(itemView) {

            binding.titleTextView.text = data.name
            binding.referenceTextView.text = data.code
            binding.membersTextView.text = context.resources.getQuantityString(
                R.plurals.member_plural, //plural from strings.xml file
                data.member_count?: 0, //quantity
                data.member_count?: 0 //var arg
            )

            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onGroupItemClickListener(data)
            }

        }
    }

    interface OnClickCallback {
        fun onGroupItemClickListener(data: GroupData)
    }

    override fun getItemCount(): Int = adapterData.size
}