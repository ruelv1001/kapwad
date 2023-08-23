package com.lionscare.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.AdapterGroupPendingRequestBinding

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
        val binding = AdapterGroupPendingRequestBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterGroupPendingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: ArticleData) = with(itemView) {
            binding.titleTextView.text = data.name
            binding.membersTextView.text = data.description
            binding.referenceTextView.visibility = View.GONE
//            binding.articleImageView.loadImage(data.image?.thumb_path, context)

            /*if(type == "invite"){
              binding.acceptTextView.visibility = View.VISIBLE
              binding.declineTextView.visibility = View.VISIBLE
              binding.cancelTextView.visibility = View.GONE
          }else{
              binding.acceptTextView.visibility = View.GONE
              binding.declineTextView.visibility = View.GONE
              binding.cancelTextView.visibility = View.VISIBLE
          }*/

            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }
            binding.acceptTextView.setOnClickListener {
                clickListener.onAcceptClicked(data)
            }
            binding.declineTextView.setOnClickListener {
                clickListener.onDeclineClicked(data)
            }
            binding.cancelTextView.setOnClickListener {
                clickListener.onCancelClicked(data)
            }
        }
    }

    interface GroupCallback{
        fun onItemClicked(data: ArticleData)
        fun onAcceptClicked(data: ArticleData)
        fun onDeclineClicked(data: ArticleData)
        fun onCancelClicked(data: ArticleData)
    }

    override fun getItemCount(): Int = adapterData.size
}