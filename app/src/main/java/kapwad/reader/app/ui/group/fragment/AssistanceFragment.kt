package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentAssistanceBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.dialog.FilterDialog
import kapwad.reader.app.utils.adapter.CustomViewPagerAdapter
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistanceFragment : Fragment() {

    private var _binding: FragmentAssistanceBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    private val activity by lazy { requireActivity() as GroupActivity }
    private var filterList = emptyList<String>()

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
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_assistance))
        if(activity.getAssistanceDetailsType() == ALL_REQUEST){
            setActiveTab(binding.allRequestRelativeLayout)
            binding.viewPager.currentItem = 1
        }else{
            setActiveTab(binding.requestRelativeLayout)
            binding.viewPager.currentItem = 0
        }
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
            findNavController().navigate(AssistanceFragmentDirections.actionNavigationGroupAssistanceCreate())
        }

        activity.getFilterImageView().setOnSingleClickListener {
            FilterDialog.newInstance(object : FilterDialog.FilterDialogListener {
                override fun onFilter(filter: ArrayList<String>) {
                    filterList = filter
                    applyOnFilterSelectedListener()
                }

                override fun onCancel() {
                    filterList = emptyList()
                    applyOnFilterSelectedListener()
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
                val currentFragment = getCurrentFragment(position)
                if (currentFragment is FilterSelectedListener){
                    currentFragment.onFilterSelected(filterList)
                }
            }
        }
    }
    private fun getCurrentFragment(position: Int): Fragment?{
        return childFragmentManager.findFragmentByTag("f$position")
    }

    private fun applyOnFilterSelectedListener(){
        val currentFragment = getCurrentFragment(binding.viewPager.currentItem)
        if (currentFragment is FilterSelectedListener){
            currentFragment.onFilterSelected(filterList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.unregisterOnPageChangeCallback(viewPager2PageCallback())
        _binding = null
    }

    companion object {
        private const val ALL_REQUEST = "ALL_REQUEST"
        private const val DECLINED = "DECLINED"
        private const val REQUEST = "REQUEST"
    }

}