package kapwad.reader.app.ui.main.fragment

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
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.wallet.response.TransactionData
import kapwad.reader.app.databinding.FragmentDaWalletBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.wallet.activity.TopUpPointsActivity
import kapwad.reader.app.ui.wallet.activity.TransactionsActivity
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.ui.wallet.dialog.Scan2PayDialog
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewState
import kapwad.reader.app.utils.dialog.ScannerDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class SyncListFragment : Fragment(), InboundOutboundAdapter.InboundOutboundCallback, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentDaWalletBinding? = null
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
        _binding = FragmentDaWalletBinding.inflate(
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
            if(viewModel.getKycStatus()  != "completed"){
                //do not allow users kyc is not completed
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified), 5000)
            }else{
                val intent = WalletActivity.getIntent(requireContext(), "Send Points")
                startActivity(intent)
            }
        }
        postRequestLinearLayout.setOnSingleClickListener {
            val intent = WalletActivity.getIntent(requireContext(), "Post Request")
            startActivity(intent)
        }
        scan2PayLinearLayout.setOnSingleClickListener {
            if(viewModel.getKycStatus()  != "completed"){
                //do not allow users kyc is not completed
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified), 5000)
            }else{
                scanToPay()
            }
        }
    }

    private fun setUpAdapter() = binding.run {
        swipeRefreshLayout.setOnRefreshListener(this@SyncListFragment)
        adapter = InboundOutboundAdapter(this@SyncListFragment)
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
                requireActivity().toastSuccess(viewState.message.toString(), CpmToast.SHORT_DURATION)
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
                        requireActivity().toastError(getString(R.string.invalid_qr_code_msg), CpmToast.SHORT_DURATION)
                    }
                } catch (e: JSONException) {
                    requireActivity().toastError(getString(R.string.invalid_qr_code_msg), CpmToast.SHORT_DURATION)
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
        fun newInstance() = SyncListFragment()
        private const val PARTICIPANT = "participant"
    }

}