package kapwad.reader.app.ui.phmarket.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.model.ProductListModelData


import kapwad.reader.app.databinding.AdapterMarketPlaceBinding

import kapwad.reader.app.utils.loadAvatar


class PreOrderListAdapter (val context: Context, val clickListener: PreOrderCallback) :
    RecyclerView.Adapter<PreOrderListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<ProductListModelData>()
    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<ProductListModelData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun getData(): MutableList<ProductListModelData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterMarketPlaceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterMarketPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun displayData(data: ProductListModelData, position: Int) = with(itemView) {

            binding.productImageVIew.loadAvatar( data.avatar.toString(),itemView.context)

            binding.nameTextView.text=data.name
            binding.priceTextView.text=data.amount
            binding.adapterLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface PreOrderCallback{
        fun onItemClicked(data: ProductListModelData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}