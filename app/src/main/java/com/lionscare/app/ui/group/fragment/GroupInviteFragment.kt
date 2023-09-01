package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.member.response.MemberListData
import com.lionscare.app.databinding.FragmentGroupInviteBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.GroupMembersAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupInviteFragment : Fragment(), GroupMembersAdapter.MembersCallback {
    private var _binding: FragmentGroupInviteBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private var adapter : GroupMembersAdapter? = null
    private var dataList: List<SampleData> = emptyList()
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupInviteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setClickListeners()
        setView()
        onResume()
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupMembersAdapter(this@GroupInviteFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        memberRecyclerView.layoutManager = linearLayoutManager
        memberRecyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = R.drawable.img_profile,
                title = "Raquel Castro",
                amount = "LC-000004",
            ),
            SampleData(
                id = R.drawable.img_profile,
                title = "Romeo Dela Cruz",
                amount = "LC-000001",
            )
        )
        //adapter?.submitData(lifecycle, PagingData.from(dataList))
        //complete button removed
//        if (activity.start == START_INVITE){
//            completeButton.text = getText(R.string.lbl_invite)
//        } else {
//            completeButton.text = getText(R.string.lbl_complete)
//        }
    }


    override fun onResume() {
        super.onResume()
        activity.setTitlee("Invite to ${activity.groupDetails?.name}")
    }

    private fun setView() = binding.run {
        searchEditText.doOnTextChanged {
                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
            if (searchEditText.text?.isNotEmpty() == true){
                memberRecyclerView.visibility = View.VISIBLE
                closeImageView.visibility = View.VISIBLE
            } else {
                memberRecyclerView.visibility = View.GONE
                closeImageView.visibility = View.GONE
            }
        }
        closeImageView.setOnSingleClickListener {
            closeImageView.visibility = View.GONE
            searchEditText.setText("")
        }
    }

    private fun setClickListeners() = binding.run {
//        completeButton.setOnSingleClickListener {
//            if (firstNameEditText.text.toString().isEmpty()){
//                firstNameTextInputLayout.error = "Field is required"
//            }
//                findNavController().navigate(GroupInviteFragmentDirection.actionNavigationOtp())
//        }

        activity.getScanImageView().setOnSingleClickListener {
            //TODO scanner
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val START_INVITE = "START_INVITE"
    }

    override fun onItemClicked(data: MemberListData) {

    }
}