package com.lionscare.app.ui.billing.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import com.lionscare.app.R
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.databinding.FragmentGroupsYourGroupBinding
import com.lionscare.app.ui.billing.dialog.RemoveFromDonationRequestDialog
import com.lionscare.app.ui.billing.viewmodel.BillingViewModel
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.ui.billing.viewstate.CustomGroupListDataModel
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter
import com.lionscare.app.ui.profile.dialog.ProfileConfirmationDialog
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.loadGroupAvatar
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AskForDonationsGroupRequestFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback , RemoveFromDonationRequestDialog.RemoveFromDonationRequestCallback{
    private var _binding: FragmentGroupsYourGroupBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var orgAdapter: GroupsYourGroupAdapter? = null

    private val viewModel: BillingViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupsYourGroupBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlow()
        setOnClickListeners()
        setupAdapter()
        setContentViews()

    }

    override fun onResume() {
        super.onResume()
        //===================== groups
        when(viewModel.currentFragmentRoute ){
            "request_add" -> {
                if (viewModel.groupsRequestsData != null && viewModel.groupsRequestsData.orEmpty().isNotEmpty()
                ) {
                    //put the cached data from viewmodel back to adapter
                    // along with other list but show currentyl checked
                    orgAdapter?.setCustomData(viewModel.groupsRequestsData!!)
                    viewModel.loadGroups()
                }else{
                    viewModel.loadGroups()
                    binding.orgPlaceHolderTextView.isVisible = true
                    binding.orgShimmerLayout.isVisible = false
                    binding.organizationRecyclerView.isVisible = false
                }
            }
            "request_list" -> {
                if (viewModel.groupsRequestsData != null && viewModel.groupsRequestsData.orEmpty().isNotEmpty()) {
                    //put the cached data from viewmodel back to adapter
                    val data = viewModel.groupsRequestsData?.map {
                        it.groupData
                    }?.toMutableList()
                    orgAdapter?.setCustomData(viewModel.groupsRequestsData.orEmpty().toMutableList())
                    val pagingData: PagingData<GroupListData> = PagingData.from(data.orEmpty())
                    showGroup(pagingData)

                }else{
                    binding.orgPlaceHolderTextView.isVisible = true
                    binding.orgShimmerLayout.isVisible = false
                    binding.organizationRecyclerView.isVisible = false
                }
            }
        }
        //============= family
        if (viewModel.immediateFamilyData != null) {
            binding.createGroupButton.isGone = true
            binding.famShimmerLayout.isGone = true
            binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true
            setImmediateFamily(viewModel.immediateFamilyData!!)
            binding.immediateFamilyLayout.checkBox.isChecked = true
        } else {
            //if request add, and not show list of request, then do api
            if (viewModel.currentFragmentRoute == "request_add") {
                viewModel.getImmediateFamily()
            } else {
                binding.createGroupButton.isVisible = true
                binding.createGroupButton.text = "No Family Selected"
                binding.createGroupButton.isEnabled = false
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }
        }
    }

    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            is BillingViewState.LoadingGroups -> binding.orgSwipeRefreshLayout.isRefreshing = true
            is BillingViewState.SuccessLoadGroup -> {
                showGroup(viewState.pagingData)
            }

            is BillingViewState.PopupError -> {
                when (viewState.endpoint) {
                    "family" -> {
                        binding.createGroupButton.isVisible = true
                        binding.createGroupButton.text = "No Family Found"
                        binding.createGroupButton.isEnabled = false
                        binding.famShimmerLayout.isGone = true
                        binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
                    }

                    else -> {
                        showPopupError(
                            requireActivity(),
                            childFragmentManager,
                            viewState.errorCode,
                            viewState.message
                        )
                    }
                }
            }

            BillingViewState.LoadingFamily -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isVisible = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }

            is BillingViewState.SuccessLoadFamily -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true

                viewState.immediateFamilyResponse?.data?.let { setImmediateFamily(it) }
            }

            else -> Unit
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setContentViews() = binding.run {
        sendRequestButton.isVisible = true
        if (viewModel.shouldShowDonationRequestsViews) {
            noteSendRequestText.isVisible = true
            searchLinearLayout.isVisible = true
        } else {
            noteSendRequestText.isVisible = false
            searchLinearLayout.isVisible = false
            sendRequestButton.text = getString(R.string.remove)
            sendRequestButton.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.red)
        }

        noteSendRequestText.text =
            "Note: This Billing ${viewModel.billingStatementNumber} " +
                    "will be posted in the Request for Donations section " +
                    "of your selected groups"
    }

    private fun setOnClickListeners() = binding.run {
        sendRequestButton.setOnSingleClickListener {
            when(viewModel.currentFragmentRoute ){
                "request_add" -> {
                    //get groups data where it is checked
                    val groupsWithChecked: MutableList<CustomGroupListDataModel>? =
                        orgAdapter?.getCustomData()
                            ?.filter { it.isChecked }?.toMutableList()

                    //remvoe imediatefamily if not checked
                    if (!binding.immediateFamilyLayout.checkBox.isChecked) {
                        viewModel.immediateFamilyData = null
                    }
                    //store to viewModel
                    viewModel.groupsRequestsData = groupsWithChecked
                    findNavController().popBackStack()
                }
                "request_list" -> {

                    RemoveFromDonationRequestDialog.newInstance(this@AskForDonationsGroupRequestFragment)
                        .show(childFragmentManager, RegisterSuccessDialog.TAG)
                }
            }

        }
    }

    override fun onItemClicked(data: GroupListData) {
        //TODO
    }

    override fun onRemoveFromRequest(dialog: RemoveFromDonationRequestDialog) {
        dialog.dismiss()
        //filter those without Chcked
        val groupsWithoutChecked: MutableList<CustomGroupListDataModel>? =
            orgAdapter?.getCustomData()
                ?.filter { !it.isChecked }?.toMutableList()

        //Get only the groupData to be sent to paging
        val data = groupsWithoutChecked?.map {
            it.groupData
        }?.toMutableList()

        //make changes to the groupsWithoutChecked and make a certain value back to true
        viewModel.groupsRequestsData = groupsWithoutChecked?.map {
            it.copy(isChecked = true)
        }?.toMutableList()

        val pagingData: PagingData<GroupListData> = PagingData.from(data.orEmpty())
        showGroup(pagingData)
    }

    private fun setupAdapter() = binding.run {
        orgAdapter = GroupsYourGroupAdapter(
            requireActivity(),
            this@AskForDonationsGroupRequestFragment,
            shouldShowDonationRequestsViews = viewModel.shouldShowDonationRequestsViews,
        )
        orgSwipeRefreshLayout.setOnRefreshListener { orgSwipeRefreshLayout.isRefreshing = false }
        linearLayoutManager = LinearLayoutManager(context)
        organizationRecyclerView.layoutManager = linearLayoutManager
        organizationRecyclerView.adapter = orgAdapter

        binding.run {
            orgAdapter?.addLoadStateListener { loadState ->
                when {
                    loadState.source.refresh is LoadState.Loading -> {
                        orgPlaceHolderTextView.isVisible = false
                        orgShimmerLayout.isVisible = true
                        organizationRecyclerView.isVisible = false
                    }

                    loadState.source.refresh is LoadState.Error -> {
                        orgPlaceHolderTextView.isVisible = true
                        orgShimmerLayout.isVisible = false
                        organizationRecyclerView.isVisible = false
                    }

                    loadState.source.refresh is LoadState.NotLoading && orgAdapter?.hasData() == true -> {
                        orgPlaceHolderTextView.isVisible = false
                        orgShimmerLayout.isVisible = false
                        orgShimmerLayout.stopShimmer()
                        organizationRecyclerView.isVisible = true
                    }
                }
            }
        }
    }


    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.billingSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }


    private fun setImmediateFamily(data: GroupListData) = binding.run {
        immediateFamilyLayout.imageView.loadGroupAvatar(data.avatar?.thumb_path)
        immediateFamilyLayout.titleTextView.text = data.name
        immediateFamilyLayout.checkBox.isVisible = true
        immediateFamilyLayout.membersTextView.text = if ((data.member_count ?: 0) > 1) {
            "${data.member_count} members"
        } else {
            "${data.member_count} member"
        }
        immediateFamilyLayout.referenceTextView.text = data.qrcode
        immediateFamilyLayout.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.immediateFamilyData = data
            } else {
                viewModel.immediateFamilyData = null
            }
        }
    }

    private fun showGroup(groupListData: PagingData<GroupListData>) {
        binding.orgSwipeRefreshLayout.isRefreshing = false
        orgAdapter?.submitData(viewLifecycleOwner.lifecycle, groupListData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.sendRequestButton.isVisible = false
        binding.noteSendRequestText.isVisible = false
        binding.searchLinearLayout.isVisible = false
        orgAdapter?.removeLoadStateListener { requireContext() }
        _binding = null
    }


}