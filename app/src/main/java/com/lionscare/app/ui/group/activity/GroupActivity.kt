package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
    private var memberCount: Int = 0

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
            START_ASSISTANCE -> navGraph.setStartDestination(R.id.navigation_group_assistance)
            START_MEMBERSHIP -> navGraph.setStartDestination(R.id.navigation_group_membership)
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

    fun getRolesView() : ImageView{
        return binding.rolesImageView
    }

    fun getFilterImageView() : ImageView{
        return binding.filterImageView
    }

    fun setTitlee(title: String) = binding.run {
        titleTextView.text = title
        when(title){
            getString(R.string.lbl_transactions) ->{
                rolesImageView.visibility = View.GONE
                searchImageView.visibility = View.VISIBLE
                filterImageView.visibility = View.GONE
            }
            getString(R.string.lbl_group_community_roles) ->{
                rolesImageView.visibility = View.VISIBLE
                searchImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
            }
            getString(R.string.lbl_assistance) -> {
                rolesImageView.visibility = View.GONE
                searchImageView.visibility = View.GONE
                filterImageView.visibility = View.VISIBLE
            }
            else -> {
                rolesImageView.visibility = View.GONE
                searchImageView.visibility = View.GONE
            }
        }
    }
    fun setAssistanceDetailsType(type: String) = binding.run {
        assistanceDetailsType = type
    }

    fun getAssistanceDetailsType(): String{
        return assistanceDetailsType
    }
    fun setMemberCount(type: Int) = binding.run {
        memberCount = type
    }

    fun getMemberCount(): Int{
        return memberCount
    }

    companion object {
        private const val INVALID_ID = -1
        private const val EXTRA_START = "EXTRA_START"
        private const val START_INVITE = "START_INVITE"
        private const val START_MANAGE = "START_MANAGE"
        private const val START_MEMBERSHIP = "START_MEMBERSHIP"
        private const val START_ASSISTANCE = "START_ASSISTANCE"
        fun getIntent(context: Context, start: String): Intent {
            val intent = Intent(context, GroupActivity::class.java)
            intent.putExtra(EXTRA_START, start)
            return intent
        }
    }
}