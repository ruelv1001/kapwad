package kapwad.reader.app.ui.accountcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentChooseDeactivateOrDeleteBinding
import kapwad.reader.app.ui.accountcontrol.activity.AccountControlActivity
import kapwad.reader.app.utils.setOnSingleClickListener

@AndroidEntryPoint
class ChooseDeactivateOrDeleteFragment : Fragment() {
    private var _binding : FragmentChooseDeactivateOrDeleteBinding? = null
    private val binding get() = _binding!!

    private val activity by lazy { requireActivity() as AccountControlActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChooseDeactivateOrDeleteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        activity.setToolbarTitle(getString(R.string.account_ownership_control))
    }

    private fun setOnClickListeners() = binding.run {
        deactivateAccountCardView.setOnSingleClickListener {
            activity.selectedChoice = AccountControlActivity.DEACTIVATE
            findNavController().navigate(ChooseDeactivateOrDeleteFragmentDirections.actionChooseDeactivateOrDeleteFragmentToDeactivateOrDeleteFormFragment())
        }
        deleteAccountPermanentlyCardView.setOnSingleClickListener {
            activity.selectedChoice = AccountControlActivity.DELETE
            findNavController().navigate(ChooseDeactivateOrDeleteFragmentDirections.actionChooseDeactivateOrDeleteFragmentToDeactivateOrDeleteFormFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}