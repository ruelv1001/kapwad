package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.FragmentHistoryBinding
import com.lionscare.app.ui.history.adapter.InboundOutboundAdapter
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter

class HistoryFragment : Fragment(), InboundOutboundAdapter.InboundOutboundCallback {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : InboundOutboundAdapter? = null
    private var searchView: SearchView? = null
    private var dataList: List<SampleData> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(
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
        setClickListeners()
        setUpAdapter()
    }

    private fun setUpAdapter() = binding.run {
        adapter = InboundOutboundAdapter(this@HistoryFragment)
        linearLayoutManager = LinearLayoutManager(context)
        historyRecyclerView.layoutManager = linearLayoutManager
        historyRecyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = 1,
                title = "Inbound",
                remarks = getString(R.string.inbound_outbound_hint)
            ),
            SampleData(
                id = 2,
                title = "Outbound",
                remarks = getString(R.string.inbound_outbound_hint)
            )
        )
        adapter?.submitData(lifecycle,PagingData.from(dataList))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }
        })
    }

    private fun filterData(query: String?) {
        val filteredList = dataList.filter { data ->
            data.title.contains(query ?: "", ignoreCase = true) ||
                    data.remarks.contains(query ?: "", ignoreCase = true)
        }
        adapter?.submitData(lifecycle, PagingData.from(filteredList))
    }

    private fun setClickListeners() = binding.run {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: SampleData) {
        // must go to details part
    }

}