package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentGroupManageBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.dialog.RemoveConfirmationDialog
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupManageFragment : Fragment() {
    private var _binding: FragmentGroupManageBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel : MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupManageBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setView()
        observeMember()
        setDetails()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_manage_group))
    }

    private fun setView() = binding.run {
//        firstNameEditText.doOnTextChanged {
//                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
//        }
    }

    private fun setDetails() = binding.run {
        titleTextView.text = activity.groupDetails?.name
        membersTextView.text = context?.resources?.getQuantityString(
            R.plurals.member_plural,
            activity.groupDetails?.member_count?: 0,
            activity.groupDetails?.member_count?: 0
        )
        membershipNotifRelativeLayout.isVisible = activity.groupDetails?.pending_requests_count !=0
        membershipCountTextView.text = activity.groupDetails?.pending_requests_count.toString()

        if (activity.groupDetails?.type.equals("organization")) {
            typeFamTextView.isVisible = false
            typeOrgTextView.isVisible = true
        } else {
            typeFamTextView.isVisible = true
            typeOrgTextView.isVisible = false
        }

        editGroupImageView.isVisible = activity.groupDetails?.owner_user_id == viewModel.user.id
    }

    private fun setClickListeners() = binding.run {
        assistanceLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupAssistance())
        }
        membershipLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupMembership())
        }
        statisticsLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupStatistics())
        }
        //TODO only visible when group admins are the user
        transactionLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupTransaction())
        }
        editGroupImageView.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupUpdate())
        }
        leaveGroupLinearLayout.setOnSingleClickListener {
            RemoveConfirmationDialog.newInstance(
                object : RemoveConfirmationDialog.ConfirmationCallback {
                    override fun onConfirm(id: String) {
                        viewModel.leaveGroup(id)
                    }
                },
                title = "Are you sure you want to leave ${activity.groupDetails?.name}?",
                groupId = activity.groupDetails?.id
            ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
        }
    }

    private fun observeMember() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                memberHandleViewState(viewState)
            }
        }
    }

    private fun memberHandleViewState(viewState: MemberViewState) {
        when (viewState) {
            MemberViewState.Loading -> showLoadingDialog(R.string.loading)
            is MemberViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is MemberViewState.SuccessLeaveGroup -> {
                hideLoadingDialog()
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_LONG).show()
                val intent = MainActivity.getIntent(activity)
                startActivity(intent)
                activity.finishAffinity()
            }
            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as GroupActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as GroupActivity).hideLoadingDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val START_MANAGE = "START_MANAGE"
    }
}