package kapwad.reader.app.ui.crops.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.repositories.crops.response.CropsData

import kapwad.reader.app.databinding.AdapterCropListBinding

class CropListAdapter(val clickListener: CropsCallback) :
    PagingDataAdapter<CropsData, CropListAdapter.CropsViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CropsData>() {
            override fun areItemsTheSame(oldItem: CropsData, newItem: CropsData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CropsData, newItem: CropsData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterCropListBinding.inflate(inflater, parent, false)
        return CropsViewHolder(binding)
    }

    fun hasData() : Boolean{
        return itemCount != 0
    }

    override fun onBindViewHolder(holder: CropsViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    inner class CropsViewHolder(private val binding: AdapterCropListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: CropsData?) {
            data?.let {


                binding.dateTextView.text=data.start_date?.date_only
                binding.statusTextView.text=data.status

                binding.adapterLinearLayout.setOnClickListener {
                    clickListener.onItemClicked(data)
                }
            }
        }
    }

    interface CropsCallback {
        fun onItemClicked(data: CropsData)
    }
}