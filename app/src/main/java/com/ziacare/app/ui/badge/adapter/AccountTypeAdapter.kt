package com.ziacare.app.ui.badge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ziacare.app.R
import com.ziacare.app.data.model.AccountTypeModel
import com.ziacare.app.databinding.AdapterAccountTypeBinding
import com.ziacare.app.utils.CommonLogger
import com.ziacare.app.utils.setOnSingleClickListener

class AccountTypeAdapter(
    val context: Context,
    val clickListener: OnClickCallback,
) :
    RecyclerView.Adapter<AccountTypeAdapter.AdapterViewHolder>()  {

    private val adapterData = mutableListOf<AccountTypeModel>()
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private var isBadgeRequestedBefore : Boolean = false

    fun clear() {
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun changeIsBadgeRequestedBefore(value: Boolean){
        isBadgeRequestedBefore = value
    }

    fun appendData(newData: List<AccountTypeModel>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun setSelectedItem(data: AccountTypeModel) {
        val selectedList = mutableListOf<AccountTypeModel>()
        for (item in adapterData) {
            item.isSelected = item == data
            selectedList.add(item)
        }
        clear()
        appendData(selectedList)
    }

    fun getSelectedData(): AccountTypeModel {
        val selectedItem = adapterData.single {
            it.isSelected == true
        }
        return selectedItem
    }


    fun getData(): MutableList<AccountTypeModel> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterAccountTypeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterAccountTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: AccountTypeModel, position: Int) = with(itemView) {
            binding.accountTypeTextView.text = data.title
            data.icon?.let { binding.accountTypeImageView.setImageResource(it) }
            binding.accountTypeRadioButton.isChecked = data.isSelected == true
            binding.accountTypeLinearLayout.isSelected = data.isSelected == true
            binding.accountTypeRadioButton.isChecked = selectedPosition == position

            //both the container and the  button
            //is both clickable to achieve the same thing
            binding.accountTypeRadioButton.setOnSingleClickListener {
                setSelectedPosition(position)
                clickListener.onItemClicked(data, position)
            }
            binding.accountTypeLinearLayout.setOnSingleClickListener {
                setSelectedPosition(position)
                clickListener.onItemClicked(data, position)
            }

            if (isBadgeRequestedBefore){
                binding.accountTypeLinearLayout.isClickable = false
                binding.accountTypeRadioButton.isClickable = false
                binding.accountTypeTextView.setTextColor(context.getColor(R.color.super_light_gray))
            }else{
                binding.accountTypeLinearLayout.isClickable = true
                binding.accountTypeRadioButton.isClickable = true
                binding.accountTypeTextView.setTextColor(context.getColor(R.color.black_text))
            }


        }
    }

    interface OnClickCallback {
        fun onItemClicked(data: AccountTypeModel, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size

    fun setSelectedPosition(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }
        notifyItemChanged(selectedPosition)
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}