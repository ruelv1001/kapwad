package da.farmer.app.ui.verify.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import da.farmer.app.data.repositories.profile.response.LOVData
import da.farmer.app.databinding.AdapterLovListBinding


class LOVListAdapter:  RecyclerView.Adapter<LOVListAdapter.AdapterViewHolder>() {


    inner class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<LOVData>(){
        override fun areItemsTheSame(oldItem: LOVData, newItem: LOVData): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: LOVData, newItem: LOVData): Boolean {
            return oldItem == newItem
        }
    }

    //for efficiency
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LOVListAdapter.AdapterViewHolder {
        return AdapterViewHolder(
            AdapterLovListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }


    override fun onBindViewHolder(holder: LOVListAdapter.AdapterViewHolder, position: Int) {
        AdapterLovListBinding.bind(holder.itemView).apply {
            val lovData = differ.currentList[position]

            nameTextView.text = lovData.name.toString()
            this.root.setOnClickListener {
                onItemClickListener?.let { it(lovData) }
            }
        }
    }

    fun clear(){
        differ.submitList(emptyList())
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((LOVData) -> Unit)? = null
    fun setOnClickListener(listner : (LOVData)-> Unit){
        onItemClickListener = listner
    }
}