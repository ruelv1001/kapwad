package kapwad.reader.app.ui.crops.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import kapwad.reader.app.data.repositories.crops.response.CropItemData

import kapwad.reader.app.databinding.AdapterDateBinding


class DateListAdapter (val context: Context, val clickListener: DateCallback) :
    RecyclerView.Adapter<DateListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<CropItemData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<CropItemData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<CropItemData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterDateBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: CropItemData, position: Int) = with(itemView) {

            binding.dateTextView.text=data.created_at?.date_db
            binding.labelTextView.text=data.status
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface DateCallback{
        fun onItemClicked(data: CropItemData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}