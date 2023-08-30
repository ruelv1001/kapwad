package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.ActivityGroupDetailsBinding
import com.lionscare.app.ui.group.viewmodel.GroupViewModel
import com.lionscare.app.ui.group.viewmodel.GroupViewState
import com.lionscare.app.ui.notifications.adapter.NotificationsAdapter
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity(), NotificationsAdapter.NotificationsCallback {

    private lateinit var binding: ActivityGroupDetailsBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : NotificationsAdapter? = null
    private var dataList: List<SampleData> = emptyList()
    private var groupId = 0
    private val viewModel: GroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        observeShowGroup()
        groupId = intent.getIntExtra(GROUP_ID, 0)
        viewModel.showGroup(groupId)
        setupClickListener()
        setUpAdapter()
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
        titleTextView.text = data.group_name
    }

    private fun setupClickListener() = binding.run{
        addImageView.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_INVITE)
            startActivity(intent)
        }
        settingsImageView.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_MANAGE)
            startActivity(intent)
        }
        assistanceRequestsLinearLayout.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_ASSISTANCE)
            startActivity(intent)
        }
        membersLinearLayout.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(this@GroupDetailsActivity, START_MEMBERSHIP)
            startActivity(intent)
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
                viewState.createGroupResponse?.data?.let { setDetails(it) }
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

    companion object {
        private const val START_INVITE = "START_INVITE"
        private const val START_MANAGE = "START_MANAGE"
        private const val START_ASSISTANCE = "START_ASSISTANCE"
        private const val START_MEMBERSHIP = "START_MEMBERSHIP"
        private const val GROUP_ID = "GROUP_ID"
        fun getIntent(context: Context, group_id: Int? = 0): Intent {
            val intent = Intent(context, GroupDetailsActivity::class.java)
            intent.putExtra(GROUP_ID,group_id)
            return intent
        }
    }

    override fun onItemClicked(data: SampleData) {
//        TODO("Not yet implemented")
    }
}