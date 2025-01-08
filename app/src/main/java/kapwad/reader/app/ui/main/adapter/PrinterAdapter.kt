package kapwad.reader.app.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.model.PrinterModel

import kapwad.reader.app.databinding.AdapterAllConsumerBinding


import kapwad.reader.app.databinding.AdapterConsumerListBinding


import kapwad.reader.app.databinding.AdapterDateBinding
import kapwad.reader.app.databinding.AdapterPrinterBinding
import kapwad.reader.app.databinding.AdapterSyncBinding


class PrinterAdapter (val context: Context, val clickListener: ConsumerCallback) :
    RecyclerView.Adapter<PrinterAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<PrinterModel>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<PrinterModel>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<PrinterModel> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterPrinterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterPrinterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: PrinterModel, position: Int) = with(itemView) {

            binding.nameTextView.text=data.title
            binding.macTextView.text=data.mac
            binding.statusTextView.text=data.status
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface ConsumerCallback{
        fun onItemClicked(data: PrinterModel, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}