package com.lionscare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.databinding.AdapterGroupYourGroupBinding

class GroupsYourGroupAdapter (val context: Context, val clickListener: GroupCallback) :
    PagingDataAdapter<GroupListData, GroupsYourGroupAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GroupListData>() {
            override fun areItemsTheSame(oldItem: GroupListData, newItem: GroupListData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GroupListData, newItem: GroupListData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGroupYourGroupBinding.inflate(inflater, parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class AdapterViewHolder(val binding: AdapterGroupYourGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GroupListData?){
            data?.let {
                binding.titleTextView.text = data.name
                  binding.membersTextView.text = context.resources.getQuantityString(
                      R.plurals.member_plural, //plural from strings.xml file
                      data.member_count?: 0, //quantity
                      data.member_count?: 0 //var arg
                  )
                  binding.referenceTextView.text = data.qrcode

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
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }

    interface GroupCallback{
        fun onItemClicked(data: GroupListData)
    }

    fun filterData(query: String?) {
        /*val filteredList = adapterData.filter { data ->
            data.group_name?.contains(query ?: "", ignoreCase = true) == true ||
                    data.group_privacy?.contains(query ?: "", ignoreCase = true) == true
        }*/
       // appendData(filteredList)
        notifyDataSetChanged()
        //adapter.submitData(lifecycle, PagingData.from(filteredList))
    }
}