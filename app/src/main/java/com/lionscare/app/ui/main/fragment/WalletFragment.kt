package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.ui.wallet.activity.TopUpPointsActivity
import com.lionscare.app.ui.wallet.activity.TransactionsActivity
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.ui.wallet.adapter.InboundOutboundAdapter
import com.lionscare.app.ui.wallet.dialog.Scan2PayDialog
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
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class WalletFragment : Fragment(), InboundOutboundAdapter.InboundOutboundCallback, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
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
        postRequestLinearLayout.setOnSingleClickListener {
            val intent = WalletActivity.getIntent(requireContext(), "Post Request")
            startActivity(intent)
        }
        scan2PayLinearLayout.setOnSingleClickListener {
            scanToPay()
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
            is WalletViewState.LoadingScan -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.SuccessTransactionList -> showTransactionList(viewState.pagingData)
            is WalletViewState.SuccessGetBalance -> binding.pointsTextView.text = viewState.balanceData?.value
            is WalletViewState.SuccessScan2Pay -> {
                activity.hideLoadingDialog()
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_SHORT).show()
                onRefresh()
            }
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
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

    private fun scanToPay() {
        val scanDialog = ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
            override fun onScannerSuccess(qrValue: String) {
                val jsonObject: JSONObject
                val res = qrValue.replace("\\", "")
                try {
                    jsonObject = JSONObject(res)
                    val type = jsonObject.getString("type")
                    val mid = jsonObject.getString("value")
                    val businessName = jsonObject.getString("name")
                    val amount = if (jsonObject.has("amount")) {
                        jsonObject.getString("amount")
                    } else {
                        ""
                    }
                    val remarks = if (jsonObject.has("remarks")) {
                        jsonObject.getString("remarks")
                    } else {
                        ""
                    }
                    if (type == PARTICIPANT) {
                        Scan2PayDialog.newInstance(
                            businessName,
                            amount,
                            remarks,
                            object : Scan2PayDialog.Scan2PayListener {
                                override fun onProceed(amount: String, remarks: String) {
                                    viewModel.doScan2Pay(amount, mid, remarks)
                                }
                            }).show(childFragmentManager, ScannerDialog.TAG)
                    } else {
                        Toast.makeText(requireActivity(),
                            getString(R.string.invalid_qr_code_msg),
                            Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(requireActivity(),
                        getString(R.string.invalid_qr_code_msg),
                        Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

        })
        scanDialog.show(childFragmentManager, ScannerDialog.TAG)
    }

    override fun onRefresh() {
        clearList()
        viewModel.loadTransactionList(5)
        viewModel.getWalletBalance()
    }

    private fun clearList(){
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

    companion object{
        private const val PARTICIPANT = "participant"
    }

}