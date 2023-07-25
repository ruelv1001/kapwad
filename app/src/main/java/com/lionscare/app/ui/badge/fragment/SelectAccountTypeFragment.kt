package com.lionscare.app.ui.badge.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.R
import com.lionscare.app.data.model.AccountTypeModel
import com.lionscare.app.databinding.FragmentSelectAccountTypeBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.badge.adapter.AccountTypeAdapter
import com.lionscare.app.utils.setOnSingleClickListener

class SelectAccountTypeFragment : Fragment(), AccountTypeAdapter.OnClickCallback {

    private var _binding: FragmentSelectAccountTypeBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AccountTypeAdapter? = null
    private var hasSelected = false
    private var accountTypeList = emptyList<AccountTypeModel>()
    private val activity by lazy { requireActivity() as VerifiedBadgeActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAccountTypeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupClickListener()
    }

    private fun setupList() {
        adapter = AccountTypeAdapter(requireContext(), this@SelectAccountTypeFragment)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter

        accountTypeList = listOf(
            AccountTypeModel(
                getString(R.string.account_type_influencer_text),
                R.drawable.ic_thumbs_up
            ),
            AccountTypeModel(
                getString(R.string.account_type_public_servant_text),
                R.drawable.ic_public_servant
            ),
            AccountTypeModel(getString(R.string.account_type_npo_text), R.drawable.ic_npo)
        )

        adapter?.appendData(accountTypeList)
    }

    private fun setupClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if(hasSelected){
                findNavController().navigate(SelectAccountTypeFragmentDirections.actionNavigationSelectAccountTypeToNavigationVerifyAccountType())
            }else{
                Toast.makeText(requireActivity(), getString(R.string.account_type_error_message_text), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if(hasSelected){
            val selectedPosition = activity.selectedPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                adapter?.setSelectedPosition(selectedPosition)
            }
        }
    }

    override fun onItemClicked(data: AccountTypeModel, position: Int) {
        adapter?.setSelectedItem(data)
        activity.accountType = data.title.toString()
        activity.selectedPosition = position
        adapter?.setSelectedPosition(position)
        hasSelected = true
    }
}