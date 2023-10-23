package com.lionscare.app.ui.profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.profile.response.UserNotificationData
import com.lionscare.app.databinding.AdapterNotificationsBinding
import org.json.JSONException
import org.json.JSONObject

class NotificationsAdapter (val clickListener: NotificationsCallback) :
    PagingDataAdapter<UserNotificationData, NotificationsAdapter.NotificationsViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserNotificationData>() {
            override fun areItemsTheSame(oldItem: UserNotificationData, newItem: UserNotificationData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserNotificationData, newItem: UserNotificationData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterNotificationsBinding.inflate(inflater, parent, false)
        return NotificationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class NotificationsViewHolder(private val binding: AdapterNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: UserNotificationData?) {
            data?.let {

                val jsonObject: JSONObject
                val res = data.data?.replace("\\", "")
                try {
                    jsonObject = JSONObject(res)
                    val title = jsonObject.getString("title")
                    val type = jsonObject.getString("type")
                    val content = jsonObject.getString("content")
                    val reference_id = jsonObject.getString("reference_id")

                    if (type.contains("KYC")) {
                        binding.iconImageView.setImageResource(R.drawable.ic_notif_badge)
                    } else {
                        binding.iconImageView.setImageResource(R.drawable.ic_notif_group)
                    }
                    binding.titleTextView.text = title
                    binding.remarksTextView.text = content

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                binding.dateTextView.text = data.date_created?.date_only_ph

                binding.inboundOutboundLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }

            }
        }
    }

    interface NotificationsCallback {
        fun onItemClicked(data: UserNotificationData)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }
}