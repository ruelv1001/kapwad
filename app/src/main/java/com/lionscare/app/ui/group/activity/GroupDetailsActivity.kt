package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.group.response.CreateGroupResponse
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.ActivityGroupDetailsBinding
import com.lionscare.app.ui.group.viewmodel.GroupViewModel
import com.lionscare.app.ui.group.viewmodel.GroupViewState
import com.lionscare.app.ui.group.viewmodel.GroupWalletViewModel
import com.lionscare.app.ui.group.viewmodel.GroupWalletViewState
import com.lionscare.app.ui.notifications.adapter.NotificationsAdapter
import com.lionscare.app.ui.wallet.viewmodel.WalletViewState
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity(),
    NotificationsAdapter.NotificationsCallback, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityGroupDetailsBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : NotificationsAdapter? = null
    private var dataList: List<SampleData> = emptyList()
    private var groupId = ""
    private val viewModel: GroupViewModel by viewModels()
    private val walletViewModel: GroupWalletViewModel by viewModels()
    private var groupDetails: GroupData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        groupId = intent.getStringExtra(GROUP_ID).toString()
        setupClickListener()
        setUpAdapter()
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        observeWallet()
        observeShowGroup()
    }

    private fun setUpAdapter() = binding.run {
        adapter = NotificationsAdapter(this@GroupDetailsActivity)
        linearLayoutManager = LinearLayoutManager(this@GroupDetailsActivity)
        activityRecyclerView.layoutManager = linearLayoutManager
        activityRecyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = 1,
                title = "You received a badge",
                remarks = "KYC process completed",
                date = "2023-07-04 3:59 PM"
            ),
            SampleData(
                id = 2,
                title = "You Joined a Group",
                remarks = "You joined Malasakit Family",
                date = "2023-07-04 3:59 PM"
            )
        )
        adapter?.submitData(lifecycle, PagingData.from(dataList))
    }

    private fun setDetails(data : GroupData) = binding.run {
        titleTextView.text = data.name
    }

    private fun setupClickListener() = binding.run{
        addImageView.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_INVITE, groupDetails)
            startActivity(intent)
        }
        settingsImageView.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_MANAGE, groupDetails)
            startActivity(intent)
        }
        assistanceRequestsLinearLayout.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_ASSISTANCE)
            startActivity(intent)
        }
        membersLinearLayout.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_MEMBERSHIP, groupDetails)
            startActivity(intent)
        }
        adminLinearLayout.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_ADMIN, groupDetails)
            startActivity(intent)
        }
        backImageView.setOnSingleClickListener {
          finish()
        }
    }

    private fun observeShowGroup() {
        lifecycleScope.launchWhenStarted {
            viewModel.groupSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupViewState) {
        when (viewState) {
            is GroupViewState.InputError -> Unit
            is GroupViewState.Loading -> showLoadingDialog(R.string.loading)
            is GroupViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    this@GroupDetailsActivity,
                    supportFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is GroupViewState.SuccessShowGroup -> {
                hideLoadingDialog()
                groupDetails = viewState.createGroupResponse?.data
                groupDetails?.let { setDetails(it) }
                walletViewModel.getWalletBalance(viewState.createGroupResponse?.data?.id.orEmpty())
            }
            else -> Unit
        }
    }

    private fun observeWallet() {
        lifecycleScope.launch{
            walletViewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: GroupWalletViewState) {
        when (viewState) {
            is GroupWalletViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is GroupWalletViewState.SuccessGetBalance -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.pointsTextView.text = viewState.balanceData?.value
            }
            is GroupWalletViewState.PopupError -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showPopupError(this, supportFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        viewModel.showGroup(groupId)
    }

    companion object {
        private const val START_INVITE = "START_INVITE"
        private const val START_MANAGE = "START_MANAGE"
        private const val START_ASSISTANCE = "START_ASSISTANCE"
        private const val START_MEMBERSHIP = "START_MEMBERSHIP"
        private const val START_ADMIN = "START_ADMIN"
        private const val GROUP_ID = "GROUP_ID"
        private const val GROUP_NAME = "GROUP_NAME"
        fun getIntent(context: Context, group_id: String? = null): Intent {
            val intent = Intent(context, GroupDetailsActivity::class.java)
            intent.putExtra(GROUP_ID,group_id)
            return intent
        }
    }

    override fun onItemClicked(data: SampleData) {

    }

    override fun onRefresh() {
        viewModel.showGroup(groupId)
    }
}