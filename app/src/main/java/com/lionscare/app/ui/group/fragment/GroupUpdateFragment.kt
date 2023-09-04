package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.group.request.CreateGroupRequest
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.FragmentGroupCreateBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.viewmodel.GroupViewModel
import com.lionscare.app.ui.group.viewmodel.GroupViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupUpdateFragment : Fragment() {

    private var _binding: FragmentGroupCreateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: GroupViewModel by viewModels()
    private var groupType = ""
    private var groupId = ""

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
        setUpSpinner()
        onResume()
        observerGroup()
        activity.groupDetails?.id?.let { viewModel.showGroup(it) }
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_update_group))
    }

    private fun setUpSpinner() = binding.run {
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.group_type_items,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupTypeSpinner.adapter = adapter
        //if coming from update, id should be existing therefore dropdown will not be enabled
        groupTypeSpinner.isEnabled = activity.groupDetails?.id == null

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
                group_id = groupId,
                name = nameEditText.text.toString(),
                type = groupType,
                privacy = groupPrivacy,
                passcode = passwordEditText.text.toString(),
                with_approval = approval
            )
            viewModel.updateGroup(createGroupRequest)
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
                handleInputError(viewState.errorData ?: ErrorsData())
            }

            is GroupViewState.SuccessCreateGroup -> {
                hideLoadingDialog()
                Toast.makeText(
                    requireActivity(),
                    viewState.createGroupResponse?.msg,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is GroupViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is GroupViewState.SuccessUpdateGroup -> {
                hideLoadingDialog()
                Toast.makeText(
                    requireActivity(),
                    viewState.createGroupResponse?.msg,
                    Toast.LENGTH_LONG
                ).show()
                requireActivity().finish()
            }

            is GroupViewState.SuccessShowGroup -> {
                hideLoadingDialog()
                viewState.createGroupResponse?.data?.let { setDetails(it) }
                groupId = viewState.createGroupResponse?.data?.id.toString()
            }

            else -> Unit
        }
    }

    private fun setDetails(data: GroupData) = binding.run {
        nameEditText.setText(data.name)
        groupTypeSpinner.setSelection(
            when (data.type) {
                "organization" -> 1
                "clan" -> 2
                else -> 0
            }
        )
        publicRadioButton.isChecked = data.privacy == "public"
        privateRadioButton.isChecked = data.privacy == "private"
        if (data.privacy == "private") {
            passwordLinearLayout.isVisible = true
        }
        approvalSwitch.isChecked = data.with_approval == true
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as GroupActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as GroupActivity).hideLoadingDialog()
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.group_name?.get(0)?.isNotEmpty() == true) nameTextInputLayout.error =
            errorsData.group_name?.get(0)
        if (errorsData.group_passcode?.get(0)?.isNotEmpty() == true) passwordTextInputLayout.error =
            errorsData.group_passcode?.get(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}