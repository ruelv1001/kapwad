package dswd.ziacare.app.ui.accountcontrol.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dswd.ziacare.app.R
import dswd.ziacare.app.databinding.FragmentDeactivateBinding
import dswd.ziacare.app.ui.accountcontrol.activity.AccountControlActivity
import dswd.ziacare.app.utils.dialog.WebviewDialog
import dswd.ziacare.app.utils.setOnSingleClickListener

class DeactivateFragment : Fragment() {
    private var _binding: FragmentDeactivateBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as AccountControlActivity }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeactivateBinding.inflate(
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
        activity.setToolbarTitle(getString(R.string.deactivate_account))
    }
    private fun setOnClickListeners() = binding.run {
        deactivateButton.setOnSingleClickListener {
            findNavController().navigate(DeactivateFragmentDirections.actionDeactivateFragmentToAccountControlOTPFragment())
        }
        privacyPolicyTextView.setOnSingleClickListener {
            openWebViewDialog("https://agricarewebsite.ziademo.com/privacy-policy")
        }
        termsAndConditionsTextView.setOnSingleClickListener {
            openWebViewDialog("https://agricarewebsite.ziademo.com/terms")
        }
    }

    private fun openWebViewDialog(url: String) {
        WebviewDialog.openDialog(
            childFragmentManager,
            url
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}