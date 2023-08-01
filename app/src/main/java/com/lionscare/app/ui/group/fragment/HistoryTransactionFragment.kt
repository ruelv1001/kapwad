package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.FragmentHistoryTransactionBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryTransactionFragment: Fragment(), InboundOutboundAdapter.InboundOutboundCallback {
    private var _binding: FragmentHistoryTransactionBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private var dataList: List<SampleData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryTransactionBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setView()
        onResume()
        setUpAdapter()
    }

    private fun setUpAdapter() = binding.run {
        adapter = InboundOutboundAdapter(this@HistoryTransactionFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = 1,
                title = "Inbound",
                amount = "100.00",
                remarks = getString(R.string.inbound_outbound_hint)
            ),
            SampleData(
                id = 2,
                title = "Outbound",
                amount = "100.00",
                remarks = getString(R.string.inbound_outbound_hint)
            )
        )
        adapter?.submitData(lifecycle, PagingData.from(dataList))
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_transactions))
    }

    private fun setView() = binding.run{
//        firstNameEditText.doOnTextChanged {
//                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
//        }
    }

    private fun setClickListeners() = binding.run {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: SampleData) {
//        TODO("Not yet implemented")
    }
}