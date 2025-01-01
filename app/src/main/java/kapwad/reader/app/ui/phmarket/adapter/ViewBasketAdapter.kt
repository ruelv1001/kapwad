package kapwad.reader.app.ui.phmarket.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import kapwad.reader.app.data.model.ProductOrderListModelData


import kapwad.reader.app.databinding.AdapterOrderSummaryBinding


class ViewBasketAdapter (val context: Context, val clickListener: ViewBasketCallback) :
    RecyclerView.Adapter<ViewBasketAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<ProductOrderListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<ProductOrderListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<ProductOrderListModelData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterOrderSummaryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterOrderSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: ProductOrderListModelData, position: Int) = with(itemView) {
            binding.noTextView.text = (position + 1).toString()
            binding.nameTextView.text=data.name
            binding.amountTextView.text=data.amount
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface ViewBasketCallback{
        fun onItemClicked(data: ProductOrderListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}