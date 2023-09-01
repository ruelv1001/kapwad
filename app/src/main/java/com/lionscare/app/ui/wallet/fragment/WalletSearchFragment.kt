package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.databinding.FragmentWalletSearchBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.ui.wallet.adapter.MembersAdapter
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.dialog.ScannerDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletSearchFragment : Fragment(), MembersAdapter.MembersCallback {

    private var _binding: FragmentWalletSearchBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : MembersAdapter? = null
    private var dataList: List<SampleData> = emptyList()
    private val activity by lazy { requireActivity() as WalletActivity }
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupDetails()
        setupClickListener()
        observeWallet()
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
            is WalletViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.SuccessScanQR -> {
                activity.hideLoadingDialog()
                activity.qrData = viewState.scanQRData?: QRData()
                findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
            }
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_to_title)
            }
            "Post Request" -> {
                titleTextView.text = getString(R.string.wallet_request_points_from_title)
            }
        }

    }

    private fun setupAdapter() = binding.run {
        adapter = MembersAdapter(this@WalletSearchFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = R.drawable.img_profile,
                title = "Raquel Castro",
                amount = "LC-000004",
            ),
            SampleData(
                id = R.drawable.img_profile,
                title = "Romeo Dela Cruz",
                amount = "LC-000001",
            )
        )
        adapter?.submitData(lifecycle, PagingData.from(dataList))

    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        qrImageView.setOnSingleClickListener {
            ScannerDialog.newInstance( object : ScannerDialog.ScannerListener{
                override fun onScannerSuccess(qrValue: String) {
                    //TODO to be updated when QR value is already in QR
                    viewModel.doScanQr(qrValue)
                }
            }, "Scan QR")
                .show(childFragmentManager, ScannerDialog.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: SampleData) {
        activity.data = data
        findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
    }

}