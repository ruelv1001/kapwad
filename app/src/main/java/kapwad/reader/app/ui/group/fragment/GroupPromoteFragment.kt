package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.member.response.MemberListData
import kapwad.reader.app.databinding.FragmentGroupInviteBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.adapter.GroupMembersAdapter
import kapwad.reader.app.ui.group.dialog.RemoveConfirmationDialog
import kapwad.reader.app.ui.group.viewmodel.AdminViewModel
import kapwad.reader.app.ui.group.viewmodel.AdminViewState
import kapwad.reader.app.ui.group.viewmodel.MemberViewModel
import kapwad.reader.app.ui.group.viewmodel.MemberViewState
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupPromoteFragment : Fragment(), GroupMembersAdapter.MembersCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentGroupInviteBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private var adapter : GroupMembersAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: MemberViewModel by viewModels()
    private val viewModelAdmin: AdminViewModel by viewModels()

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
        observeMemberList()
        onRefresh()
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupMembersAdapter(requireContext(),this@GroupPromoteFragment)
        swipeRefreshLayout.setOnRefreshListener(this@GroupPromoteFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter?.addLoadStateListener {
            recyclerView.isVisible = adapter?.hasData() == true
        }
    }

    private fun observeMemberList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModelAdmin.adminSharedFlow.collectLatest { viewState ->
                handleViewStateAdmin(viewState)
            }
        }
    }

    private fun handleViewStateAdmin(viewState: AdminViewState) {
        when (viewState) {
            AdminViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is AdminViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is AdminViewState.SuccessGetListOfAdmin -> showList(viewState.pagingData)
            is AdminViewState.SuccessPromoteMember -> {
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                setupAdapter()
                onRefresh()
            }
            else -> Unit
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
            is MemberViewState.SuccessGetListOfMembers -> showList(viewState.pagingData)
            else -> Unit
        }
    }

    private fun showList(memberListData: PagingData<MemberListData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, memberListData)
    }


    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_co_admin))
    }

    private fun setView() = binding.run {
        recyclerView.visibility = View.VISIBLE
       /* searchEditText.doOnTextChanged {
        recyclerView.visibility = View.VISIBLE
        searchEditText.doOnTextChanged {
                text, start, before, count ->
//            firstNameTextInputLayout.error = ""
            if (searchEditText.text?.isNotEmpty() == true){
                closeImageView.visibility = View.VISIBLE
            } else {
                closeImageView.visibility = View.GONE
            }
        }
        closeImageView.setOnSingleClickListener {
            closeImageView.visibility = View.GONE
            searchEditText.setText("")
        }*/
    }

    private fun setClickListeners() = binding.run {
//        completeButton.setOnSingleClickListener {
//            if (firstNameEditText.text.toString().isEmpty()){
//                firstNameTextInputLayout.error = "Field is required"
//            }
//                findNavController().navigate(GroupInviteFragmentDirection.actionNavigationOtp())
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: MemberListData) {
        RemoveConfirmationDialog.newInstance(
            object : RemoveConfirmationDialog.ConfirmationCallback {
                override fun onConfirm(id: String) {
                    viewModelAdmin.doPromoteMember(activity.groupDetails?.id!!,id.toInt())
                }
            },
            title = "Promote Selected Member?",
            groupId = data.id.toString()
        ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
    }

    companion object {
        private const val START_INVITE = "START_INVITE"
    }

    override fun onRefresh() {
        viewModel.refreshListOfMembers(activity.groupDetails?.id.toString(), false)

    }
}