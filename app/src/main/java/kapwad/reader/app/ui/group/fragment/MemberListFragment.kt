package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.data.repositories.member.response.MemberListData
import kapwad.reader.app.data.repositories.wallet.response.QRData
import kapwad.reader.app.databinding.FragmentGroupMembershipReqBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.adapter.GroupMembersAdapter
import kapwad.reader.app.ui.group.dialog.MemberDetailsDialog
import kapwad.reader.app.ui.group.dialog.RemoveConfirmationDialog
import kapwad.reader.app.ui.group.viewmodel.AdminViewModel
import kapwad.reader.app.ui.group.viewmodel.AdminViewState
import kapwad.reader.app.ui.group.viewmodel.MemberViewModel
import kapwad.reader.app.ui.group.viewmodel.MemberViewState
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MemberListFragment : Fragment(),
    GroupMembersAdapter.MembersCallback, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentGroupMembershipReqBinding? = null
    private val binding get() = _binding!!
    private var adapter: GroupMembersAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: MemberViewModel by viewModels()
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModelAdmin: AdminViewModel by viewModels()
    private var isOwner = false

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
        observeMemberList()
        viewModel.ownerInfo = activity.groupDetails?.owner?: UserModel()
        viewModel.refreshListOfMembers(activity.groupDetails?.id.toString(), true)
       // isOwner = activity.groupDetails?.owner_user_id == viewModel.user.id
    }

    private fun setupAdapter() = binding.run {

    }

    private fun setClickListeners() = binding.run {

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
            is AdminViewState.SuccessRemoveMember -> {
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                setupAdapter()
                onRefresh()
            }

            is AdminViewState.SuccessTransferOwnership ->{
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                setupAdapter()
                requireActivity().onBackPressedDispatcher.onBackPressed()
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
    private fun showList(memberListData: PagingData<MemberListData>){
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(viewLifecycleOwner.lifecycle, memberListData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: MemberListData) {
        callMemberDetailDialog(data)
    }

    private fun callMemberDetailDialog(data: MemberListData) {
        MemberDetailsDialog.newInstance(
            object : MemberDetailsDialog.MembershipCallback {
                override fun onRemoveMember(memberListData: MemberListData) {
                    callRemoveMemberDialog(memberListData.id ?: 0)
                }

                override fun onSendPoint(memberListData: MemberListData) {

                }

                override fun onTransferOwnership(memberListData: MemberListData) {
                    viewModelAdmin.doTransferOwnership(activity.groupDetails?.id.toString(),data.user?.id.toString())
                }
            }, data, activity.groupDetails?.is_admin == true, isOwner, activity.groupDetails?.owner_user_id.toString()
        ).show(childFragmentManager, MemberDetailsDialog.TAG)
    }

    private fun callRemoveMemberDialog(id: Int) {
        RemoveConfirmationDialog.newInstance(
            object : RemoveConfirmationDialog.ConfirmationCallback {
                override fun onConfirm(id: String) {
                    viewModelAdmin.doRemoveMember(activity.groupDetails?.id.orEmpty(), id.toInt())
                }
            },
            title = "Remove Selected Member?",
            groupId = id.toString()
        ).show(childFragmentManager, RemoveConfirmationDialog.TAG)
    }

    override fun onRefresh() {
        viewModel.refreshListOfMembers(activity.groupDetails?.id.toString(), true)
    }

    companion object {
        private const val REQUEST = "REQUEST"
        fun newInstance(): MemberListFragment {
            return MemberListFragment()
        }
    }

}