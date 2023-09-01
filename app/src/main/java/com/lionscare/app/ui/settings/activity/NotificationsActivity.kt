package com.lionscare.app.ui.settings.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.ActivityNotificationBinding
import com.lionscare.app.ui.notifications.adapter.NotificationsAdapter
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsActivity : AppCompatActivity(), NotificationsAdapter.NotificationsCallback {

    private lateinit var binding: ActivityNotificationBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : NotificationsAdapter? = null
    private var searchView: SearchView? = null
    private var dataList: List<SampleData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setOnClickListener()
        setUpAdapter()
    }

    private fun setUpAdapter() = binding.run {
        adapter = NotificationsAdapter(this@NotificationsActivity)
        linearLayoutManager = LinearLayoutManager(this@NotificationsActivity)
        historyRecyclerView.layoutManager = linearLayoutManager
        historyRecyclerView.adapter = adapter

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

    private fun setOnClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressed()
        }
    }

    fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun setTitlee(title: String) = binding.run {
        titleTextView.text = title
    }

    companion object {
        private const val INVALID_ID = -1
        private const val EXTRA_START = "EXTRA_START"
        fun getIntent(context: Context): Intent {
            return Intent(context, NotificationsActivity::class.java)
        }
    }

    override fun onItemClicked(data: SampleData) {
//        TODO("Not yet implemented")
    }
}