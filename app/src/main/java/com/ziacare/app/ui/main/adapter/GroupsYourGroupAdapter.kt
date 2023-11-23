package com.ziacare.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.R
import com.ziacare.app.data.repositories.group.response.GroupListData
import com.ziacare.app.data.repositories.member.response.MemberListData
import com.ziacare.app.databinding.AdapterGroupYourGroupBinding
import com.ziacare.app.ui.billing.viewstate.CustomGroupListDataModel
import com.ziacare.app.utils.CommonLogger
import com.ziacare.app.utils.loadGroupAvatar

class GroupsYourGroupAdapter(
    val context: Context,
    val clickListener: GroupCallback,
    val shouldShowDonationRequestsViews: Boolean? = null,
) :
    PagingDataAdapter<GroupListData, GroupsYourGroupAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

    var customGroupListDataModel: MutableList<CustomGroupListDataModel> = mutableListOf()
        private set

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GroupListData>() {
            override fun areItemsTheSame(oldItem: GroupListData, newItem: GroupListData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: GroupListData,
                newItem: GroupListData
            ): Boolean {
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
        holder.bind(data, position)
    }

    inner class AdapterViewHolder(val binding: AdapterGroupYourGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GroupListData?, position: Int) {
            data?.let {
                //set to ischecked true to make sure cached data will match contains
                val customGroupDataModel = CustomGroupListDataModel(groupData = data, isChecked = true)
                var existingModel = customGroupListDataModel.find { it == customGroupDataModel }

                binding.checkBox.isVisible = shouldShowDonationRequestsViews != null
                binding.checkBox.isChecked = existingModel?.isChecked ?: false

                binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    existingModel = customGroupListDataModel.find { it == customGroupDataModel }
                    if (existingModel != null) {
                        existingModel?.isChecked = isChecked
                        val index = customGroupListDataModel.indexOf(existingModel)
                        customGroupListDataModel[index] = existingModel!!
                        CommonLogger.instance.sysLogE("fefe",  customGroupListDataModel[index].isChecked)
                    } else {
                        // If not in the list, add it with the current isChecked value
                        customGroupListDataModel.add(CustomGroupListDataModel(groupData = data, isChecked = isChecked))
                    }
                }


                binding.titleTextView.text = data.name
                binding.membersTextView.text = context.resources.getQuantityString(
                    R.plurals.member_plural, //plural from strings.xml file
                    data.member_count ?: 0, //quantity
                    data.member_count ?: 0 //var arg
                )
                binding.referenceTextView.text = data.qrcode
                binding.imageView.loadGroupAvatar(data.avatar?.thumb_path)

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



    fun getCustomData(): List<CustomGroupListDataModel>{
        return customGroupListDataModel
    }

    //get cahced result from viewmodel
    fun setCustomData(data: MutableList<CustomGroupListDataModel>) {
        customGroupListDataModel = data
    }

    fun hasData(): Boolean {
        return itemCount != 0
    }

    interface GroupCallback {
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