package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import kapwad.reader.app.R
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.repositories.group.request.CreateGroupRequest
import kapwad.reader.app.data.repositories.group.response.GroupData
import kapwad.reader.app.databinding.FragmentGroupCreateBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.viewmodel.GroupViewModel
import kapwad.reader.app.ui.group.viewmodel.GroupViewState
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
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
        onResume()
        observerGroup()
        activity.groupDetails?.id?.let { viewModel.showGroup(it) }
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_update_group))
    }

    private fun setUpSpinner(items : ArrayList<String>) = binding.run {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupTypeSpinner.adapter = adapter
        //if coming from update, id should be existing therefore dropdown will not be enabled
        groupTypeSpinner.isEnabled = activity.groupDetails?.id == null
        groupTypeSpinner.isVisible = false
        typeInputLayout.isVisible = true
        typeEditText.setText( when(items[0]) {
            "immediate_family" -> "Immediate Family"
            "organization" -> "Organization"
            "community"  -> "Community"
            else -> items[0]
        })

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
                type = if(typeEditText.text.toString().lowercase() == "immediate family") "immediate_family" else typeEditText.text.toString().lowercase(),
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
                requireActivity().toastSuccess(viewState.createGroupResponse?.msg.toString(), CpmToast.SHORT_DURATION)
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
                requireActivity().toastSuccess(viewState.createGroupResponse?.msg.toString(), CpmToast.LONG_DURATION)
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
        /*groupTypeSpinner.setSelection(
            when (data.type) {
                "organization" -> 1
                "community" -> 2
                else -> 0
            }
        )*/
        setUpSpinner(arrayListOf(data.type.toString()))
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
        if (errorsData.name?.get(0)?.isNotEmpty() == true) nameTextInputLayout.error = errorsData.name?.get(0)
        if (errorsData.group_passcode?.get(0)?.isNotEmpty() == true) passwordTextInputLayout.error = errorsData.group_passcode?.get(0)
        if (errorsData.passcode?.get(0)?.isNotEmpty() == true) passwordTextInputLayout.error = errorsData.passcode?.get(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}