package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentGroupCreateBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupCreateFragment: Fragment() {
    private var _binding: FragmentGroupCreateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }

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
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitle(R.string.lbl_create_group)
    }

    private fun setView() = binding.run{
//        firstNameEditText.doOnTextChanged {
//                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
//        }
    }

    private fun setClickListeners() = binding.run {
        continueButton.setOnSingleClickListener {
//            if (firstNameEditText.text.toString().isEmpty()){
//                firstNameTextInputLayout.error = "Field is required"
//            }
//                findNavController().navigate(RegisterPrimaryInfoFragmentDirections.actionNavigationOtp())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}