package com.lionscare.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.FragmentWalletSearchBinding
import com.lionscare.app.ui.wallet.activity.WalletActivity
import com.lionscare.app.ui.wallet.adapter.MembersAdapter
import com.lionscare.app.utils.setOnSingleClickListener

class WalletSearchFragment : Fragment(), MembersAdapter.MembersCallback {

    private var _binding: FragmentWalletSearchBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter : MembersAdapter? = null
    private var dataList: List<SampleData> = emptyList()
    private val activity by lazy { requireActivity() as WalletActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupDetails()
        setupClickListener()
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_to_title)
            }
            "Post Request" -> {
                titleTextView.text = getString(R.string.wallet_request_points_from_title)
            }
        }

    }

    private fun setupAdapter() = binding.run {
        adapter = MembersAdapter(this@WalletSearchFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        dataList = listOf(
            SampleData(
                id = R.drawable.img_profile,
                title = "Raquel Castro",
                amount = "LC-000004",
            ),
            SampleData(
                id = R.drawable.img_profile,
                title = "Romeo Dela Cruz",
                amount = "LC-000001",
            )
        )
        adapter?.submitData(lifecycle, PagingData.from(dataList))

        searchView.setOnQueryTextListener(object : OnQueryTextListener{
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
            data.amount?.contains(query ?: "", ignoreCase = true) == true
        }
        adapter?.submitData(lifecycle, PagingData.from(filteredList))
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: SampleData) {
        activity.data = data
        findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
    }

}