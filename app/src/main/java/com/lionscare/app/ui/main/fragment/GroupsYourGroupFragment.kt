package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.FragmentGroupsYourGroupBinding
import com.lionscare.app.ui.group.activity.GroupDetailsActivity
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GroupsYourGroupFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback {

    private var _binding: FragmentGroupsYourGroupBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupsYourGroupAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupsYourGroupBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setupAdapter()
    }

    private fun setupAdapter() = binding.run {
        adapter = GroupsYourGroupAdapter(requireActivity(), this@GroupsYourGroupFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        val model = listOf(
            ArticleData(
                name = "Malasakit Family",
                description = "10 Members",
                type = "FAM",
                reference = "IF-000001"
            ),
            ArticleData(
                name = "Quick Response Group",
                description = "8 Members",
                type = "ORG",
                reference = "OR-000001"
            )
        )
        adapter?.appendData(model)
    }

    private fun setClickListeners() = binding.run {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = GroupsYourGroupFragment()
    }

    override fun onItemClicked(data: ArticleData) {
        val intent = GroupDetailsActivity.getIntent(requireActivity())
        startActivity(intent)
    }


}