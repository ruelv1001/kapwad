package da.farmer.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import da.farmer.app.R
import da.farmer.app.data.repositories.assistance.response.CreateAssistanceData
import da.farmer.app.data.repositories.group.response.CreateGroupResponse
import da.farmer.app.data.repositories.group.response.GroupData
import da.farmer.app.databinding.ActivityGroupBinding
import da.farmer.app.utils.dialog.CommonDialog
import da.farmer.app.utils.getParcelable
import da.farmer.app.utils.getSerializable
import da.farmer.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding
    private var loadingDialog: CommonDialog? = null
    var start: String = ""
    private var assistanceDetailsType = ""
    private var memberCount: Int = 0
    var groupDetails: GroupData? = null
    var referenceId = ""
    var groupCount = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        setOnClickListener()
        groupDetails = intent.getParcelable(GROUP_DATA)
        assistanceDetailsType = intent.getStringExtra(ASSISTANCE_TYPE).toString()
        referenceId = intent.getStringExtra(EXTRA_REF_ID).toString()
        groupCount = intent.getIntExtra(GROUP_COUNT, -1)
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
            START_ASSISTANCE -> {
                navGraph.setStartDestination(R.id.navigation_group_assistance)
                if(assistanceDetailsType == ALL_REQUEST){
                    setAssistanceDetailsType(ALL_REQUEST)
                }else{
                    setAssistanceDetailsType(REQUEST)
                }
            }
            START_MEMBERSHIP -> navGraph.setStartDestination(R.id.navigation_group_membership)
            START_ADMIN -> navGraph.setStartDestination(R.id.navigation_group_admin_list)
            START_GROUP_SEARCH -> navGraph.setStartDestination(R.id.navigation_group_search)
            START_ASSISTANCE_DETAILS -> navGraph.setStartDestination(R.id.navigation_group_assistance_req_details)
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

    fun getRolesView(): ImageView {
        return binding.rolesImageView
    }

    fun getFilterImageView(): ImageView {
        return binding.filterImageView
    }

    fun getScanImageView(): ImageView {
        return binding.scanImageView
    }

    fun setTitlee(title: String) = binding.run {
        titleTextView.text = title
        when (title) {

            /*getString(R.string.lbl_group_community_roles) ->{
                rolesImageView.visibility = View.VISIBLE
                searchImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
                scanImageView.visibility = View.GONE
            }*/
            getString(R.string.lbl_assistance) -> {
                rolesImageView.visibility = View.GONE
                filterImageView.visibility = View.VISIBLE
                scanImageView.visibility = View.GONE
            }

            "Invite to ${groupDetails?.name}" -> {
                rolesImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
                scanImageView.visibility = View.VISIBLE
            }

            "Group Search" -> {
                rolesImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
                scanImageView.visibility = View.VISIBLE
            }

            else -> {
                rolesImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
                scanImageView.visibility = View.GONE
            }
        }
    }

    fun setAssistanceDetailsType(type: String) = binding.run {
        assistanceDetailsType = type
    }

    fun getAssistanceDetailsType(): String {
        return assistanceDetailsType
    }

    fun setMemberCount(type: Int) = binding.run {
        memberCount = type
    }

    fun getMemberCount(): Int {
        return memberCount
    }

    companion object {
        private const val INVALID_ID = -1
        private const val EXTRA_START = "EXTRA_START"
        private const val START_INVITE = "START_INVITE"
        private const val START_MANAGE = "START_MANAGE"
        private const val START_MEMBERSHIP = "START_MEMBERSHIP"
        private const val START_ASSISTANCE = "START_ASSISTANCE"
        private const val START_ADMIN = "START_ADMIN"
        private const val START_GROUP_SEARCH = "START_GROUP_SEARCH"
        private const val START_ASSISTANCE_DETAILS = "START_ASSISTANCE_DETAILS"
        private const val GROUP_DATA = "GROUP_DATA"
        private const val ASSISTANCE_TYPE = "ASSISTANCE_TYPE"
        private const val ALL_REQUEST = "ALL_REQUEST"
        private const val REQUEST = "REQUEST"
        private const val EXTRA_REF_ID = "EXTRA_REF_ID"
        private const val GROUP_COUNT = "GROUP_COUNT"
        fun getIntent(
            context: Context,
            start: String,
            groupData: GroupData? = null,
            assistanceType: String? = null,
            referenceId: String? = null,
            groupCount : Int ? = -1
        ): Intent {
            val intent = Intent(context, GroupActivity::class.java)
            intent.putExtra(GROUP_DATA, groupData)
            intent.putExtra(EXTRA_START, start)
            intent.putExtra(ASSISTANCE_TYPE, assistanceType)
            intent.putExtra(EXTRA_REF_ID,referenceId)
            intent.putExtra(GROUP_COUNT, groupCount)
            return intent
        }
    }
}