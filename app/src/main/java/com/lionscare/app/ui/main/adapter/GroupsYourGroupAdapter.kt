package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.AdapterGroupYourGroupBinding

class GroupsYourGroupAdapter (val context: Context, val clickListener: GroupCallback) :
    PagingDataAdapter<ArticleData, GroupsYourGroupAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleData>() {
            override fun areItemsTheSame(oldItem: ArticleData, newItem: ArticleData): Boolean {
                return oldItem.article_id == newItem.article_id
            }

            override fun areContentsTheSame(oldItem: ArticleData, newItem: ArticleData): Boolean {
                return oldItem == newItem
            }
        }
    }

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
            binding.referenceTextView.text = data.reference

            /*if (data.type.equals("FAM")){
                binding.typeFamTextView.visibility = View.VISIBLE
                binding.typeOrgTextView.visibility = View.GONE
            } else {
                binding.typeFamTextView.visibility = View.GONE
                binding.typeOrgTextView.visibility = View.VISIBLE
            }*/

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

    fun filterData(query: String?) {
        val filteredList = adapterData.filter { data ->
            data.name?.contains(query ?: "", ignoreCase = true) == true ||
                    data.reference?.contains(query ?: "", ignoreCase = true) == true
        }
        appendData(filteredList)
        notifyDataSetChanged()
        // adapter.submitData(lifecycle, PagingData.from(filteredList))
    }
}