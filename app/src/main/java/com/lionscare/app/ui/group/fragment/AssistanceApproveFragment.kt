package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.FragmentGroupAssistanceBinding
import com.lionscare.app.ui.group.adapter.AssistanceAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AssistanceApproveFragment : Fragment(), AssistanceAdapter.GroupCallback {

    private var _binding: FragmentGroupAssistanceBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AssistanceAdapter? = null
    private var direction: NavDirections? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupAssistanceBinding.inflate(
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
        adapter = AssistanceAdapter( requireActivity(),this@AssistanceApproveFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        val model = listOf(
            ArticleData(
                name = "Zyla Valerie",
                description = "2023-07-04 3:59 PM",
                reference = "003254300012",
                type = "200.00"
            ),
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
        fun newInstance(direction: NavDirections): AssistanceApproveFragment {
            val fragment = AssistanceApproveFragment()
            fragment.direction = direction
            return fragment
        }
    }

    override fun onItemClicked(data: ArticleData) {
        findNavController().navigate(direction!!)
    }


}