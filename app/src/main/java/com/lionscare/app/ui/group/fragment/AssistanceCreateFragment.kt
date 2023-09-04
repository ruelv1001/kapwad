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
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.generalsetting.response.RequestAssistanceData
import com.lionscare.app.databinding.FragmentAssistanceCreateBinding
import com.lionscare.app.ui.generalSetting.viewmodel.GeneralSettingViewModel
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.RequestAssistanceDataAdapter
import com.lionscare.app.ui.group.viewmodel.GeneralSettingViewState
import com.lionscare.app.ui.group.viewmodel.GroupViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssistanceCreateFragment : Fragment() {

    private var _binding: FragmentAssistanceCreateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val generalSettingViewModel : GeneralSettingViewModel by viewModels()

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
        generalSettingViewModel.getRequestAssistanceReasons()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_add_assistance))
    }

    private fun setClickListeners() = binding.run {
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
                othersTextInputLayout.isVisible = selectedValue == "Other"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun observeGeneralSetting() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                generalSettingViewModel.generalSettingSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: GeneralSettingViewState) {
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