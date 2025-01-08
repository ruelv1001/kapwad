package kapwad.reader.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.databinding.AdapterAllConsumerBinding


import kapwad.reader.app.databinding.AdapterConsumerListBinding


import kapwad.reader.app.databinding.AdapterDateBinding
import kapwad.reader.app.databinding.AdapterSyncBinding


class TempoListAdapter (val context: Context, val clickListener: ConsumerCallback) :
    RecyclerView.Adapter<TempoListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<TempListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<TempListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<TempListModelData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterAllConsumerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterAllConsumerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: TempListModelData, position: Int) = with(itemView) {

            binding.accountTextView.text=data.account_number
            binding.nameTextView.text=data.Prev
            binding.meterTextView.text=data.Meternumber
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface ConsumerCallback{
        fun onItemClicked(data: TempListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}