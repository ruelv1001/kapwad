package com.ziacare.app.ui.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.data.model.AddFundsModel
import com.ziacare.app.databinding.AdapterTopUpButtonsBinding


class AddFundsAdapter (val context: Context, val clickListener: AddFundCallback) :
    RecyclerView.Adapter<AddFundsAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<AddFundsModel>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<AddFundsModel>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun setSelectedItem(data: AddFundsModel){
        val selectedList = mutableListOf<AddFundsModel>()
        for(item in adapterData){
            item.isSelected = item == data
            selectedList.add(item)
        }
        clear()
        appendData(selectedList)
    }

    fun getSelectedData(): String{
        val selectedItem = adapterData.single {
            it.isSelected == true
        }
        return selectedItem.title.orEmpty()
    }


    fun getData(): MutableList<AddFundsModel> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterTopUpButtonsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterTopUpButtonsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: AddFundsModel, position: Int) = with(itemView) {
            binding.myButton.text = data.title
            binding.myButton.isSelected = data.isSelected == true
            binding.myButton.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface AddFundCallback{
        fun onItemClicked(data: AddFundsModel, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}