package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.data.repositories.member.response.PendingMemberData
import com.lionscare.app.databinding.FragmentGroupMembershipReqBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.adapter.GroupsInvitesAdapter
import com.lionscare.app.ui.group.viewmodel.MemberViewModel
import com.lionscare.app.ui.group.viewmodel.MemberViewState
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MembershipRequestFragment : Fragment(), GroupsInvitesAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentGroupMembershipReqBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupsInvitesAdapter? = null
    private val viewModel: MemberViewModel by viewModels()
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupMembershipReqBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setupAdapter()
        observeAllPendingRequestList()
        viewModel.refresh(activity.groupDetails?.id.toString())
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupsInvitesAdapter( requireActivity(),this@MembershipRequestFragment)
        swipeRefreshLayout.setOnRefreshListener(this@MembershipRequestFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter?.addLoadStateListener {
            if(adapter?.hasData() == true){
                placeHolderTextView.isVisible = false
                recyclerView.isVisible = true
            }else{
                placeHolderTextView.isVisible = true
                recyclerView.isVisible = false
            }
        }
    }

    private fun setClickListeners() = binding.run {

    }

    private fun observeAllPendingRequestList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: MemberViewState) {
        when (viewState) {
            MemberViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is MemberViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is MemberViewState.SuccessGetPendingRequest -> showList(viewState.pagingData)
            else -> Unit
        }
    }

    private fun showList(pendingMemberList: PagingData<PendingMemberData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, pendingMemberList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    companion object {
        private const val REQUEST = "REQUEST"
        fun newInstance(): MembershipRequestFragment {
            return MembershipRequestFragment()
        }
    }

    override fun onItemClicked(data: PendingMemberData) {
        Toast.makeText(requireActivity(),"Clicked : ${data.user?.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onAcceptClicked(data: PendingMemberData) {
        Toast.makeText(requireActivity(),"Accept : ${data.user?.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDeclineClicked(data: PendingMemberData) {
       Toast.makeText(requireActivity(),"Declined : ${data.user?.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onCancelClicked(data: PendingMemberData) {
        Toast.makeText(requireActivity(),"Canceled : ${data.user?.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        viewModel.refresh(activity.groupDetails?.id.toString())
    }
}