package kapwad.reader.app.ui.crops.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import kapwad.reader.app.data.repositories.crops.response.CropsData
import kapwad.reader.app.databinding.AdapterCropListBinding


class CropsListAdapter (val context: Context, val clickListener: CropsListCallback) :
    RecyclerView.Adapter<CropsListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<CropsData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<CropsData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<CropsData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterCropListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterCropListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: CropsData, position: Int) = with(itemView) {

            binding.dateTextView.text=data.start_date?.date_only
            binding.statusTextView.text=data.status
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface CropsListCallback{
        fun onItemClicked(data: CropsData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}