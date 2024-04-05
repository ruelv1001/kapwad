package dswd.ziacare.app.ui.main.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.baseresponse.UserModel
import dswd.ziacare.app.databinding.FragmentSettingsBinding
import dswd.ziacare.app.ui.main.activity.MainActivity
import dswd.ziacare.app.ui.main.viewmodel.SettingsViewModel
import dswd.ziacare.app.ui.main.viewmodel.SettingsViewState
import dswd.ziacare.app.ui.onboarding.activity.SplashScreenActivity
import dswd.ziacare.app.ui.profile.activity.NotificationsActivity
import dswd.ziacare.app.ui.profile.activity.ProfileActivity
import dswd.ziacare.app.ui.profile.activity.UpdatePasswordActivity
import dswd.ziacare.app.ui.verify.activity.AccountVerificationActivity
import dswd.ziacare.app.utils.dialog.WebviewDialog
import dswd.ziacare.app.utils.loadAvatar
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var isBadgeVerified : Boolean? = false
    private var isAccountVerified : Boolean? = false
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLogoutAccount()
        setClickListeners()

        binding.swipeRefreshLayout.setOnRefreshListener(this@ProfileFragment)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getProfileDetails()
    }
    private fun observeLogoutAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: SettingsViewState) {
        when (viewState) {
            is SettingsViewState.Loading -> showLoadingDialog(R.string.logout_loading)
            is SettingsViewState.LoadingProfile -> showLoadingDialog(R.string.loading) //so it wont show "logging out" on get profile
            is SettingsViewState.Success -> {
                hideLoadingDialog()
                binding.swipeRefreshLayout.isRefreshing = false
                requireActivity().toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                val intent = SplashScreenActivity.getIntent(requireActivity())
                startActivity(intent)
                requireActivity().finishAffinity()
            }

            is SettingsViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is SettingsViewState.InputError -> Unit
            is SettingsViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
                binding.swipeRefreshLayout.isRefreshing = false
                viewModel.userKycStatus = viewState.userModel?.kyc_status.toString()
                setView(viewState.userModel)
            }
            else -> Unit
        }
    }

    private fun setView(userModel: UserModel?)=binding.run {
        profileImageView.loadAvatar(userModel?.avatar?.thumb_path, requireContext())
        nameTextView.text = userModel?.name
        badgeTextView.isVisible = false
        idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")

        isBadgeVerified = userModel?.badge_type?.isNotEmpty() == true
        when(userModel?.kyc_status){
            "ongoing" -> {
                isAccountVerified = false
            }
            "completed" -> {
                isAccountVerified = true
            }
            "pending" -> {
                isAccountVerified = false
            }
        }

        setDetails(userModel)
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }

    private fun setClickListeners() = binding.run {
        profileDetailsLinearLayout.setOnSingleClickListener {
            val intent = ProfileActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        verifyAccountLinearLayout.setOnSingleClickListener {
            val intent = AccountVerificationActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        changePasswordLinearLayout.setOnSingleClickListener {
            val intent = UpdatePasswordActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        logoutLinearLayout.setOnSingleClickListener {
            openLogoutConfirmation()
        }

        profileLinearLayout.setOnSingleClickListener {
            val intent = ProfileActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        notifLinearLayout.setOnSingleClickListener {
            val intent = NotificationsActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        privacyPolicyLinearLayout.setOnClickListener{
            openWebViewDialog("https://www.lionscare.ph/privacy-policy")
        }
        faqLinearLayout.setOnClickListener{
            openWebViewDialog("https://www.lionscare.ph/faq")
        }
        termsLinearLayout.setOnClickListener{
            openWebViewDialog("https://www.lionscare.ph/terms")
        }
        contactLinearLayout.setOnClickListener{
            openWebViewDialog("https://www.lionscare.ph/contact")
        }
        aboutLinearLayout.setOnClickListener{
            openWebViewDialog("https://www.lionscare.ph/about")
        }
    }

    private fun setDetails(data: UserModel?) = binding.run {
        if(isBadgeVerified == true){
            binding.badgeIdStatus.visibility = View.VISIBLE
            binding.badgeImageView.visibility = View.VISIBLE
            //change icon of avatar badge
            when(data?.badge_type){
                "non_government_organization" -> {
                    binding.badgeImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_npo))
                    binding.badgeIdStatus.text = getString(R.string.account_type_npo_text)
                    binding.badgeIdStatus.setBackgroundResource(R.drawable.bg_rounded_npo)
                }
                "influencer" -> {
                    binding.badgeImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_thumbs_up))
                    binding.badgeIdStatus.text = getString(R.string.account_type_influencer_text)
                    binding.badgeIdStatus.setBackgroundResource(R.drawable.bg_rounded_influencer)
                }
                "public_servant" -> {
                    binding.badgeImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_public_servant))
                    binding.badgeIdStatus.text = getString(R.string.account_type_public_servant_text)
                    binding.badgeIdStatus.setBackgroundResource(R.drawable.bg_rounded_public_servant)
                }
            }
        }else{
            binding.badgeIdStatus.visibility = View.GONE
            binding.badgeImageView.visibility = View.GONE
        }

        if(isAccountVerified == true){
            verifiedVATextView.visibility = View.VISIBLE
            arrowVAImageView.visibility = View.GONE
        }else{
            verifyAccountImageView.setImageResource(R.drawable.ic_settings_verify_account)
            verifiedVATextView.visibility = View.GONE
            arrowVAImageView.visibility = View.VISIBLE
        }

        swipeRefreshLayout.isEnabled = isAccountVerified != true || isBadgeVerified != true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openLogoutConfirmation() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.logout_title_lbl))
        builder.setMessage(getString(R.string.logout_desc_lbl))
        builder.setPositiveButton(getString(R.string.logout_btn)) { _, _ ->
            viewModel.doLogoutAccount()
        }
        builder.setNegativeButton(getString(R.string.logout_cancel_btn), null)
        builder.show()
    }

    override fun onRefresh() {
        viewModel.getProfileDetails()
    }

    private fun openWebViewDialog(url: String){
        WebviewDialog.openDialog(
            childFragmentManager,
            url
        )
    }

}