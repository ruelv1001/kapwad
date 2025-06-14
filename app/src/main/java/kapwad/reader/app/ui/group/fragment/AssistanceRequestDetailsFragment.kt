package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.assistance.response.CreateAssistanceData
import kapwad.reader.app.data.repositories.baseresponse.Avatar
import kapwad.reader.app.data.repositories.wallet.response.QRData
import kapwad.reader.app.databinding.FragmentGroupAssistanceReqDetailsBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.viewmodel.AssistanceViewModel
import kapwad.reader.app.ui.group.viewmodel.AssistanceViewState
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.utils.copyToClipboard
import kapwad.reader.app.utils.currencyFormat
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssistanceRequestDetailsFragment : Fragment() {
    private var _binding: FragmentGroupAssistanceReqDetailsBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: AssistanceViewModel by viewModels()
    private var userId = ""
    private var avatar =Avatar()
    private var userName = ""
    private var assistanceId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupAssistanceReqDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeAssistance()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_request_details))
        viewModel.getAssistanceInfo(activity.referenceId, activity.groupDetails?.id.toString())
    }

    private fun setView(data: CreateAssistanceData) = binding.run {
        titleTextView.text = data.user?.name
        referenceTextView.text = data.user?.qrcode
        reasonTextView.text = data.reason
        refIDTextView.text = data.reference_id
        dateTimeTextView.text = data.date_requested?.datetime_ph
        amountTextView.text = currencyFormat(data.amount.toString())
        remarksTextView.text = data.note
        dateProcessedTextView.text = data.date_processed?.datetime_ph
        if(data.has_sent == true){
            sendTextView.isVisible = true
            sendTextView.text = getString(R.string.lbl_sent)
            sendTextView.isEnabled = false
            sendTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_text))
            sendTextView.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))
        }else{
            sendTextView.isVisible = data.status == "approved" && activity.groupDetails?.is_admin == true
            sendTextView.text = getString(R.string.search_user_send_txt)
            sendTextView.isEnabled = true
            sendTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        }
        profileImageView.loadAvatar(data.user?.avatar?.thumb_path, requireActivity())
        when (data.status) {
            "declined" -> {
                cancelButton.visibility = View.GONE
                requestLinearLayout.visibility = View.GONE
                approvedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.GONE
                declinedTextView.visibility = View.VISIBLE
                dateProcessedTextView.visibility = View.VISIBLE
                dateProcessedTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red))
            }

            "cancelled" -> {
                cancelButton.visibility = View.GONE
                requestLinearLayout.visibility = View.GONE
                approvedTextView.visibility = View.GONE
                declinedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.VISIBLE
                dateProcessedTextView.visibility = View.VISIBLE
                dateProcessedTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            }

            "approved" -> {
                cancelButton.visibility = View.GONE
                requestLinearLayout.visibility = View.GONE
                declinedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.GONE
                approvedTextView.visibility = View.VISIBLE
                dateProcessedTextView.visibility = View.VISIBLE
                dateProcessedTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue_text))
            }

            else -> { // pending
                approvedTextView.visibility = View.GONE
                declinedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.GONE
                dateProcessedTextView.visibility = View.GONE

            }
        }

    }

    private fun setClickListeners() = binding.run {
//        assistanceLinearLayout.setOnSingleClickListener {
//            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupAssistance())
//        }
        approveButton.setOnSingleClickListener {
            if(viewModel. getUserKYC() != "completed"){
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified))
            }else{
                viewModel.approveAssistance(
                    activity.referenceId,
                    activity.groupDetails?.id.toString(),
                    messageEditText.text.toString()
                )
            }

        }
        declineButton.setOnSingleClickListener {
            if(viewModel. getUserKYC() != "completed"){
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified))
            }else{
                viewModel.declineAssistance(
                    activity.referenceId,
                    activity.groupDetails?.id.toString(),
                    messageEditText.text.toString()
                )
            }
        }
        cancelButton.setOnSingleClickListener {
            viewModel.cancelAssistance(activity.referenceId, activity.groupDetails?.id.toString())
        }
        refIDTextView.setOnSingleClickListener {
            activity.copyToClipboard(refIDTextView.text.toString())
        }
        sendTextView.setOnSingleClickListener {
            if(viewModel. getUserKYC() != "completed"){
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified))
            }else{
                val intent = WalletActivity.getIntent(
                    requireActivity(),
                    "Send Points",
                    true,
                    groupSenderId = activity.groupDetails?.id.orEmpty(),
                    assistanceId = assistanceId,
                    qrData = QRData(
                        id = userId,
                        name = userName,
                        amount = amountTextView.text.toString(),
                        avatar = avatar
                    ),
                    start = "START_INPUT"
                )
                startActivity(intent)
            }
        }
    }

    private fun observeAssistance() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assistanceSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: AssistanceViewState) {
        when (viewState) {
            is AssistanceViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is AssistanceViewState.SuccessGetAssistanceInfo -> {
                activity.hideLoadingDialog()
                viewState.response?.data?.let { setView(it) }
                userId = viewState.response?.data?.user?.id.toString()
                avatar = viewState.response?.data?.user?.avatar ?: Avatar()
                userName = viewState.response?.data?.user?.name.toString()
                assistanceId = viewState.response?.data?.reference_id.toString()
            }
            is AssistanceViewState.SuccessApproveDeclineAssistance -> {
                activity.hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                onResume()
            }
            is AssistanceViewState.SuccessCancelAssistance -> {
                activity.hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                onResume()
            }
            is AssistanceViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> Unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val APPROVED = "APPROVED"
        private const val DECLINED = "DECLINED"
    }
}