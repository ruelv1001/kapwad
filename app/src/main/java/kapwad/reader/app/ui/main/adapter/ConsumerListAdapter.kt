package kapwad.reader.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.model.CreatedBillListModelData


import kapwad.reader.app.databinding.AdapterConsumerListBinding


import kapwad.reader.app.databinding.AdapterDateBinding
import kapwad.reader.app.databinding.AdapterSyncBinding


class ConsumerListAdapter (val context: Context, val clickListener: ConsumerCallback) :
    RecyclerView.Adapter<ConsumerListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<CreatedBillListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<CreatedBillListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<CreatedBillListModelData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterConsumerListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterConsumerListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: CreatedBillListModelData, position: Int) = with(itemView) {

            binding.accountTextView.text=data.accountnumber
            binding.billingMonthTextView.text=data.month
            binding.meterTextView.text=data.meternumber
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface ConsumerCallback{
        fun onItemClicked(data: CreatedBillListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}