package kapwad.reader.app.ui.main.fragment

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
import kapwad.reader.app.data.repositories.group.response.GroupListData
import kapwad.reader.app.databinding.FragmentGroupsYourGroupBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.main.viewmodel.GroupListViewModel
import kapwad.reader.app.ui.group.activity.GroupDetailsActivity
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.main.adapter.GroupsYourGroupAdapter
import kapwad.reader.app.ui.main.viewmodel.GroupListViewState
import kapwad.reader.app.ui.main.viewmodel.ImmediateFamilyViewModel
import kapwad.reader.app.ui.main.viewmodel.ImmediateFamilyViewState
import kapwad.reader.app.utils.loadGroupAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupsYourGroupFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentGroupsYourGroupBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var orgAdapter: GroupsYourGroupAdapter? = null
    private val viewModel: GroupListViewModel by viewModels()
    private val iFViewModel: ImmediateFamilyViewModel by viewModels()
    private var immediateFamilyId = ""
    private val activity by lazy { requireActivity() as MainActivity }

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
        setClickListeners()
        setupAdapter()
        observeGroupList()
        observeImmediateFamily()
    }

    private fun setupAdapter() = binding.run {
        orgAdapter = GroupsYourGroupAdapter(requireActivity(), this@GroupsYourGroupFragment)
        orgSwipeRefreshLayout.setOnRefreshListener(this@GroupsYourGroupFragment)
        linearLayoutManager = LinearLayoutManager(context)
        organizationRecyclerView.layoutManager = linearLayoutManager
        organizationRecyclerView.adapter = orgAdapter

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
                    activity.groupCount = orgAdapter?.itemCount ?: 0
                }
            }
        }
    }

    private fun observeGroupList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getGroupSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun observeImmediateFamily() {
        viewLifecycleOwner.lifecycleScope.launch {
            iFViewModel.immediateFamilySharedFlow.collectLatest { viewState ->
                iFHandleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupListViewState) {
        when (viewState) {
            is GroupListViewState.Loading -> binding.orgSwipeRefreshLayout.isRefreshing = true
            is GroupListViewState.Success -> {
                showGroup(viewState.pagingData)
            }
            is GroupListViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> Unit
        }
    }

    private fun iFHandleViewState(viewState: ImmediateFamilyViewState) {
        when (viewState) {
            ImmediateFamilyViewState.Loading -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isVisible = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }
            is ImmediateFamilyViewState.PopupError -> {
                binding.createGroupButton.isVisible = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }

            is ImmediateFamilyViewState.Success -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true

                viewState.immediateFamilyResponse?.data?.let { setImmediateFamily(it) }
                immediateFamilyId = viewState.immediateFamilyResponse?.data?.id.toString()
            }
        }
    }

    private fun setImmediateFamily(data: GroupListData) = binding.run{
        immediateFamilyLayout.imageView.loadGroupAvatar(data.avatar?.thumb_path)
        immediateFamilyLayout.titleTextView.text = data.name
        immediateFamilyLayout.membersTextView.text = if ((data.member_count ?: 0) > 1) {
            "${data.member_count} members"
        } else {
            "${data.member_count} member"
        }
        immediateFamilyLayout.referenceTextView.text = data.qrcode
    }

    private fun showGroup(groupListData: PagingData<GroupListData>) {
        binding.orgSwipeRefreshLayout.isRefreshing = false
        orgAdapter?.submitData(viewLifecycleOwner.lifecycle, groupListData)
    }

    private fun setClickListeners() = binding.run {
        immediateFamilyLayout.adapterLinearLayout.setOnSingleClickListener {
            val intent = GroupDetailsActivity.getIntent(requireActivity(), immediateFamilyId)
            startActivity(intent)
        }
        createGroupButton.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(requireActivity(),
                START_CREATE_FAMILY
            )
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        orgAdapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

    companion object {
        private const val START_CREATE_FAMILY = "START_CREATE_FAMILY"
        fun newInstance() = GroupsYourGroupFragment()
    }

    override fun onRefresh() {
        binding.orgSwipeRefreshLayout.isRefreshing = true
        viewModel.refreshAll()
        iFViewModel.getImmediateFamily()
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onItemClicked(data: GroupListData) {
        val intent = GroupDetailsActivity.getIntent(requireActivity(), data.id)
        startActivity(intent)
    }

}