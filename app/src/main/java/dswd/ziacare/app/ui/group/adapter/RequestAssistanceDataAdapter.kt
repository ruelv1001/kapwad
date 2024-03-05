package dswd.ziacare.app.ui.group.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import dswd.ziacare.app.data.repositories.generalsetting.response.RequestAssistanceData

class RequestAssistanceDataAdapter(context: Context, data: List<RequestAssistanceData>) :
    ArrayAdapter<RequestAssistanceData>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)
        val item = getItem(position)
        view.findViewById<TextView>(android.R.id.text1)?.text = item?.code
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val item = getItem(position)
        view.findViewById<TextView>(android.R.id.text1)?.text = item?.code
        return view
    }
}
