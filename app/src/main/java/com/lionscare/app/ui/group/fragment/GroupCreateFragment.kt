package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.databinding.FragmentGroupCreateBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.viewmodel.GroupViewModel
import com.lionscare.app.ui.group.viewmodel.GroupViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupCreateFragment : Fragment() {
    private var _binding: FragmentGroupCreateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: GroupViewModel by viewModels()
    private var groupType = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupCreateBinding.inflate(
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
        setUpSpinner()
        onResume()
        observerGroup()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_create_group))
    }

    private fun setView() = binding.run {
//        firstNameEditText.doOnTextChanged {
//                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
//        }
    }

    private fun setUpSpinner() = binding.run {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.group_type_items,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupTypeSpinner.adapter = adapter
        groupTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                groupType = when (selectedItem) {
                    "Immediate Family" -> "immediate_family"
                    "Organization" -> "organization"
                    "Clan" -> "clan"
                    else -> "immediate_family"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setClickListeners() = binding.run {
        continueButton.setOnSingleClickListener {

            val groupPrivacy = if (publicRadioButton.isChecked) {
                "public"
            } else {
                "private"
            }

            val approval = if (approvalSwitch.isChecked) {
                1
            } else {
                0
            }

            val createGroupRequest = CreateGroupRequest(
                name = nameEditText.text.toString(),
                type = groupType,
                privacy = groupPrivacy,
                passcode = passwordEditText.text.toString(),
                with_approval = approval
            )

            viewModel.createGroup(createGroupRequest)
        }

        publicLinearLayout.setOnSingleClickListener {
            publicRadioButton.isChecked = true
            privateRadioButton.isChecked = false
            publicLinearLayout.isSelected = true
            privateRelativeLayout.isSelected = false
            passwordLinearLayout.visibility = View.GONE
        }

        privateRelativeLayout.setOnSingleClickListener {
            privateRadioButton.isChecked = true
            publicRadioButton.isChecked = false
            privateRelativeLayout.isSelected = true
            publicLinearLayout.isSelected = false
            passwordLinearLayout.visibility = View.VISIBLE
        }
    }

    private fun observerGroup() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.groupSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: GroupViewState) {
        when (viewState) {
            is GroupViewState.Loading -> showLoadingDialog(R.string.loading)
            is GroupViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }
            is GroupViewState.SuccessCreateGroup -> {
                hideLoadingDialog()
                Toast.makeText(requireActivity(),viewState.createGroupResponse?.msg, Toast.LENGTH_SHORT).show()
                findNavController().navigate(GroupCreateFragmentDirections.actionNavigationGroupInvite())
            }
            is GroupViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            is GroupViewState.SuccessUpdateGroup -> Unit
            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as GroupActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as GroupActivity).hideLoadingDialog()
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.group_name?.get(0)?.isNotEmpty() == true) nameTextInputLayout.error = errorsData.group_name?.get(0)
        if (errorsData.group_passcode?.get(0)?.isNotEmpty() == true) passwordTextInputLayout.error = errorsData.group_passcode?.get(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}