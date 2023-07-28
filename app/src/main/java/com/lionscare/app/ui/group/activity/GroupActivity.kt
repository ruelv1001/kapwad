package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityGroupBinding
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding
    private var loadingDialog: CommonDialog? = null
    var start: String = ""
    private var assistanceDetailsType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        setOnClickListener()
    }

    private fun setOnClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressed()
        }
    }

    private fun setupNavigationComponent() {
        start = intent.getStringExtra(EXTRA_START).orEmpty()
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.group_nav_graph)
        when (start) {
            START_INVITE -> navGraph.setStartDestination(R.id.navigation_group_invite)
            START_MANAGE -> navGraph.setStartDestination(R.id.navigation_group_manage)
            else -> navGraph.setStartDestination(R.id.navigation_group_create)
        }
        navController.setGraph(navGraph, null)
        val appBarConfig = AppBarConfiguration.Builder(INVALID_ID)
            .setFallbackOnNavigateUpListener {
                onBackPressed()
                true
            }.build()
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfig)
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
    fun setAssistanceDetailsType(type: String) = binding.run {
        assistanceDetailsType = type
    }

    fun getAssistanceDetailsType(): String{
        return assistanceDetailsType
    }

    companion object {
        private const val INVALID_ID = -1
        private const val EXTRA_START = "EXTRA_START"
        private const val START_INVITE = "START_INVITE"
        private const val START_MANAGE = "START_MANAGE"
        fun getIntent(context: Context, start: String): Intent {
            val intent = Intent(context, GroupActivity::class.java)
            intent.putExtra(EXTRA_START, start)
            return intent
        }
    }
}