package com.ziacare.app.ui.billing.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ziacare.app.data.repositories.baseresponse.DateModel
import com.ziacare.app.data.repositories.baseresponse.UserModel
import com.ziacare.app.data.repositories.billing.response.DonatorData
import com.ziacare.app.data.repositories.member.response.MemberListData
import com.ziacare.app.data.repositories.member.response.User
import com.ziacare.app.databinding.FragmentAskForDonationsCustomRequestBinding
import com.ziacare.app.ui.billing.viewmodel.BillingViewModel
import com.ziacare.app.ui.billing.viewstate.BillingViewState
import com.ziacare.app.ui.group.adapter.GroupMembersAdapter
import com.ziacare.app.ui.group.viewmodel.MemberViewState
import com.ziacare.app.utils.CommonLogger
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AskForDonationsCustomRequestFragment : Fragment(),  GroupMembersAdapter.MembersCallback {
    private var _binding: FragmentAskForDonationsCustomRequestBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupMembersAdapter? = null

    private val viewModel: BillingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAskForDonationsCustomRequestBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeMemberList()
        setOnClickListeners()
        setupAdapter()
        setContentViews()

    }

    private fun setOnClickListeners() = binding.run {
        sendRequestButton.setOnSingleClickListener {
            //get groups data where it is checked
            val peopleWithChecked = adapter?.getCustomData()
                ?.filter{ it.isChecked }
            CommonLogger.instance.sysLogI("Custom Member List", peopleWithChecked)
            //TODO
        }

        adapter?.setOnRemoveButtonClicked {
            TODO()
        }
    }
    override fun onItemClicked(data: MemberListData) {
        //todo
    }

    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            is BillingViewState.LoadingMembers -> binding.swipeRefreshLayout.isRefreshing = true
            is BillingViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            //TODO GET ALL MEMBERS LIST
            else -> Unit
        }
    }

    private fun setContentViews(){
        if(viewModel.shouldShowDonationRequestsViews){
            binding.noteSendRequestText.isVisible = false
            binding.sendRequestButton.isVisible = false
            binding.searchTextInputLayout.isVisible = false
        }else{
            binding.noteSendRequestText.isVisible = true
            binding.sendRequestButton.isVisible = true
            binding.searchTextInputLayout.isVisible = true
        }

        //TODO change api
        val sampleData = mutableListOf<MemberListData>()
        sampleData.add(
            MemberListData(
                date_joined = DateModel("11/23/2023","11/23/2023","11/23/2023","11/23/2023","11/23/2023"),
                user = User(id = "1", name = "Von Denuelle Tandoc", qrcode = "876545679872"),
                id = 1
            )
        )
        sampleData.add(
            MemberListData(
                date_joined = DateModel("11/23/2023","11/23/2023","11/23/2023","11/23/2023","11/23/2023"),
                user = User(id = "2", name = "Von Denuelle Tandoc", qrcode = "876545679872"),
                id =2
            )
        )
        sampleData.add(
            MemberListData(
                date_joined = DateModel("11/23/2023","11/23/2023","11/23/2023","11/23/2023","11/23/2023"),
                user = User(id = "3", name = "Von Denuelle Tandoc", qrcode = "876545679872"),
                id =3
            )
        )

        val samplePagingData: PagingData<MemberListData> = PagingData.from(sampleData)
        showList(samplePagingData)
        binding.noteSendRequestText.text = "Note: This ${viewModel.billingStatementNumber} will be available in their Asked for Donations Bulletin Board"
    }
    private fun showList(memberListData: PagingData<MemberListData>){
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, memberListData)
    }
    private fun setupAdapter() = binding.run {
        adapter = GroupMembersAdapter(
            requireActivity(),
            this@AskForDonationsCustomRequestFragment,
            shouldShowDonationRequestsViews = viewModel.shouldShowDonationRequestsViews,
        )
        swipeRefreshLayout.setOnRefreshListener { swipeRefreshLayout.isRefreshing = false }
        linearLayoutManager = LinearLayoutManager(context)
        membersRecyclerView.layoutManager = linearLayoutManager
        membersRecyclerView.adapter = adapter

        adapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    placeHolderTextView.isVisible = false
                    shimmerLayout.isVisible = true
                    membersRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.Error -> {
                    placeHolderTextView.isVisible = true
                    shimmerLayout.isVisible = false
                    membersRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.NotLoading && adapter?.hasData() == true -> {
                    placeHolderTextView.isVisible = false
                    shimmerLayout.isVisible = false
                    shimmerLayout.stopShimmer()
                    membersRecyclerView.isVisible = true
                }
            }
        }
    }

    private fun observeMemberList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.billingSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireContext() }
        _binding = null
    }
}