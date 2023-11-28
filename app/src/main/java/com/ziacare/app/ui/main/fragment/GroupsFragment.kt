package com.ziacare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import com.ziacare.app.R
import com.ziacare.app.databinding.FragmentGroupsBinding
import com.ziacare.app.ui.group.activity.GroupActivity
import com.ziacare.app.ui.group.viewmodel.GroupViewState
import com.ziacare.app.ui.main.activity.MainActivity
import com.ziacare.app.ui.main.viewmodel.GroupsViewModel
import com.ziacare.app.utils.adapter.CustomViewPagerAdapter
import com.ziacare.app.utils.setOnSingleClickListener
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupsFragment : Fragment(){

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    private var menuItem: MenuItem ?= null
    private val viewModel : GroupsViewModel by viewModels()

    private val activity by lazy { requireActivity() as MainActivity }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupsBinding.inflate(
            inflater,
            container,
            false
        )

        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabs()
        setClickListeners()
        observeGroup()
    }

    override fun onResume() {
        super.onResume()
        viewModel.doGetGroupListCount()
    }


    private fun observeGroup() {
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.groupSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }

        }
    }

    private fun handleViewState(viewState: GroupViewState) {
        when (viewState) {
            is GroupViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is GroupViewState.SuccessPendingGroupListCount -> {
                activity.hideLoadingDialog()
                for (i in 0 until viewState.pendingGroupRequestsListResponse.data?.size!!) {
                    if(viewState.pendingGroupRequestsListResponse.data!![i].type == "request"){
                        viewModel.currentGroupCount++
                    }
                }
            }
            is GroupViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> activity.hideLoadingDialog()
        }
    }


    private fun setClickListeners() = binding.run {

        yourGroupRelativeLayout.setOnSingleClickListener {
            setActiveTab(yourGroupRelativeLayout)
            viewPager.currentItem = 0
        }

        /*groupRelativeLayout.setOnSingleClickListener {
            setActiveTab(groupRelativeLayout)
            viewPager.currentItem = 1
        }
*/
        invitesRelativeLayout.setOnSingleClickListener {
            setActiveTab(invitesRelativeLayout)
            viewPager.currentItem = 1
        }

        createGroupFloatingActionButton.setOnSingleClickListener {
            if(viewModel.getKycStatus() != "completed") {
                if (viewModel.currentGroupCount >= 1){
                    requireActivity().toastWarning(getString(R.string.group_self_request_non_verified),
                        CpmToast.LONG_DURATION)
                }else if (activity.groupCount < 1) {
                    val intent = GroupActivity.getIntent(requireActivity(), START_CREATE_ORG)
                    startActivity(intent)
                }  else {
                    requireActivity().toastWarning(
                        getString(R.string.not_verified_group),
                        5000
                    )
                }
            }else{
                val intent = GroupActivity.getIntent(requireActivity(), START_CREATE_ORG)
                startActivity(intent)
            }
        }
    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {

            yourGroupRelativeLayout -> {
                yourGroupView.visibility = View.VISIBLE
                yourGroupTextView.visibility = View.VISIBLE

                groupsView.visibility = View.GONE
                groupsTextView.visibility = View.GONE
                invitesView.visibility = View.GONE
                invitesTextView.visibility = View.GONE

                searchImageView.visibility = View.VISIBLE
            }

            /*groupRelativeLayout -> {
                groupsView.visibility = View.VISIBLE
                groupsTextView.visibility = View.VISIBLE

                yourGroupView.visibility = View.GONE
                yourGroupTextView.visibility = View.GONE
                invitesView.visibility = View.GONE
                invitesTextView.visibility = View.GONE
            }*/

            invitesRelativeLayout -> {
                invitesView.visibility = View.VISIBLE
                invitesTextView.visibility = View.VISIBLE

                yourGroupView.visibility = View.GONE
                yourGroupTextView.visibility = View.GONE
                groupsView.visibility = View.GONE
                groupsTextView.visibility = View.GONE

                searchImageView.visibility = View.GONE
            }

            else -> Unit
        }

        searchImageView.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(requireActivity(),
                START_GROUP_SEARCH, groupCount = activity.groupCount)
            startActivity(intent)
        }
    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(GroupsYourGroupFragment.newInstance())
            addFragment(GroupsPendingRequestsFragment.newInstance())
        }

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(viewPager2PageCallback())

    }
    private fun viewPager2PageCallback(): ViewPager2.OnPageChangeCallback {
        return object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0-> setActiveTab(binding.yourGroupRelativeLayout)
                    //1-> setActiveTab(binding.groupRelativeLayout)
                    1-> setActiveTab(binding.invitesRelativeLayout)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val START_GROUP_SEARCH = "START_GROUP_SEARCH"
        private const val START_CREATE_ORG = "START_CREATE_ORG"
    }

}