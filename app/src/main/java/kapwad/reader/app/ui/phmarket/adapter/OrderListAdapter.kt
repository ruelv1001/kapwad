package kapwad.reader.app.ui.phmarket.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import kapwad.reader.app.data.model.OrderListModelData

import kapwad.reader.app.databinding.AdapterOrderHistoryBinding


class OrderListAdapter (val context: Context, val clickListener: OrderCallback) :
    RecyclerView.Adapter<OrderListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<OrderListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<OrderListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<OrderListModelData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterOrderHistoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterOrderHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: OrderListModelData, position: Int) = with(itemView) {

            binding.dateTextView.text=data.date
            binding.nameTextView.text=data.name
            binding.amountTextView.text=data.amount
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface OrderCallback{
        fun onItemClicked(data: OrderListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}