package com.lionscare.app.ui.verify.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentChooseKycProcessBinding
import com.lionscare.app.ui.verify.activity.AccountVerificationActivity
import com.lionscare.app.utils.setOnSingleClickListener

class ChooseKYCProcessFragment : Fragment() {

    private var _binding: FragmentChooseKycProcessBinding? = null
    private val binding get() = _binding!!
    private var isIdVerified : Boolean? = false
    private var isAddressVerified : Boolean? = false
    private val activity by lazy { requireActivity() as AccountVerificationActivity }

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
        setupClickListener()
        setUpDetails()
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

        }

        addressLinearLayout.setOnSingleClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}