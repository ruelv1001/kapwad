package com.lionscare.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.AdapterGroupAssistanceBinding

class AssistanceAdapter (val context: Context, val clickListener: GroupCallback) :
    RecyclerView.Adapter<AssistanceAdapter.AdapterViewHolder>() {

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
        val binding = AdapterGroupAssistanceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterGroupAssistanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: ArticleData) = with(itemView) {
            binding.requestByTextView.text = data.name
            binding.dateTextView.text = data.description
            binding.refIDTextView.text = data.reference
            binding.amountTextView.text = data.type
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