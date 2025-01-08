package kapwad.reader.app.ui.main.fragment

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
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.databinding.FragmentSettingsBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.main.viewmodel.SettingsViewModel
import kapwad.reader.app.ui.main.viewmodel.SettingsViewState
import kapwad.reader.app.ui.onboarding.activity.SplashScreenActivity
import kapwad.reader.app.ui.profile.activity.NotificationsActivity
import kapwad.reader.app.ui.profile.activity.ProfileActivity
import kapwad.reader.app.ui.profile.activity.UpdatePasswordActivity
import kapwad.reader.app.ui.verify.activity.AccountVerificationActivity
import kapwad.reader.app.utils.dialog.WebviewDialog
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kapwad.reader.app.BuildConfig

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
               // viewModel.userKycStatus = viewState.userModel?.kyc_status.toString()
                setView(viewState.userModel)
            }
            else -> Unit
        }
    }

    private fun setView(userModel: UserModel?)=binding.run {
     //   profileImageView.loadAvatar(userModel?.avatar?.thumb_path, requireContext())
       // nameTextView.text = userModel?.name
        //badgeTextView.isVisible = false
        //idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")



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
            openWebViewDialog(BuildConfig.PRIVACY_POLICY_URL)
        }
        faqLinearLayout.setOnClickListener{
            openWebViewDialog(BuildConfig.FAQ_URL)
        }
        termsLinearLayout.setOnClickListener{
            openWebViewDialog(BuildConfig.TERMS_URL)
        }
        contactLinearLayout.setOnClickListener{
            openWebViewDialog(BuildConfig.CONTACT_US_URL)
        }
        aboutLinearLayout.setOnClickListener{
            openWebViewDialog(BuildConfig.ABOUT_US_URL)
        }
    }

    private fun setDetails(data: UserModel?) = binding.run {
        if(isBadgeVerified == true){
            binding.badgeIdStatus.visibility = View.VISIBLE
            binding.badgeImageView.visibility = View.VISIBLE
            //change icon of avatar badge


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