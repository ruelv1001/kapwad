package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.databinding.ActivityRequestForDonationBinding
import com.lionscare.app.ui.bulletin.fragment.AskedForDonationFragment
import com.lionscare.app.ui.bulletin.fragment.PublicRequestFragment
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestForDonationActivity  : AppCompatActivity() {

    private var _binding: ActivityRequestForDonationBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRequestForDonationBinding.inflate(layoutInflater)
        setContentView( binding.root)
        setUpTabs()
        setOnClickListeners()
    }

    private fun setOnClickListeners() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        myRequestsRelativeLayout.setOnSingleClickListener {
            setActiveTab(myRequestsRelativeLayout)
            viewPager.currentItem = 0
        }

        allRequestsRelativeLayout.setOnSingleClickListener {
            setActiveTab(allRequestsRelativeLayout)
            viewPager.currentItem = 1
        }
    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {

            myRequestsRelativeLayout -> {
                myRequestsView.visibility = View.VISIBLE
                myRequestsTextView.visibility = View.VISIBLE
                allRequestsView.visibility = View.GONE
                allRequestsTextView.visibility = View.GONE
            }

            allRequestsRelativeLayout -> {
                myRequestsView.visibility = View.GONE
                myRequestsTextView.visibility = View.GONE
                allRequestsView.visibility = View.VISIBLE
                allRequestsTextView.visibility = View.VISIBLE
            }

            else -> Unit
        }

    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(supportFragmentManager, lifecycle)
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
                    0-> setActiveTab(binding.myRequestsRelativeLayout)
                    1-> setActiveTab(binding.allRequestsRelativeLayout)
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
            return Intent(context, RequestForDonationActivity::class.java)
        }
    }
}