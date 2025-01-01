package kapwad.reader.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.group.response.GroupListData
import kapwad.reader.app.databinding.AdapterGroupYourGroupBinding
import kapwad.reader.app.utils.loadGroupAvatar

class GroupsYourGroupAdapter(
    val context: Context,
    val clickListener: GroupCallback,
    val shouldShowDonationRequestsViews: Boolean? = null,
) :
    PagingDataAdapter<GroupListData, GroupsYourGroupAdapter.AdapterViewHolder>(
        DIFF_CALLBACK
    ) {

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

                binding.checkBox.isVisible = shouldShowDonationRequestsViews != null

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

    fun hasData(): Boolean {
        return itemCount != 0
    }

    interface GroupCallback {
        fun onItemClicked(data: GroupListData)
    }
}