package da.farmer.app.ui.register.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import da.farmer.app.data.repositories.address.response.AddressData
import da.farmer.app.databinding.AdapterAddressBinding


class CountryListAdapter (
    val context: Context,
    val clickListener: AddressCallback,
    val displayPhoneCode: Boolean = false
) :
    RecyclerView.Adapter<CountryListAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<AddressData>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<AddressData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<AddressData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterAddressBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position], position)
    }

    inner class AdapterViewHolder(val binding: AdapterAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: AddressData, position: Int) = with(itemView) {
            binding.addressTitleTextView.text = data.name
            binding.phoneTextView.text = data.phone_code
            binding.phoneTextView.isVisible = displayPhoneCode
            binding.addressTitleTextView.setOnClickListener {
                clickListener.onItemClicked(data, position)
            }
        }
    }

    interface AddressCallback{
        fun onItemClicked(data: AddressData, position: Int)
    }

    override fun getItemCount(): Int = adapterData.size
}