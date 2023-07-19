package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.AdapterGroupYourGroupBinding

class GroupsYourGroupAdapter (val context: Context, val clickListener: GroupCallback) :
    RecyclerView.Adapter<GroupsYourGroupAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<ArticleData>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<ArticleData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<ArticleData> = adapterData

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

        fun displayData(data: ArticleData) = with(itemView) {
            binding.titleTextView.text = data.name
            binding.membersTextView.text = data.description
//            binding.articleImageView.loadImage(data.image?.thumb_path, context)
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }
        }
    }

    interface GroupCallback{
        fun onItemClicked(data: ArticleData)
    }

    override fun getItemCount(): Int = adapterData.size
}