package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentMembersBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersFragment : Fragment() {

    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMembersBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabs()
        setClickListeners()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_membership))
    }

    private fun setClickListeners() = binding.run {

        pendingRequestCountTextView.isVisible = activity.groupDetails?.pending_requests_count != 0
        pendingRequestCountTextView.text = activity.groupDetails?.pending_requests_count.toString()

        membersRelativeLayout.setOnSingleClickListener {
            setActiveTab(membersRelativeLayout)
            viewPager.currentItem = 0
        }

        requestRelativeLayout.setOnSingleClickListener {
            setActiveTab(requestRelativeLayout)
            viewPager.currentItem = 1
        }

        inviteMemberFloatingActionButton.setOnSingleClickListener {
            findNavController().navigate(MembersFragmentDirections.actionNavigationGroupMembershipToNavigationGroupInvite())
        }
    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {

            requestRelativeLayout -> {
                requestView.visibility = View.VISIBLE
                requestTextView.visibility = View.VISIBLE

                membersView.visibility = View.GONE
                membersTextView.visibility = View.GONE

                activity.getFilterImageView().isVisible = true
                inviteMemberFloatingActionButton.isVisible = false
            }

            membersRelativeLayout -> {
                membersView.visibility = View.VISIBLE
                membersTextView.visibility = View.VISIBLE

                requestView.visibility = View.GONE
                requestTextView.visibility = View.GONE

                activity.getFilterImageView().isVisible = false
                //TODO: Add isAdmin condition
                inviteMemberFloatingActionButton.isVisible = true
            }
            else -> Unit
        }
    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(MemberListFragment.newInstance())
            addFragment(MembershipRequestFragment.newInstance())
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
                    0-> setActiveTab(binding.membersRelativeLayout)
                    1-> setActiveTab(binding.requestRelativeLayout)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val APPROVED = "APPROVED"
        private const val START_INVITE = "START_INVITE"
    }

}