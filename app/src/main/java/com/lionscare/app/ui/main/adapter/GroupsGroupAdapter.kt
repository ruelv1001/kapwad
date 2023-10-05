package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.AdapterGroupGroupBinding
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.loadGroupAvatar

class GroupsGroupAdapter (val context: Context, val clickListener: GroupCallback) :
    RecyclerView.Adapter<GroupsGroupAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<GroupData>()

    fun clear(){
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
        val binding = AdapterGroupGroupBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterGroupGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: GroupData) = with(itemView) {
            binding.titleTextView.text = data.name
            binding.membersTextView.text = context.resources.getQuantityString(
                R.plurals.member_plural, //plural from strings.xml file
                data.member_count?: 0, //quantity
                data.member_count?: 0 //var arg
            )
            binding.referenceTextView.text = data.qrcode
            binding.imageView.loadGroupAvatar(data.avatar?.thumb_path)
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }
            binding.joinButton.setOnClickListener {
                clickListener.onJoinClicked(data)
            }

        }
    }

    interface GroupCallback{
        fun onItemClicked(data: GroupData)
        fun onJoinClicked(data: GroupData)
    }

    override fun getItemCount(): Int = adapterData.size
}