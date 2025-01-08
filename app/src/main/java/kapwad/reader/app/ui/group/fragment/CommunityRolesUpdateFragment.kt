package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.member.response.MemberListData
import kapwad.reader.app.databinding.FragmentGroupCommunityRolesUpdateBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.adapter.GroupPromoteMembersAdapter
import kapwad.reader.app.ui.group.dialog.RemoveConfirmationDialog
import kapwad.reader.app.ui.group.dialog.SaveSuccessDialog
import kapwad.reader.app.ui.group.viewmodel.AdminViewModel
import kapwad.reader.app.ui.group.viewmodel.AdminViewState
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityRolesUpdateFragment: Fragment(), GroupPromoteMembersAdapter.MembersCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentGroupCommunityRolesUpdateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private var adapter : GroupPromoteMembersAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupCommunityRolesUpdateBinding.inflate(
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
        setupAdapter()
        observeMemberList()
        onRefresh()
    }


    private fun setupAdapter() = binding.run {
        adapter = GroupPromoteMembersAdapter(requireContext(),this@CommunityRolesUpdateFragment, isUpdating = true)
        swipeRefreshLayout.setOnRefreshListener(this@CommunityRolesUpdateFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        adapter?.addLoadStateListener {
            recyclerView.isVisible = adapter?.hasData() == true
        }
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_group_community_roles_update))
    }

    private fun setView() = binding.run{


    }

    private fun setClickListeners() = binding.run {

        addLinearLayout.setOnSingleClickListener {
            if(viewModel.getUserKYC() != "completed"){
                requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified))
            }else{
                findNavController().navigate(CommunityRolesUpdateFragmentDirections.actionNavigationGroupRolesPromote())

            }
        }
        saveButton.setOnSingleClickListener {
            SaveSuccessDialog.newInstance().show(childFragmentManager, SaveSuccessDialog.TAG)
        }

    }

    private fun observeMemberList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.adminSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: AdminViewState) {
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
            is AdminViewState.SuccessDemoteAdmin -> {
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                setupAdapter()
                onRefresh()
            }
            else -> Unit
        }
    }

    private fun showList(memberListData: PagingData<MemberListData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, memberListData)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: MemberListData) {
//        TODO("Not yet implemented")
    }

    override fun onRemoveClicked(data: MemberListData) {
        RemoveConfirmationDialog.newInstance(
            object : RemoveConfirmationDialog.ConfirmationCallback {
                override fun onConfirm(id: String) {
                    viewModel.doDemoteAdmin(activity.groupDetails?.id!!,id.toInt())
                }
            },
            title = "Demote Selected Co-Admin?",
            groupId = data.id.toString()
        ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
    }

    override fun onRefresh() {
        viewModel.refresh(activity.groupDetails?.id.toString())
    }

}