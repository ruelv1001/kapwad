package kapwad.reader.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.repositories.wallet.response.QRData
import kapwad.reader.app.databinding.AdapterMembersBinding
import kapwad.reader.app.utils.loadAvatar

class SearchInviteMemberAdapter (val context: Context, val clickListener: SearchCallback) :
    RecyclerView.Adapter<SearchInviteMemberAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<QRData>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<QRData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<QRData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterMembersBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: QRData) = with(itemView) {
            binding.nameTextView.text = data.name
            binding.idNoTextView.text = data.phone_number
            binding.profileImageView.loadAvatar(data.avatar?.thumb_path, context)
            binding.membersLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }

        }
    }

    interface SearchCallback{
        fun onItemClicked(data: QRData)
    }

    override fun getItemCount(): Int = adapterData.size
}