package kapwad.reader.app.ui.wallet.fragment

import android.annotation.SuppressLint
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
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.wallet.response.TransactionData
import kapwad.reader.app.databinding.FragmentWalletSummaryBinding
import kapwad.reader.app.ui.group.viewmodel.GroupWalletViewModel
import kapwad.reader.app.ui.group.viewmodel.GroupWalletViewState
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewState
import kapwad.reader.app.utils.PopupErrorState
import kapwad.reader.app.utils.currencyFormat
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.loadGroupAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalletSummaryFragment : Fragment() {

    private var _binding: FragmentWalletSummaryBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }
    private val viewModel: WalletViewModel by viewModels()
    private val groupViewModel: GroupWalletViewModel by viewModels()


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
        observeGroupWallet()
    }

    @SuppressLint("SetTextI18n")
    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_summary_title)
                if (activity.isGroupId){
                    recipientGroupLayout.adapterLinearLayout.isVisible = true
                    recipientLayout.membersLinearLayout.isGone = true
                    recipientGroupLayout.titleTextView.text = activity.groupData.name
                    recipientGroupLayout.referenceTextView.text = activity.groupData.code
                    recipientGroupLayout.imageView.loadGroupAvatar(activity.groupData.avatar?.thumb_path)
                    recipientGroupLayout.membersTextView.text = "${activity.groupData.member_count.toString()} members"
                }else{
                    recipientGroupLayout.adapterLinearLayout.isGone = true
                    recipientLayout.membersLinearLayout.isVisible = true
                    recipientLayout.nameTextView.text = activity.qrData.name
                    recipientLayout.profileImageView.loadAvatar(activity.qrData.avatar?.thumb_path, requireActivity())
                    //TODO to be updated when display id ready
                    recipientLayout.idNoTextView.isGone = true
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
            is WalletViewState.InputError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager,PopupErrorState.HttpError, viewState.errorData?.amount?.get(0).orEmpty())
            }
            else -> Unit
        }
    }

    private fun observeGroupWallet() {
        viewLifecycleOwner.lifecycleScope.launch{
            groupViewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupWalletViewState) {
        when (viewState) {
            is GroupWalletViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is GroupWalletViewState.SuccessSendPoint -> {
                activity.hideLoadingDialog()
                activity.transactionData = viewState.data?: TransactionData()
                findNavController().navigate(WalletSummaryFragmentDirections.actionNavigationWalletSummaryToNavigationWalletDetails())
            }
            is GroupWalletViewState.PopupError -> {
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
            if (activity.isFromGroupWallet){
                callGroupWalletSendPoints()
            }else{
                callUserWalletSendPoints()
            }
        }
    }

    private fun callUserWalletSendPoints() {
        if (activity.isGroupId) {
            viewModel.doSendPoints(
                groupId = activity.groupData.id.orEmpty(),
                amount = activity.amount,
                notes = activity.message
            )
        } else {
            viewModel.doSendPoints(
                userId = activity.qrData.id.orEmpty(),
                amount = activity.amount,
                notes = activity.message
            )
        }
    }

    private fun callGroupWalletSendPoints() {
        if (activity.isGroupId) {
            groupViewModel.doSendPoints(
                groupId = activity.groupSenderId,
                amount = activity.amount,
                receiverGroupId = activity.groupData.id.orEmpty()
            )
        } else {
            groupViewModel.doSendPoints(
                groupId = activity.groupSenderId,
                amount = activity.amount,
                receiverUserId = activity.qrData.id.orEmpty(),
                assistanceId = activity.assistanceId
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}