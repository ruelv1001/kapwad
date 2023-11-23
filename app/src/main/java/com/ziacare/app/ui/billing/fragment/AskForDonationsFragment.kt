package com.ziacare.app.ui.billing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ziacare.app.databinding.FragmentAskForDonationsBinding
import com.ziacare.app.databinding.FragmentBillingMainDetailsBinding
import com.ziacare.app.ui.billing.activity.BillingActivity
import com.ziacare.app.ui.billing.viewmodel.BillingViewModel
import com.ziacare.app.ui.billing.viewstate.BillingViewState
import com.ziacare.app.utils.setOnSingleClickListener
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
            viewModel.shouldShowDonationRequestsViews = true
            viewModel.currentFragmentRoute = "request_add"
         findNavController().navigate(AskForDonationsFragmentDirections.actionAskForDonationsFragmentToAskForDonationsGroupRequestFragment())
        }
        askInCustomRequestLinearLayout.setOnSingleClickListener {
            viewModel.shouldShowDonationRequestsViews = true
            viewModel.currentFragmentRoute = "request_add"
         findNavController().navigate(AskForDonationsFragmentDirections.actionAskForDonationsFragmentToAskForDonationsCustomRequestFragment())
        }
        listOfGroupRequestLinearLayout.setOnSingleClickListener {
            viewModel.shouldShowDonationRequestsViews = false
            viewModel.currentFragmentRoute = "request_list"
            findNavController().navigate(AskForDonationsFragmentDirections.actionAskForDonationsFragmentToAskForDonationsGroupRequestFragment())
        }
        listOfPeopleCustomRequestLinearLayout.setOnSingleClickListener {
            viewModel.shouldShowDonationRequestsViews = false
            viewModel.currentFragmentRoute = "request_list"
            findNavController().navigate(AskForDonationsFragmentDirections.actionAskForDonationsFragmentToAskForDonationsCustomRequestFragment())
        }
        continueButton.setOnSingleClickListener {
            //todo
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
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