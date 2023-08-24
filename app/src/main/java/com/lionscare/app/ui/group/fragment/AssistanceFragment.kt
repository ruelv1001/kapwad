package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentAssistanceBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.dialog.FilterDialog
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistanceFragment : Fragment() {

    private var _binding: FragmentAssistanceBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAssistanceBinding.inflate(
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
        activity.setTitlee(getString(R.string.lbl_assistance))
    }

    private fun setClickListeners() = binding.run {

        requestRelativeLayout.setOnSingleClickListener {
            setActiveTab(requestRelativeLayout)
            viewPager.currentItem = 0
        }

        allRequestRelativeLayout.setOnSingleClickListener {
            setActiveTab(allRequestRelativeLayout)
            viewPager.currentItem = 1
        }

        createAssistanceFloatingActionButton.setOnSingleClickListener {

        }

        activity.getFilterImageView().setOnSingleClickListener {
            FilterDialog.newInstance(object : FilterDialog.FilterDialogListener {
                override fun onFilter(filter: ArrayList<String>) {
                    CommonLogger.devLog("FILTERS", filter)
                }

                override fun onCancel() {
                    // set filter to all
                }

            }).show(childFragmentManager, FilterDialog.TAG)
        }

        /*declineRelativeLayout.setOnSingleClickListener {
            setActiveTab(declineRelativeLayout)
            viewPager.currentItem = 2
        }*/


    }

    private fun setActiveTab(layout: RelativeLayout) = binding.run {
        when (layout) {

            requestRelativeLayout -> {
                activity.setAssistanceDetailsType(REQUEST)

                requestView.visibility = View.VISIBLE
                requestTextView.visibility = View.VISIBLE

                allRequestView.visibility = View.GONE
                allRequestTextView.visibility = View.GONE
                declineView.visibility = View.GONE
                declineTextView.visibility = View.GONE
            }

            allRequestRelativeLayout -> {
                activity.setAssistanceDetailsType(ALL_REQUEST)

                allRequestView.visibility = View.VISIBLE
                allRequestTextView.visibility = View.VISIBLE

                requestView.visibility = View.GONE
                requestTextView.visibility = View.GONE
                declineView.visibility = View.GONE
                declineTextView.visibility = View.GONE
            }

            /*declineRelativeLayout -> {
                activity.setAssistanceDetailsType(DECLINED)

                declineView.visibility = View.VISIBLE
                declineTextView.visibility = View.VISIBLE

                requestView.visibility = View.GONE
                requestTextView.visibility = View.GONE
                approveView.visibility = View.GONE
                approveTextView.visibility = View.GONE
            }*/

            else -> Unit
        }
    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(AssistanceRequestFragment.newInstance(
                AssistanceFragmentDirections.actionNavigationGroupAssistanceReqDetails()))
            addFragment(AssistanceAllRequestFragment.newInstance(
                AssistanceFragmentDirections.actionNavigationGroupAssistanceReqDetails()))
            /*addFragment(AssistanceDeclineFragment.newInstance(
                AssistanceFragmentDirections.actionNavigationGroupAssistanceReqDetails()))*/
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
                    1-> setActiveTab(binding.allRequestRelativeLayout)
                    //2-> setActiveTab(binding.declineRelativeLayout)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ALL_REQUEST = "ALL_REQUEST"
        private const val DECLINED = "DECLINED"
        private const val REQUEST = "REQUEST"
    }

}