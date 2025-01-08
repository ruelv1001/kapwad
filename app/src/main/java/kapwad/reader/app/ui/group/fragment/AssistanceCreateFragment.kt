package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.assistance.request.CreateAssistanceRequest
import kapwad.reader.app.data.repositories.generalsetting.response.RequestAssistanceData
import kapwad.reader.app.databinding.FragmentAssistanceCreateBinding
import kapwad.reader.app.ui.generalSetting.viewmodel.GeneralSettingViewModel
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.adapter.RequestAssistanceDataAdapter
import kapwad.reader.app.ui.group.viewmodel.AssistanceViewModel
import kapwad.reader.app.ui.group.viewmodel.AssistanceViewState
import kapwad.reader.app.ui.group.viewmodel.GeneralSettingViewState
import kapwad.reader.app.utils.setAmountFormat
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssistanceCreateFragment : Fragment() {

    private var _binding: FragmentAssistanceCreateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val generalSettingViewModel : GeneralSettingViewModel by viewModels()
    private val viewModel: AssistanceViewModel by viewModels()
    private var reason = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAssistanceCreateBinding.inflate(
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
        observeGeneralSetting()
        observeAssistance()
        generalSettingViewModel.getRequestAssistanceReasons()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_add_assistance))
    }

    private fun setClickListeners() = binding.run {
        amountEditText.setAmountFormat()
        amountEditText.doOnTextChanged { text, start, before, count ->
            amountTextInputLayout.error = ""
        }
        proceedButton.setOnSingleClickListener {
        }
        }


    private fun setUpSpinner(data: List<RequestAssistanceData>) = binding.run {
        val adapter = RequestAssistanceDataAdapter(requireActivity(), data)
        purposeSpinner.adapter = adapter
        purposeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = adapter.getItem(position)
                val selectedValue = selectedItem?.name
                reason = selectedItem?.code.toString()
//                othersTextInputLayout.isVisible = selectedValue == "Other"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun observeGeneralSetting() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                generalSettingViewModel.generalSettingSharedFlow.collect { viewState ->
                    generalHandleViewState(viewState)
                }
            }
        }
    }

    private fun generalHandleViewState(viewState: GeneralSettingViewState) {
        when (viewState) {
            is GeneralSettingViewState.Loading -> showLoadingDialog(R.string.loading)
            is GeneralSettingViewState.Success -> {
                hideLoadingDialog()
                viewState.requestAssistanceLOVResponse?.data?.let { setUpSpinner(it) }
            }
            is GeneralSettingViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
        }
    }

    private fun observeAssistance() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assistanceSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: AssistanceViewState) {
        when (viewState) {
            is AssistanceViewState.Loading -> showLoadingDialog(R.string.loading)
            is AssistanceViewState.SuccessCreateAssistance -> {
                hideLoadingDialog()
                requireActivity().toastSuccess(viewState.message, CpmToast.LONG_DURATION)
                activity.onBackPressedDispatcher.onBackPressed()
            }
            is AssistanceViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
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
}