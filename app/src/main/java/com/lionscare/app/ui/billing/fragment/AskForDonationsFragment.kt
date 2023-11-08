package com.lionscare.app.ui.billing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lionscare.app.databinding.FragmentAskForDonationsBinding
import com.lionscare.app.databinding.FragmentBillingMainDetailsBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.ui.billing.viewmodel.BillingViewModel
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AskForDonationsFragment: Fragment()  {
    private var _binding: FragmentAskForDonationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BillingViewModel by activityViewModels()
    private val activity by lazy { requireActivity() as BillingActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAskForDonationsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBillingFlow()
        setOnClickListeners()
        setContentViews()
    }

    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            else -> Unit
        }
    }

    private fun setOnClickListeners() = binding.run {
        askInGroupRequestLinearLayout.setOnSingleClickListener {
         findNavController().navigate(AskForDonationsFragmentDirections.actionAskForDonationsFragmentToAskForDonationsGroupRequestFragment())
        }
        askInCustomRequestLinearLayout.setOnSingleClickListener {
        //todo
        }
        listOfGroupRequestLinearLayout.setOnSingleClickListener {
        //todo
        }
        listOfPeopleCustomRequestLinearLayout.setOnSingleClickListener {
        //todo
        }
        continueButton.setOnSingleClickListener {
            //todo
        }
    }

    private fun setContentViews() = binding.run{
        activity.setToolbarTitle("Ask for donations")
        numberOfGroupsRequestedText.text = "3"
        numberOfCustomPeopleRequestedText.text = "5"
    }

    private fun observeBillingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.billingSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }
}