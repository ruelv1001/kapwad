package com.ziacare.app.ui.verify.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ziacare.app.R
import com.ziacare.app.data.repositories.profile.response.LOVData
import com.ziacare.app.databinding.DialogLovListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LOVListDialog : BottomSheetDialogFragment() {

    private var _binding: DialogLovListBinding? = null
    private val binding get() = _binding!!
    private var callback: LOVListDialogCallBack? = null
    private var lovData : List<LOVData>? = null
    private var title : String = "List"

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: LOVListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLovListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setClickListener()
        binding.titleTextView.text = title
    }



    private fun setupRecyclerView() = binding.run {
        adapter = LOVListAdapter( )
        linearLayoutManager = LinearLayoutManager(requireContext())
        lovDataRecyclerView.layoutManager = linearLayoutManager
        lovDataRecyclerView.adapter = adapter

        if (lovData?.isEmpty() == true){ //if returned empty, make txt visible
            binding.txtNothingToShow.visibility = View.VISIBLE
            binding.lovDataRecyclerView.visibility = View.GONE
        }else{  //else make it gone, not invisible so it won't take up space
            binding.txtNothingToShow.visibility = View.GONE
            binding.lovDataRecyclerView.visibility = View.VISIBLE

            //set data
            adapter?.differ?.submitList(lovData) //initially submit a list of value for the adapter
        }
    }


    private fun setClickListener() {
        adapter?.setOnClickListener {
            callback?.onLovDataClicked(this,it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface LOVListDialogCallBack {
        fun onLovDataClicked(dialog: LOVListDialog, lovData: LOVData)
    }

    companion object {
        fun newInstance(callback: LOVListDialogCallBack? = null, lovData :List<LOVData>, title : String) = LOVListDialog()
            .apply {
                this.callback = callback
                this.lovData = lovData
                this.title = title
            }

        val TAG: String = LOVListDialog::class.java.simpleName
    }
}