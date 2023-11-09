package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.databinding.FragmentBulletinBinding
import com.lionscare.app.ui.bulletin.activity.AllRequestBulletinActivity
import com.lionscare.app.ui.bulletin.fragment.AskedForDonationFragment
import com.lionscare.app.ui.bulletin.fragment.PublicRequestFragment
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BulletinFragment : Fragment() {

    private var _binding: FragmentBulletinBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBulletinBinding.inflate(
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
    }

    private fun setClickListeners() = binding.run {
        publicRequestsRelativeLayout.setOnSingleClickListener {
            setActiveTab(publicRequestsRelativeLayout)
            viewPager.currentItem = 0
        }

        askedForDonationsRelativeLayout.setOnSingleClickListener {
            setActiveTab(askedForDonationsRelativeLayout)
            viewPager.currentItem = 1
        }

        viewAllTextView.setOnSingleClickListener {
            val intent = AllRequestBulletinActivity.getIntent(requireActivity())
            startActivity(intent)
        }
    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {

            publicRequestsRelativeLayout -> {
                publicRequestsView.visibility = View.VISIBLE
                publicRequestsTextView.visibility = View.VISIBLE
                askedForDonationsView.visibility = View.GONE
                askedForDonationsTextView.visibility = View.GONE
                viewAllTextView.isVisible = true
            }

            askedForDonationsRelativeLayout -> {
                publicRequestsView.visibility = View.GONE
                publicRequestsTextView.visibility = View.GONE
                askedForDonationsView.visibility = View.VISIBLE
                askedForDonationsTextView.visibility = View.VISIBLE

                viewAllTextView.isGone = true
            }

            else -> Unit
        }

    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(PublicRequestFragment.newInstance())
            addFragment(AskedForDonationFragment.newInstance())
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
                    0-> setActiveTab(binding.publicRequestsRelativeLayout)
                    1-> setActiveTab(binding.askedForDonationsRelativeLayout)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}