package kapwad.reader.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentGroupCommunityRolesBinding
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.viewmodel.AdminViewModel
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityRolesFragment: Fragment() {
    private var _binding: FragmentGroupCommunityRolesBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupCommunityRolesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setView()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_group_community_roles))
    }

    private fun setView() = binding.run{
        activity.getRolesView().isVisible = activity.groupDetails?.owner_user_id == viewModel.user.id
    }

    private fun setClickListeners() = binding.run {
        activity.getRolesView().setOnSingleClickListener {
            findNavController().navigate(CommunityRolesFragmentDirections.actionNavigationRolesUpdate())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}