package com.lionscare.app.ui.verify.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentChooseKycProcessBinding
import com.lionscare.app.ui.settings.viewmodel.ProfileViewState
import com.lionscare.app.ui.verify.VerifyViewModel
import com.lionscare.app.ui.verify.activity.AccountVerificationActivity
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import kotlinx.coroutines.launch

class ChooseKYCProcessFragment : Fragment() {

    private var _binding: FragmentChooseKycProcessBinding? = null
    private val binding get() = _binding!!
    private var isIdVerified : Boolean? = false
    private var isAddressVerified : Boolean? = false
    private val activity by lazy { requireActivity() as AccountVerificationActivity }

    private val viewModel : VerifyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseKycProcessBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        setupClickListener()
        setUpDetails()
        viewModel.getVerificationStatus()

    }



    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.kycSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }


    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> Unit
            is ProfileViewState.SuccessGetVerificationStatus -> {
                if (viewState.profileVerificationResponse.data?.id_status == "pending"
                    && viewState.profileVerificationResponse.data?.address_status == "pending"
                    ){
                    binding.statusTextView.text = "Pending"

                    binding.verifyTextView.text = "ID Status: ${viewState.profileVerificationResponse.data?.id_status }\n" +
                            "ID Type: ${viewState.profileVerificationResponse.data?.id_type}\n" +
                            "Submitted on: ${viewState.profileVerificationResponse.data?.id_submitted_date?.date_db}"

                    binding.verifyAddressTextView.text = "Address Proof Status: ${viewState.profileVerificationResponse.data?.address_status }\n" +
                            "Address Proof Type: ${viewState.profileVerificationResponse.data?.address_type}\n" +
                            "Submitted on: ${viewState.profileVerificationResponse.data?.address_submitted_date?.date_db}"
                }

                if (viewState.profileVerificationResponse.data?.id_status == "approved"
                    && viewState.profileVerificationResponse.data?.address_status == "approved"
                ){
                    binding.statusTextView.text = "Approved"

                    binding.verifyTextView.text = "ID Status: ${viewState.profileVerificationResponse.data?.id_status }\n" +
                            "ID Type: ${viewState.profileVerificationResponse.data?.id_type}\n" +
                            "Submitted on: ${viewState.profileVerificationResponse.data?.id_submitted_date?.date_db}"

                    binding.verifyAddressTextView.text = "Address Proof Status: ${viewState.profileVerificationResponse.data?.address_status }\n" +
                            "Address Proof Type: ${viewState.profileVerificationResponse.data?.address_type}\n" +
                            "Submitted on: ${viewState.profileVerificationResponse.data?.address_submitted_date?.date_db}"
                }


                if (viewState.profileVerificationResponse.data?.id_status == "declined"
                    && viewState.profileVerificationResponse.data?.address_status == "declined"
                ){
                    binding.statusTextView.text = "Declined"

                    binding.verifyTextView.text = "ID Status: ${viewState.profileVerificationResponse.data?.id_status }\n" +
                            "ID Type: ${viewState.profileVerificationResponse.data?.id_type}\n" +
                            "Submitted on: ${viewState.profileVerificationResponse.data?.id_submitted_date?.date_db}"

                    binding.verifyAddressTextView.text = "Address Proof Status: ${viewState.profileVerificationResponse.data?.address_status }\n" +
                            "Address Proof Type: ${viewState.profileVerificationResponse.data?.address_type}\n" +
                            "Submitted on: ${viewState.profileVerificationResponse.data?.address_submitted_date?.date_db}"
                }


            }
            is ProfileViewState.PopupError -> {
                showPopupError(requireContext(), childFragmentManager, viewState.errorCode, viewState.message)
            }
            else -> Unit
        }
    }



    private fun setUpDetails() = binding.run{
        activity.setTitle(getString(R.string.verify_account_title))

        if(isIdVerified == true){
            idArrowImageView.visibility = View.GONE
            idCheckImageView.visibility = View.VISIBLE
        }
        if(isAddressVerified == true){
            addressArrowImageView.visibility = View.GONE
            addressCheckImageView.visibility = View.VISIBLE
        }
    }

    private fun setupClickListener() = binding.run {
        validIdLinearLayout.setOnSingleClickListener {
            findNavController().navigate(ChooseKYCProcessFragmentDirections.actionNavigationChooseKycToNavigationUploadId())
        }

        addressLinearLayout.setOnSingleClickListener {
            findNavController().navigate(ChooseKYCProcessFragmentDirections.actionNavigationChooseKycToNavigationProofOfAddress())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}