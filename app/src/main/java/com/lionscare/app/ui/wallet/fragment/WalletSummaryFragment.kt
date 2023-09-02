package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.databinding.FragmentWalletSummaryBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.currencyFormat
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletSummaryFragment : Fragment() {

    private var _binding: FragmentWalletSummaryBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }
    private val viewModel: WalletViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletSummaryBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDetails()
        setupClickListener()
        observeWallet()
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_summary_title)
                if (activity.isGroupId){
                    recipientGroupLayout.adapterLinearLayout.isVisible = true
                    recipientLayout.membersLinearLayout.isGone = true
                    recipientGroupLayout.titleTextView.text = activity.groupData.name
                    //TODO to be updated when display id ready
                    recipientGroupLayout.referenceTextView.text = "LC-000123"
                }else{
                    recipientGroupLayout.adapterLinearLayout.isGone = true
                    recipientLayout.membersLinearLayout.isVisible = true
                    recipientLayout.nameTextView.text = activity.qrData.name
                    //TODO to be updated when display id ready
                    recipientLayout.idNoTextView.text = "LC-000123"
                }

            }
        }

        amountTextView.text = currencyFormat(activity.amount)
        messageTextView.text = activity.message

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
            is WalletViewState.SuccessSendPoint -> {
                activity.hideLoadingDialog()
                activity.transactionData = viewState.data?: TransactionData()
                findNavController().navigate(WalletSummaryFragmentDirections.actionNavigationWalletSummaryToNavigationWalletDetails())
            }
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if (activity.isGroupId){
                viewModel.doSendPoints(
                    groupId = activity.groupData.id.orEmpty(),
                    amount = activity.amount,
                    notes = activity.message
                )
            }else{
                viewModel.doSendPoints(
                    userId = activity.qrData.id.orEmpty(),
                    amount = activity.amount,
                    notes = activity.message
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}