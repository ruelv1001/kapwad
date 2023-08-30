package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.databinding.ActivityTransactionsBinding
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity(),
    InboundOutboundAdapter.InboundOutboundCallback, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityTransactionsBinding

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : InboundOutboundAdapter? = null
    private var searchView: SearchView? = null
    private val viewModel: WalletViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        val view = binding.root
        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        setContentView(view)
        setUpAdapter()
        setupClickListener()
        observeWallet()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setUpAdapter() = binding.run {
        swipeRefreshLayout.setOnRefreshListener(this@TransactionsActivity)
        adapter = InboundOutboundAdapter(this@TransactionsActivity)
        linearLayoutManager = LinearLayoutManager(this@TransactionsActivity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

    }

    private fun observeWallet() {
        lifecycleScope.launch{
            viewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: WalletViewState) {
        when (viewState) {
            is WalletViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is WalletViewState.SuccessTransactionList -> showTransactionList(viewState.pagingData)
            is WalletViewState.PopupError -> {
                showPopupError(this, supportFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun showTransactionList(transactions: PagingData<TransactionData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(lifecycle, transactions)
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
//        val filteredList = dataList.filter { data ->
//            data.title?.contains(query ?: "", ignoreCase = true) == true ||
//                    data.remarks?.contains(query ?: "", ignoreCase = true) == true
//        }
//        adapter?.submitData(lifecycle, PagingData.from(filteredList))
    }

    override fun onItemClicked(data: TransactionData) {

    }

    override fun onRefresh() {
        viewModel.loadTransactionList()
    }
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TransactionsActivity::class.java)
        }
    }
}