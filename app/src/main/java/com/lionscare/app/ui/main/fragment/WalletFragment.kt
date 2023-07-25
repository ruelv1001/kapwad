package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.FragmentWalletBinding
import com.lionscare.app.ui.wallet.activity.TopUpPointsActivity
import com.lionscare.app.ui.wallet.activity.TransactionsActivity
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import com.lionscare.app.utils.setOnSingleClickListener

class WalletFragment : Fragment(), InboundOutboundAdapter.InboundOutboundCallback {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : InboundOutboundAdapter? = null
    private var dataList: List<SampleData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWalletBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setClickListeners()
    }

    private fun setClickListeners() = binding.run {
        viewAllTextView.setOnSingleClickListener {
            val intent = TransactionsActivity.getIntent(requireContext())
            startActivity(intent)
        }
        topUpPointsLinearLayout.setOnSingleClickListener {
            val intent = TopUpPointsActivity.getIntent(requireContext())
            startActivity(intent)
        }
        sendPointsLinearLayout.setOnSingleClickListener {
            val intent = WalletActivity.getIntent(requireContext(),"Send Points")
            startActivity(intent)
        }
        scan2PayLinearLayout.setOnSingleClickListener {

        }
        postRequestLinearLayout.setOnSingleClickListener {
            val intent = WalletActivity.getIntent(requireContext(),"Post Request")
            startActivity(intent)
        }
    }

    private fun setUpAdapter() = binding.run {
        adapter = InboundOutboundAdapter(this@WalletFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: SampleData) {

    }

}