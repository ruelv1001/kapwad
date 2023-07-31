package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentMembershipBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembershipFragment : Fragment() {

    private var _binding: FragmentMembershipBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMembershipBinding.inflate(
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

        requestRelativeLayout.setOnSingleClickListener {
            setActiveTab(requestRelativeLayout)
            viewPager.currentItem = 0
        }

        membersRelativeLayout.setOnSingleClickListener {
            setActiveTab(membersRelativeLayout)
            viewPager.currentItem = 1
        }

    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {

            requestRelativeLayout -> {
                requestView.visibility = View.VISIBLE
                requestTextView.visibility = View.VISIBLE

                membersView.visibility = View.GONE
                membersTextView.visibility = View.GONE
            }

            membersRelativeLayout -> {
                membersView.visibility = View.VISIBLE
                membersTextView.visibility = View.VISIBLE

                requestView.visibility = View.GONE
                requestTextView.visibility = View.GONE
            }
            else -> Unit
        }
    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(MembershipRequestFragment.newInstance())
            addFragment(MembershipMembersFragment.newInstance())
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
                    0-> setActiveTab(binding.requestRelativeLayout)
                    1-> setActiveTab(binding.membersRelativeLayout)
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
    }

}