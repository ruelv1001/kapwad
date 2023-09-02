package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.databinding.FragmentWalletBinding
import com.lionscare.app.ui.wallet.activity.TopUpPointsActivity
import com.lionscare.app.ui.wallet.activity.TransactionsActivity
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import com.lionscare.app.ui.wallet.fragment.WalletSearchFragmentDirections
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.currencyFormat
import com.lionscare.app.utils.dialog.ScannerDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletFragment : Fragment(), InboundOutboundAdapter.InboundOutboundCallback,
    ScannerDialog.ScannerListener, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val activity by lazy { requireActivity() as WalletActivity }
    private val viewModel: WalletViewModel by viewModels()


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
        setDetails()
        setClickListeners()
        observeWallet()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    private fun setDetails() = binding.run {
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
            val intent = WalletActivity.getIntent(requireContext(), "Send Points")
            startActivity(intent)
        }
        scan2PayLinearLayout.setOnSingleClickListener {
            ScannerDialog.newInstance(this@WalletFragment, "Scan 2 Pay")
                .show(childFragmentManager, ScannerDialog.TAG)
        }
        postRequestLinearLayout.setOnSingleClickListener {
            val intent = WalletActivity.getIntent(requireContext(), "Post Request")
            startActivity(intent)
        }
    }

    private fun setUpAdapter() = binding.run {
        swipeRefreshLayout.setOnRefreshListener(this@WalletFragment)
        adapter = InboundOutboundAdapter(this@WalletFragment)
        linearLayoutManager = LinearLayoutManager(context)
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
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: WalletViewState) {
        when (viewState) {
            is WalletViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is WalletViewState.SuccessTransactionList -> showTransactionList(viewState.pagingData)
            is WalletViewState.SuccessGetBalance -> binding.pointsTextView.text = viewState.balanceData?.value
            is WalletViewState.PopupError -> {
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun showTransactionList(transactions: PagingData<TransactionData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, transactions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    override fun onItemClicked(data: TransactionData) {

    }

    override fun onScannerSuccess(qrValue: String) {
        activity.data = SampleData(
            id = R.drawable.img_profile,
            title = "Romeo Dela Cruz",
            amount = "LC-000001",
        )
        findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
    }

    override fun onRefresh() {
        clearList()
        viewModel.loadTransactionList(5)
        viewModel.getWalletBalance()
    }

    private fun clearList(){
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

}