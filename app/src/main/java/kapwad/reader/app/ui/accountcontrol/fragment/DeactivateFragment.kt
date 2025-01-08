package kapwad.reader.app.ui.accountcontrol.fragment

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
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.R
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.databinding.FragmentDeactivateBinding
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.ui.accountcontrol.activity.AccountControlActivity
import kapwad.reader.app.ui.accountcontrol.viewmodel.AccountControlViewModel
import kapwad.reader.app.ui.accountcontrol.viewmodel.AccountControlViewState
import kapwad.reader.app.utils.dialog.WebviewDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeactivateFragment : Fragment() {
    private var _binding: FragmentDeactivateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as AccountControlActivity }
    private val viewModel : AccountControlViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeactivateBinding.inflate(
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
        activity.setToolbarTitle(getString(R.string.deactivate_account))
    }
    private fun setOnClickListeners() = binding.run {
      //  val username = AuthEncryptedDataManager().getUserBasicInfo().name
       // titleTextView.text = "$username deactivate this account?"
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
                findNavController().navigate(DeactivateFragmentDirections.actionDeactivateFragmentToAccountControlOTPFragment())
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