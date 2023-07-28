package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.databinding.FragmentGroupMembershipReqBinding
import com.lionscare.app.ui.group.adapter.GroupsInvitesAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MembershipRequestFragment : Fragment(), GroupsInvitesAdapter.GroupCallback {

    private var _binding: FragmentGroupMembershipReqBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: GroupsInvitesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupMembershipReqBinding.inflate(
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
        adapter = GroupsInvitesAdapter( requireActivity(),this@MembershipRequestFragment)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        val model = listOf(
            ArticleData(
                name = "Juan Dela Cruz",
                description = "LC-000123"
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
        private const val REQUEST = "REQUEST"
        fun newInstance(): MembershipRequestFragment {
            return MembershipRequestFragment()
        }
    }

    override fun onItemClicked(data: ArticleData) {
        Toast.makeText(requireActivity(),"Clicked : ${data.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onAcceptClicked(data: ArticleData) {
        Toast.makeText(requireActivity(),"Accept : ${data.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDeclineClicked(data: ArticleData) {
        Toast.makeText(requireActivity(),"Declined : ${data.name}", Toast.LENGTH_SHORT).show()
    }


}