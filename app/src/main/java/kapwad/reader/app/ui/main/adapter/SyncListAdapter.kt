package kapwad.reader.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.model.SyncListModelData




import kapwad.reader.app.databinding.AdapterDateBinding
import kapwad.reader.app.databinding.AdapterSyncBinding


class SyncListAdapter (val context: Context, val clickListener: DateCallback) :
    RecyclerView.Adapter<SyncListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<SyncListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<SyncListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<SyncListModelData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterSyncBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterSyncBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: SyncListModelData, position: Int) = with(itemView) {

            binding.dateTextView.text=data.biller_month
            binding.updateStatusTextView.text=data.status
            binding.uploadStatusTextView.text=data.uploaded
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface DateCallback{
        fun onItemClicked(data: SyncListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}