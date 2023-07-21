package com.lionscare.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.FragmentGroupsGroupBinding
import com.lionscare.app.ui.main.adapter.GroupsGroupAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupsGroupFragment : Fragment(), GroupsGroupAdapter.GroupCallback {

    private var _binding: FragmentGroupsGroupBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupsGroupAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupsGroupBinding.inflate(
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

    private fun setClickListeners() = binding.run {

    }
    private fun setupAdapter() = binding.run {
        adapter = GroupsGroupAdapter(requireActivity(), this@GroupsGroupFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        val model = listOf(
            ArticleData(
                name = "Group 1",
                description = "10 Members",
                reference = "CL-000001"
            ),
            ArticleData(
                name = "Group 2",
                description = "8 Members",
                reference = "CL-000002"
            ),
            ArticleData(
                name = "Group 3",
                description = "6 Members",
                reference = "CL-000003"
            ),
            ArticleData(
                name = "Group 4",
                description = "2 Members",
                reference = "CL-000004"
            )
        )
        adapter?.appendData(model)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = GroupsGroupFragment()
    }

    override fun onItemClicked(data: ArticleData) {
        Toast.makeText(requireActivity(),"CLICKED Title : ${data.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onJoinClicked(data: ArticleData) {
        Toast.makeText(requireActivity(),"JOINED Title : ${data.name}", Toast.LENGTH_SHORT).show()
    }

}