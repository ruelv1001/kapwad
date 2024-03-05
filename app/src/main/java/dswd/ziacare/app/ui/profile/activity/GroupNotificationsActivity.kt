package dswd.ziacare.app.ui.profile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import dswd.ziacare.app.R
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.data.repositories.group.response.PendingGroupRequestData
import dswd.ziacare.app.data.repositories.profile.response.UserNotificationData
import dswd.ziacare.app.databinding.ActivityNotificationBinding
import dswd.ziacare.app.ui.profile.adapter.NotificationsAdapter
import dswd.ziacare.app.ui.profile.fragment.ProfileEditNumberFragmentDirections
import dswd.ziacare.app.ui.profile.viewmodel.ProfileViewModel
import dswd.ziacare.app.ui.profile.viewmodel.ProfileViewState
import dswd.ziacare.app.utils.dialog.CommonDialog
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupNotificationsActivity : AppCompatActivity(), NotificationsAdapter.NotificationsCallback,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityNotificationBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : NotificationsAdapter? = null
    private val viewModel: ProfileViewModel by viewModels()
    private var groupId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        groupId = intent.getStringExtra(GROUP_ID).toString()
        setOnClickListener()
        setUpAdapter()
        observeProfile()
    }

    private fun setUpAdapter() = binding.run {
        adapter = NotificationsAdapter(this@GroupNotificationsActivity)
        linearLayoutManager = LinearLayoutManager(this@GroupNotificationsActivity)
        swipeRefreshLayout.setOnRefreshListener(this@GroupNotificationsActivity)
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

    private fun observeProfile() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> binding.swipeRefreshLayout.isRefreshing = true
            is ProfileViewState.SuccessGetNotificationList -> {
                clearList()
                showData(viewState.pagingData)
            }

            is ProfileViewState.PopupError -> {
                binding.swipeRefreshLayout.isRefreshing = false
                showPopupError(
                    this@GroupNotificationsActivity,
                    supportFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> hideLoadingDialog()
        }
    }

    private fun showData(list: PagingData<UserNotificationData>) {
        binding.swipeRefreshLayout.isRefreshing = false
        adapter?.submitData(lifecycle, list)
    }

    private fun setOnClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    override fun onItemClicked(data: UserNotificationData) {

    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    override fun onRefresh() {
       viewModel.refreshGroupNotif(groupId)
    }

    private fun clearList() {
        adapter?.submitData(lifecycle, PagingData.empty())
    }

    companion object {
        private const val GROUP_ID = "GROUP_ID"
        fun getIntent(context: Context, groupId: String): Intent {
            val intent = Intent(context, GroupNotificationsActivity::class.java)
            intent.putExtra(GROUP_ID,groupId)
            return intent
        }
    }
}