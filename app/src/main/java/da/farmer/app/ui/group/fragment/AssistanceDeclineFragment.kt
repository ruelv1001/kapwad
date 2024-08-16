package da.farmer.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import da.farmer.app.data.repositories.article.response.ArticleData
import da.farmer.app.data.repositories.assistance.response.CreateAssistanceData
import da.farmer.app.databinding.FragmentGroupAssistanceBinding
import da.farmer.app.ui.group.adapter.AssistanceAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AssistanceDeclineFragment : Fragment(), AssistanceAdapter.GroupCallback {

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
        adapter = AssistanceAdapter(requireActivity(),this@AssistanceDeclineFragment)
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
        fun newInstance(direction: NavDirections): AssistanceDeclineFragment {
            val fragment = AssistanceDeclineFragment()
            fragment.direction = direction
            return fragment
        }
    }

    override fun onItemClicked(data: CreateAssistanceData) {
        findNavController().navigate(direction!!)
    }


}