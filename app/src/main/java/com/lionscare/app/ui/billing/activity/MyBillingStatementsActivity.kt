package com.lionscare.app.ui.billing.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.databinding.ActivityMyBillingStatementsBinding
import com.lionscare.app.ui.billing.fragment.CompletedBillingStatementsFragment
import com.lionscare.app.ui.billing.fragment.OngoingBillingStatementsFragment
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBillingStatementsActivity : AppCompatActivity() {

    private var _binding: ActivityMyBillingStatementsBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMyBillingStatementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpTabs()
        setOnClickListeners()
    }

    private fun setOnClickListeners() = binding.run {
        ongoingBillingRelativeLayout.setOnSingleClickListener {
            setActiveTab(ongoingBillingRelativeLayout)
            viewPager.currentItem = 0
        }

        completedBillingRelativeLayout.setOnSingleClickListener {
            setActiveTab(completedBillingRelativeLayout)
            viewPager.currentItem = 1
        }

        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {
            ongoingBillingRelativeLayout -> {
                ongoingBillingView.visibility = View.VISIBLE
                ongoingBillingTextView.visibility = View.VISIBLE
                completedBillingView.visibility = View.GONE
                completedBillingTextView.visibility = View.GONE
            }

            completedBillingRelativeLayout -> {
                ongoingBillingView.visibility = View.GONE
                ongoingBillingTextView.visibility = View.GONE
                completedBillingTextView.visibility = View.VISIBLE
                completedBillingView.visibility = View.VISIBLE
            }

            else -> Unit
        }
    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(supportFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(OngoingBillingStatementsFragment.newInstance())
            addFragment(CompletedBillingStatementsFragment.newInstance())
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
                when (position) {
                    0 -> setActiveTab(binding.ongoingBillingRelativeLayout)
                    1 -> setActiveTab(binding.completedBillingRelativeLayout)
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MyBillingStatementsActivity::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}