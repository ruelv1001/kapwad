package com.lionscare.app.ui.bulletin.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.billing.response.BillData
import com.lionscare.app.databinding.ActivityAllRequestBulletinBinding
import com.lionscare.app.databinding.ActivityBillingBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.ui.bulletin.adapter.BillAdapter
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener

class AllRequestBulletinActivity : AppCompatActivity(), BillAdapter.OnClickCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: ActivityAllRequestBulletinBinding? = null
    private val binding get() = _binding!!
    private var adapter: BillAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAllRequestBulletinBinding.inflate(layoutInflater)
        setContentView( binding.root)
        setupAdapter()
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupAdapter() = binding.run {
        adapter = BillAdapter(this@AllRequestBulletinActivity, this@AllRequestBulletinActivity)
        swipeRefreshLayout.setOnRefreshListener(this@AllRequestBulletinActivity)
        linearLayoutManager = LinearLayoutManager(this@AllRequestBulletinActivity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        shimmerLayout.isVisible = true
        shimmerLayout.startShimmer()

        adapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    placeHolderTextView.isVisible = false
                    shimmerLayout.isVisible = true
                    recyclerView.isVisible = false
                }
                loadState.source.refresh is LoadState.Error -> {
                    placeHolderTextView.isVisible = true
                    shimmerLayout.isVisible = false
                    recyclerView.isVisible = false
                }
                loadState.source.refresh is LoadState.NotLoading && adapter?.hasData() == true -> {
                    placeHolderTextView.isVisible = false
                    shimmerLayout.isVisible = false
                    shimmerLayout.stopShimmer()
                    recyclerView.isVisible = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AllRequestBulletinActivity::class.java)
        }
    }

    override fun onItemClicked(data: BillData) {

    }

    override fun onRefresh() {
    }
}