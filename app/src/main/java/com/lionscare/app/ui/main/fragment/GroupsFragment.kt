package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.viewpager2.widget.ViewPager2
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.FragmentGroupsBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter
import com.lionscare.app.ui.register.activity.RegisterActivity
import com.lionscare.app.utils.adapter.CustomViewPagerAdapter
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: CustomViewPagerAdapter? = null
    private var searchView: SearchView? = null
    var dataList: List<ArticleData> = emptyList()
    private var menuItem: MenuItem ?= null

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
            val intent = GroupActivity.getIntent(requireActivity(),START_CREATE)
            startActivity(intent)
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

                menuItem?.isVisible = true
                searchView?.visibility = View.VISIBLE
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

                menuItem?.isVisible = false
                searchView?.visibility = View.GONE
            }

            else -> Unit
        }
    }

    private fun setUpTabs() = binding.run {
        pagerAdapter = CustomViewPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapter?.apply {
            addFragment(GroupsYourGroupFragment.newInstance())
            //addFragment(GroupsGroupFragment.newInstance())
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

    //for search and filter
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        menuItem = menu.findItem(R.id.search)
        searchView = menuItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //make viewmodel to get the search value
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val START_CREATE = "START_CREATE"
    }

}