package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.assistance.response.CreateAssistanceData
import com.lionscare.app.databinding.FragmentGroupAssistanceBinding
import com.lionscare.app.ui.group.adapter.AssistanceAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AssistanceRequestFragment : Fragment(), AssistanceAdapter.GroupCallback {

    private var _binding: FragmentGroupAssistanceBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AssistanceAdapter? = null
    private var actionNavigationGroupAssistanceReqDetails: NavDirections? = null

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
        adapter = AssistanceAdapter(this@AssistanceRequestFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }

    private fun setClickListeners() = binding.run {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST = "REQUEST"
        fun newInstance(direction: NavDirections): AssistanceRequestFragment {
            val fragment = AssistanceRequestFragment()
            fragment.actionNavigationGroupAssistanceReqDetails = direction
            return fragment
        }
    }

    override fun onItemClicked(data: CreateAssistanceData) {
        findNavController().navigate(actionNavigationGroupAssistanceReqDetails!!)
    }


}