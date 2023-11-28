package com.ziacare.app.ui.group.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import com.ziacare.app.R
import com.ziacare.app.data.model.SampleData
import com.ziacare.app.data.repositories.assistance.response.CreateAssistanceData
import com.ziacare.app.data.repositories.group.response.CreateGroupResponse
import com.ziacare.app.data.repositories.group.response.GroupData
import com.ziacare.app.databinding.ActivityGroupDetailsBinding
import com.ziacare.app.ui.group.adapter.AssistanceAdapter
import com.ziacare.app.ui.group.viewmodel.AssistanceViewModel
import com.ziacare.app.ui.group.viewmodel.AssistanceViewState
import com.ziacare.app.ui.group.viewmodel.GroupViewModel
import com.ziacare.app.ui.group.viewmodel.GroupViewState
import com.ziacare.app.ui.group.viewmodel.GroupWalletViewModel
import com.ziacare.app.ui.group.viewmodel.GroupWalletViewState
import com.ziacare.app.ui.profile.adapter.NotificationsAdapter
import com.ziacare.app.ui.wallet.activity.WalletActivity
import com.ziacare.app.ui.wallet.viewmodel.WalletViewState
import com.ziacare.app.utils.copyToClipboard
import com.ziacare.app.utils.dialog.CommonDialog
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.setQR
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    AssistanceAdapter.GroupCallback {

    private lateinit var binding: ActivityGroupDetailsBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AssistanceAdapter? = null
    private var groupId = ""
    private val viewModel: GroupViewModel by viewModels()
    private val walletViewModel: GroupWalletViewModel by viewModels()
    private val assistanceViewModel: AssistanceViewModel by viewModels()
    private var groupDetails: GroupData? = null
    var frontAnim: AnimatorSet? = null
    var backAnim: AnimatorSet? = null

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
        observeAssistance()
        setUpAnimation()
    }

    private fun setUpAdapter() = binding.run {
        adapter = AssistanceAdapter(this@GroupDetailsActivity, this@GroupDetailsActivity)
        linearLayoutManager = LinearLayoutManager(this@GroupDetailsActivity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

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

    private fun setDetails(data: GroupData) = binding.run {
        titleTextView.text = data.name
        referenceTextView.text = data.qrcode
        membersTextView.text = data.member_count.toString()
       // adminTextView.text = data.admin_only_count.toString()
        assistanceTextView.text = data.request_assistance_count.toString()
        qrLayout.qrImageView.setImageBitmap(setQR(this@GroupDetailsActivity, data.qrcode_value))
        qrLayout.idNoTextView.text = data.code
        walletLayout.sendLinearLayout.isVisible = data.is_admin == true
        addImageView.isVisible = data.is_admin == true
    }

    private fun setupClickListener() = binding.run {
        addImageView.setOnSingleClickListener {
            val intent =
                GroupActivity.getIntent(this@GroupDetailsActivity, START_INVITE, groupDetails)
            startActivity(intent)
        }
        settingsImageView.setOnSingleClickListener {
            val intent =
                GroupActivity.getIntent(this@GroupDetailsActivity, START_MANAGE, groupDetails)
            startActivity(intent)
        }
        assistanceRequestsLinearLayout.setOnSingleClickListener {
            val intent =
                GroupActivity.getIntent(this@GroupDetailsActivity, START_ASSISTANCE, groupDetails)
            startActivity(intent)
        }
        membersLinearLayout.setOnSingleClickListener {
            val intent =
                GroupActivity.getIntent(this@GroupDetailsActivity, START_MEMBERSHIP, groupDetails)
            startActivity(intent)
        }
        adminLinearLayout.setOnSingleClickListener {
           val intent =
                GroupActivity.getIntent(this@GroupDetailsActivity, START_ADMIN, groupDetails)
            startActivity(intent)
        }
        backImageView.setOnSingleClickListener {
            finish()
        }
        walletLayout.sendLinearLayout.setOnSingleClickListener {
            if(assistanceViewModel.getUserKYC() != "completed"){
                toastWarning(getString(R.string.kyc_status_must_be_verified))
            }else{
                val intent = WalletActivity.getIntent(
                    this@GroupDetailsActivity,
                    "Send Points",
                    true,
                    groupId
                )
                startActivity(intent)
            }

        }
        walletLayout.qrImageView.setOnSingleClickListener {
            frontAnim?.setTarget(walletLayout.walletCardView)
            backAnim?.setTarget(qrLayout.qrCardView)
            frontAnim?.start()
            backAnim?.start()
        }
        qrLayout.walletImageView.setOnSingleClickListener {
            frontAnim?.setTarget(qrLayout.qrCardView)
            backAnim?.setTarget(walletLayout.walletCardView)
            backAnim?.start()
            frontAnim?.start()
        }
        qrLayout.idNoTextView.setOnSingleClickListener {
            copyToClipboard(qrLayout.idNoTextView.text.toString())
        }

        viewAllTextView.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(
                this@GroupDetailsActivity,
                start = START_ASSISTANCE,
                groupData = groupDetails,
                assistanceType = ALL_REQUEST
            )
            startActivity(intent)
        }
    }

    private fun setUpAnimation() {
        frontAnim = AnimatorInflater.loadAnimator(
            this,
            R.animator.front_animator
        ) as AnimatorSet
        backAnim =
            AnimatorInflater.loadAnimator(this, R.animator.back_animator) as AnimatorSet

        val scale = resources.displayMetrics.density * 8000
        binding.walletLayout.walletCardView.cameraDistance = scale
        binding.qrLayout.qrCardView.cameraDistance = scale
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
        lifecycleScope.launch {
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
                binding.walletLayout.pointsTextView.text = viewState.balanceData?.value
            }

            is GroupWalletViewState.PopupError -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showPopupError(this, supportFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun observeAssistance() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                assistanceViewModel.assistanceSharedFlow.collect { viewState ->
                    assistanceHandleViewState(viewState)
                }
            }
        }
    }

    private fun assistanceHandleViewState(viewState: AssistanceViewState) {
        when (viewState) {
            is AssistanceViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is AssistanceViewState.SuccessGetAllListOfAssistance -> {
                binding.swipeRefreshLayout.isRefreshing = false
                clearList()
                showList(viewState.pagingData)
            }

            is AssistanceViewState.PopupError -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showPopupError(
                    this@GroupDetailsActivity,
                    supportFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> Unit
        }
    }

    private fun showList(createAssistanceData: PagingData<CreateAssistanceData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(lifecycle, createAssistanceData)
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
        adapter?.removeLoadStateListener { this@GroupDetailsActivity }
        super.onDestroy()
        hideLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        viewModel.showGroup(groupId)
        assistanceViewModel.refresh(groupId, emptyList(), true)
    }

    override fun onRefresh() {
        viewModel.showGroup(groupId)
    }

    private fun clearList() {
        adapter?.submitData(lifecycle, PagingData.empty())
    }

    override fun onItemClicked(data: CreateAssistanceData) {
        if(groupDetails?.is_admin == true || data.user?.id == assistanceViewModel.user.id){
            val intent = GroupActivity.getIntent(
                this@GroupDetailsActivity,
                start = START_ASSISTANCE_DETAILS,
                groupData = groupDetails,
                referenceId = data.reference_id.toString()
            )
            startActivity(intent)
        }
    }

    companion object {
        private const val START_INVITE = "START_INVITE"
        private const val START_MANAGE = "START_MANAGE"
        private const val START_ASSISTANCE = "START_ASSISTANCE"
        private const val START_MEMBERSHIP = "START_MEMBERSHIP"
        private const val START_ADMIN = "START_ADMIN"
        private const val START_ASSISTANCE_DETAILS = "START_ASSISTANCE_DETAILS"
                private const val GROUP_ID = "GROUP_ID"
        private const val GROUP_NAME = "GROUP_NAME"
        private const val ALL_REQUEST = "ALL_REQUEST"
        fun getIntent(context: Context, group_id: String? = null): Intent {
            val intent = Intent(context, GroupDetailsActivity::class.java)
            intent.putExtra(GROUP_ID, group_id)
            return intent
        }
    }


}