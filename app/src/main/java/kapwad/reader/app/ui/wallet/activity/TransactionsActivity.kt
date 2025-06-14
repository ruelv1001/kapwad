package kapwad.reader.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kapwad.reader.app.data.repositories.wallet.response.TransactionData
import kapwad.reader.app.databinding.ActivityTransactionsBinding
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewState
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity(),
    InboundOutboundAdapter.InboundOutboundCallback, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityTransactionsBinding

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : InboundOutboundAdapter? = null
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

        adapter?.addLoadStateListener {
            if(adapter?.hasData() == true){
                placeHolderTextView.isVisible = false
                recyclerView.isVisible = true
            }else{
                placeHolderTextView.isVisible = true
                recyclerView.isVisible = false
            }
        }
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
    override fun onItemClicked(data: TransactionData) {

    }

    override fun onRefresh() {
        clearList()
        viewModel.loadTransactionList()
    }

    private fun clearList(){
        adapter?.submitData(lifecycle, PagingData.empty())
    }
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TransactionsActivity::class.java)
        }
    }
}