package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.ActivityTransactionsBinding
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import com.lionscare.app.utils.setOnSingleClickListener

class TransactionsActivity : AppCompatActivity(), InboundOutboundAdapter.InboundOutboundCallback {

    private lateinit var binding: ActivityTransactionsBinding

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : InboundOutboundAdapter? = null
    private var searchView: SearchView? = null
    private var dataList: List<SampleData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        val view = binding.root
        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        setContentView(view)
        setUpAdapter()
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setUpAdapter() = binding.run {
        adapter = InboundOutboundAdapter(this@TransactionsActivity)
        linearLayoutManager = LinearLayoutManager(this@TransactionsActivity)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu?.findItem(R.id.search)
        searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun filterData(query: String?) {
        val filteredList = dataList.filter { data ->
            data.title?.contains(query ?: "", ignoreCase = true) == true ||
                    data.remarks?.contains(query ?: "", ignoreCase = true) == true
        }
        adapter?.submitData(lifecycle, PagingData.from(filteredList))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TransactionsActivity::class.java)
        }
    }

    override fun onItemClicked(data: SampleData) {

    }
}