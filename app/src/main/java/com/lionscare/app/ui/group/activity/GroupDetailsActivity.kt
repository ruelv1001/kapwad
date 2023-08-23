package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.ActivityGroupDetailsBinding
import com.lionscare.app.ui.main.fragment.GroupsFragment
import com.lionscare.app.ui.notifications.adapter.NotificationsAdapter
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity(), NotificationsAdapter.NotificationsCallback {

    private lateinit var binding: ActivityGroupDetailsBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : NotificationsAdapter? = null
    private var dataList: List<SampleData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
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
        fun getIntent(context: Context): Intent {
            return Intent(context, GroupDetailsActivity::class.java)
        }
    }

    override fun onItemClicked(data: SampleData) {
//        TODO("Not yet implemented")
    }
}