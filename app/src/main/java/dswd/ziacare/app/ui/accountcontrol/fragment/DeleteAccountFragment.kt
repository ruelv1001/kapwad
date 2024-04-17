package dswd.ziacare.app.ui.accountcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dswd.ziacare.app.R
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.databinding.FragmentDeleteAccountBinding
import dswd.ziacare.app.security.AuthEncryptedDataManager
import dswd.ziacare.app.ui.accountcontrol.activity.AccountControlActivity
import dswd.ziacare.app.ui.accountcontrol.viewmodel.AccountControlViewModel
import dswd.ziacare.app.ui.accountcontrol.viewmodel.AccountControlViewState
import dswd.ziacare.app.utils.dialog.WebviewDialog
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dswd.ziacare.app.utils.showToastError
import dswd.ziacare.app.utils.showToastSuccess
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeleteAccountFragment : Fragment()  {
    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as AccountControlActivity }
    private val viewModel : AccountControlViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeleteAccountBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        observeDeleteOrDeactivate()
    }

    override fun onResume() {
        super.onResume()
        activity.setToolbarTitle(getString(R.string.delete_account_permanently))
    }
    private fun setOnClickListeners() = binding.run {
        val username = AuthEncryptedDataManager().getUserBasicInfo().name
        titleTextView.text = "$username deactivate this account?"
        deactivateButton.setOnSingleClickListener {
            viewModel.deleteOrDeactivateAccount(reasonId = activity.reasonId.toString(), other = activity.reason.toString(), type = activity.selectedChoice.orEmpty())
        }
        privacyPolicyTextView.setOnSingleClickListener {
            openWebViewDialog("https://ziapay.ph/privacy-policy")
        }
        termsAndConditionsTextView.setOnSingleClickListener {
            openWebViewDialog("https://ziapay.ph/terms")
        }
    }

    private fun observeDeleteOrDeactivate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accountControlSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }
    private fun handleViewState(viewState: AccountControlViewState) {
        when (viewState) {
            is AccountControlViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is AccountControlViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is AccountControlViewState.InputError ->{
                activity.hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }
            is AccountControlViewState.SuccessDeleteOrDeactivateAccount -> {
                activity.hideLoadingDialog()
                findNavController().navigate(DeleteAccountFragmentDirections.actionDeleteAccountFragmentToAccountControlOTPFragment())
                showToastSuccess(requireActivity(), description = viewState.response.msg.toString())
            }
            else -> activity.hideLoadingDialog()

        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.other_reason?.get(0)?.isNotEmpty() == true) {
            showToastError(requireActivity(), description = errorsData.other_reason?.get(0).toString())
        }
    }

    private fun openWebViewDialog(url: String) {
        WebviewDialog.openDialog(
            childFragmentManager,
            url
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}