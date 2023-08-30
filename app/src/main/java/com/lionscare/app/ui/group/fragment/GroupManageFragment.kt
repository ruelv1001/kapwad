package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentGroupManageBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.viewmodel.GroupViewModel
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupManageFragment : Fragment() {
    private var _binding: FragmentGroupManageBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel : GroupViewModel by viewModels()

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
        titleTextView.text = activity.groupDetails?.group_name
        if(activity.groupDetails?.group_type.equals("organization")){
            typeFamTextView.isVisible = false
            typeOrgTextView.isVisible = true
        }else{
            typeFamTextView.isVisible = true
            typeOrgTextView.isVisible = false
        }

        editGroupLinearLayout.isVisible = activity.groupDetails?.owner_user_id == viewModel.user.id
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
        transactionLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupTransaction())
        }
        editGroupLinearLayout.setOnSingleClickListener {
            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupUpdate())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val START_MANAGE = "START_MANAGE"
    }
}