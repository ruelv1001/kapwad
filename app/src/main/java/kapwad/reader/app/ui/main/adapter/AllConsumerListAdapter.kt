package kapwad.reader.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.databinding.AdapterAllConsumerBinding


import kapwad.reader.app.databinding.AdapterConsumerListBinding


import kapwad.reader.app.databinding.AdapterDateBinding
import kapwad.reader.app.databinding.AdapterSyncBinding


class AllConsumerListAdapter (val context: Context, val clickListener: ConsumerCallback) :
    RecyclerView.Adapter<AllConsumerListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<ConsumerListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<ConsumerListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<ConsumerListModelData> = adapterData

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
        fun displayData(data: ConsumerListModelData, position: Int) = with(itemView) {

            binding.accountTextView.text=data.accountnumber
            binding.nameTextView.text=data.firstname +" "+ data.middlename+" "+ data.lastname
            binding.meterTextView.text=data.meternumber
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface ConsumerCallback{
        fun onItemClicked(data: ConsumerListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}