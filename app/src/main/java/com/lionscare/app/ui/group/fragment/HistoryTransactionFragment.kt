package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.databinding.FragmentHistoryTransactionBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.viewmodel.GroupWalletViewModel
import com.lionscare.app.ui.group.viewmodel.GroupWalletViewState
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryTransactionFragment: Fragment(), InboundOutboundAdapter.InboundOutboundCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentHistoryTransactionBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val viewModel: GroupWalletViewModel by viewModels()


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
        observeWallet()
    }

    private fun setUpAdapter() = binding.run {
        swipeRefreshLayout.setOnRefreshListener(this@HistoryTransactionFragment)
        adapter = InboundOutboundAdapter(this@HistoryTransactionFragment)
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

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_transactions))
        onRefresh()
    }

    private fun observeWallet() {
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupWalletViewState) {
        when (viewState) {
            is GroupWalletViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is GroupWalletViewState.SuccessTransactionList -> showTransactionList(viewState.pagingData)
            is GroupWalletViewState.PopupError -> {
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun showTransactionList(transactions: PagingData<TransactionData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, transactions)
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

    override fun onItemClicked(data: TransactionData) {
    }

    override fun onRefresh() {
        clearList()
        viewModel.loadTransactionList(groupId = activity.groupDetails?.id.orEmpty())
    }

    private fun clearList(){
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }
}