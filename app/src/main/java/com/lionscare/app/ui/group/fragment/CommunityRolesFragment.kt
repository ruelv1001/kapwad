package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentGroupCommunityRolesBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityRolesFragment: Fragment() {
    private var _binding: FragmentGroupCommunityRolesBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupCommunityRolesBinding.inflate(
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
        activity.setTitlee(getString(R.string.lbl_group_community_roles))
    }

    private fun setView() = binding.run{
//        firstNameEditText.doOnTextChanged {
//                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
//        }
    }

    private fun setClickListeners() = binding.run {
        activity.getRolesView().setOnSingleClickListener {
            findNavController().navigate(CommunityRolesFragmentDirections.actionNavigationRolesUpdate())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}