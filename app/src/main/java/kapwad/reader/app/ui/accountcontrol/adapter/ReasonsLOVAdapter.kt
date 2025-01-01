package kapwad.reader.app.ui.accountcontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kapwad.reader.app.data.repositories.auth.response.ReasonsData
import kapwad.reader.app.databinding.AdapterLovReasonBinding


class ReasonsLOVAdapter(val clickListener: ReasonCallback):
    RecyclerView.Adapter<ReasonsLOVAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<ReasonsData>()
    var selectedPosition = -1

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<ReasonsData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }

    fun setSelectedItem(data: ReasonsData){
        val selectedList = mutableListOf<ReasonsData>()
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
        return selectedItem.id.toString()
    }


    fun getData(): MutableList<ReasonsData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterLovReasonBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterLovReasonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: ReasonsData, position: Int) = with(itemView) {
            binding.reasonRadioButton.text = data.reason
            binding.reasonRadioButton.isChecked = data.isSelected == true

            binding.reasonRadioButton.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface ReasonCallback{
        fun onItemClicked(data: ReasonsData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}