package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.databinding.AdapterGroupYourGroupBinding

class ImmediateFamilyAdapter (val context: Context, val clickListener: ImmediateFamilyCallback) :
    RecyclerView.Adapter<ImmediateFamilyAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<GroupListData>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<GroupListData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<GroupListData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterGroupYourGroupBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterGroupYourGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: GroupListData) = with(itemView) {
            binding.titleTextView.text = data.group_name
            //  binding.membersTextView.text = data.description
            //  binding.referenceTextView.text = data.reference


//            binding.articleImageView.loadImage(data.image?.thumb_path, context)
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onImmediateFamilyItemClicked(data)
            }
        }
    }

    interface ImmediateFamilyCallback{
        fun onImmediateFamilyItemClicked(data: GroupListData)
    }

    override fun getItemCount(): Int = adapterData.size
}