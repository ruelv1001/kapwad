package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.AdapterGroupInvitesBinding

class GroupsInvitesAdapter (val context: Context, val clickListener: GroupCallback) :
    RecyclerView.Adapter<GroupsInvitesAdapter.AdapterViewHolder>() {

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
        val binding = AdapterGroupInvitesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterGroupInvitesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: ArticleData) = with(itemView) {
            binding.titleTextView.text = data.name
            binding.membersTextView.text = data.description
//            binding.articleImageView.loadImage(data.image?.thumb_path, context)
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }
            binding.acceptImageButton.setOnClickListener {
                clickListener.onAcceptClicked(data)
            }
            binding.declineImageButton.setOnClickListener {
                clickListener.onDeclineClicked(data)
            }

        }
    }

    interface GroupCallback{
        fun onItemClicked(data: ArticleData)
        fun onAcceptClicked(data: ArticleData)
        fun onDeclineClicked(data: ArticleData)
    }

    override fun getItemCount(): Int = adapterData.size
}