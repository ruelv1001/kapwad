package kapwad.reader.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentPaymentMethodBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.wallet.activity.TopUpPointsActivity
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewState
import kapwad.reader.app.utils.dialog.WebviewDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentMethodFragment : Fragment() {

    private var _binding: FragmentPaymentMethodBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as TopUpPointsActivity }
    private val viewModel: WalletViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentMethodBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        observeWallet()
    }

    private fun setupClickListener() = binding.run{
        continueButton.text = "Top Up ${activity.amount} Points"
        //balanceTextView.text = "₱${activity.amount}"

        backImageView.setOnSingleClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }

        continueButton.setOnSingleClickListener {
            viewModel.doTopupPoints(activity.amount.replace("," , ""))
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
            is WalletViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.SuccessTopup -> {
                activity.hideLoadingDialog()
                openWebViewDialog(viewState.webUrl.orEmpty())
            }
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun openWebViewDialog(webUrl: String) {
        WebviewDialog.openDialog(
            childFragmentManager,
            webUrl,
            object : WebviewDialog.WebViewListener {
                override fun onDissmiss() {
                    val intent = MainActivity.getIntent(activity,"asds")
                    startActivity(intent)
//                    CommonLogger.devLog("huhua","close webview")
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}