package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lionscare.app.R
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.databinding.FragmentGroupAssistanceReqDetailsBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.viewmodel.AssistanceViewModel
import com.lionscare.app.ui.group.viewmodel.AssistanceViewState
import com.lionscare.app.utils.copyToClipboard
import com.lionscare.app.utils.currencyFormat
import com.lionscare.app.utils.loadAvatar
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssistanceRequestDetailsFragment : Fragment() {
    private var _binding: FragmentGroupAssistanceReqDetailsBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: AssistanceViewModel by viewModels()

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
        profileImageView.loadAvatar(data.user?.avatar?.thumb_path, requireActivity())
        when (data.status) {
            "declined" -> {
                cancelButton.visibility = View.GONE
                requestLinearLayout.visibility = View.GONE
                approvedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.GONE
                declinedTextView.visibility = View.VISIBLE
            }

            "cancelled" -> {
                cancelButton.visibility = View.GONE
                requestLinearLayout.visibility = View.GONE
                approvedTextView.visibility = View.GONE
                declinedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.VISIBLE
            }

            "approved" -> {
                cancelButton.visibility = View.GONE
                requestLinearLayout.visibility = View.GONE
                declinedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.GONE
                approvedTextView.visibility = View.VISIBLE
            }

            else -> { // pending
                approvedTextView.visibility = View.GONE
                declinedTextView.visibility = View.GONE
                cancelledTextView.visibility = View.GONE
                if (data.user?.id == viewModel.user.id) {
                    cancelButton.isVisible = true
                } else {
                    requestLinearLayout.isVisible = activity.groupDetails?.is_admin == true
                }
            }
        }

    }

    private fun setClickListeners() = binding.run {
//        assistanceLinearLayout.setOnSingleClickListener {
//            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupAssistance())
//        }
        approveButton.setOnSingleClickListener {
            viewModel.approveAssistance(
                activity.referenceId,
                activity.groupDetails?.id.toString(),
                messageEditText.text.toString()
            )
        }
        declineButton.setOnSingleClickListener {
            viewModel.declineAssistance(
                activity.referenceId,
                activity.groupDetails?.id.toString(),
                messageEditText.text.toString()
            )
        }
        cancelButton.setOnSingleClickListener {
            viewModel.cancelAssistance(activity.referenceId, activity.groupDetails?.id.toString())
        }
        refIDTextView.setOnSingleClickListener {
            activity.copyToClipboard(refIDTextView.text.toString())
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
            }
            is AssistanceViewState.SuccessApproveDeclineAssistance -> {
                activity.hideLoadingDialog()
                Toast.makeText(activity, viewState.message, Toast.LENGTH_SHORT).show()
                onResume()
            }
            is AssistanceViewState.SuccessCancelAssistance -> {
                activity.hideLoadingDialog()
                Toast.makeText(activity, viewState.message, Toast.LENGTH_SHORT).show()
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