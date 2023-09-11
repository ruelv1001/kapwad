package com.lionscare.app.ui.main.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.repositories.article.response.ArticleData
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.group.response.GroupListData
import com.lionscare.app.databinding.FragmentHomeBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.group.activity.GroupDetailsActivity
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.ui.main.adapter.GroupsYourGroupAdapter
import com.lionscare.app.ui.main.viewmodel.GroupListViewState
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewModel
import com.lionscare.app.ui.main.viewmodel.ImmediateFamilyViewState
import com.lionscare.app.ui.main.viewmodel.SettingsViewModel
import com.lionscare.app.ui.main.viewmodel.SettingsViewState
import com.lionscare.app.ui.onboarding.activity.SplashScreenActivity
import com.lionscare.app.ui.verify.activity.AccountVerificationActivity
import com.lionscare.app.utils.copyToClipboard
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.setQR
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var frontAnim: AnimatorSet? = null
    var backAnim: AnimatorSet? = null

    private var immediateFamilyId = ""

    private val iFViewModel: ImmediateFamilyViewModel by viewModels()
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAccount()
        observeImmediateFamily()
        setClickListeners()
        setUpAnimation()
        onResume()
        viewModel.getProfileDetails()
        viewModel.getBadgeStatus()
        iFViewModel.getImmediateFamily()
    }

    override fun onResume() {
        super.onResume()
        hideLoadingDialog()
    }

    private fun observeAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleViewState(viewState: SettingsViewState) {
        when (viewState) {
            is SettingsViewState.Loading -> showLoadingDialog(R.string.loading)
            is SettingsViewState.LoadingBadge -> Unit
            is SettingsViewState.PopupError -> {
                hideLoadingDialog()
                /* tinanggal ko yung pop up
                error para walang lumalabas
                if wala pa syang badge
                -josh
                 */
                /*showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )*/
            }

            is SettingsViewState.InputError -> Unit
            is SettingsViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
                setView(viewState.userModel)
            }
            is SettingsViewState.SuccessGetBadgeStatus -> {
                hideLoadingDialog()
//                when(viewState.badgeStatus?.status){
//                    "pending" -> {
//                        binding.mainLayout.requestVerifiedBadgeLinearLayout.visibility = View.GONE
//                        binding.mainLayout.statusVerifiedBadgeLinearLayout.visibility = View.VISIBLE
//
//                        binding.mainLayout.statusVerifiedBadgeImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_access_time_filled_24))
//                        binding.mainLayout.statusVerifiedBadgeTextView.text = "Pending: ${formatBadgeType(viewState.badgeStatusResponse.data.badge_type)}"
//                        binding.mainLayout.statusDateVerifiedBadgeTextView.text = "Submitted on: ${viewState.badgeStatusResponse.data.submitted_date?.date_only}"
//                    }
//                    "approved" -> {
//                        binding.mainLayout.requestVerifiedBadgeLinearLayout.visibility = View.GONE
//                        binding.mainLayout.statusVerifiedBadgeLinearLayout.visibility = View.VISIBLE
//
//                        binding.mainLayout.statusVerifiedBadgeImageView.setImageDrawable(
//                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
//                                ?.mutate()
//                                ?.apply {
//                                    setColorFilter(
//                                        ContextCompat.getColor(requireContext(), R.color.color_primary),
//                                        PorterDuff.Mode.SRC_IN
//                                    )
//                                }
//                        )
//                        binding.mainLayout.statusVerifiedBadgeTextView.text = formatBadgeType(viewState.badgeStatusResponse.data.badge_type)
//                        binding.mainLayout.statusDateVerifiedBadgeTextView.text =  "Approved on: ${viewState.badgeStatusResponse.data.submitted_date?.date_only}"
//                    }
//                    else -> {
//                        binding.mainLayout.requestVerifiedBadgeLinearLayout.visibility = View.VISIBLE
//                        binding.mainLayout.statusVerifiedBadgeLinearLayout.visibility = View.GONE
//                    }
//                }
            }


            else -> hideLoadingDialog()
        }
    }

    private fun formatBadgeType(badge: String?) : String{
        var accountTypeFormmatted = ""
        when(badge){
            "influencer" -> {
                accountTypeFormmatted =   getString(R.string.account_type_influencer_text)
            }
            "public_servant" -> {
                accountTypeFormmatted =   getString(R.string.account_type_public_servant_text)
            }
            "non_government_Organization" -> {
                accountTypeFormmatted =   getString(R.string.account_type_npo_text)
            }
        }
        return accountTypeFormmatted
    }
    private fun observeImmediateFamily() {
        viewLifecycleOwner.lifecycleScope.launch {
            iFViewModel.immediateFamilySharedFlow.collectLatest { viewState ->
                iFHandleViewState(viewState)
            }
        }
    }

    private fun iFHandleViewState(viewState: ImmediateFamilyViewState) {
        when (viewState) {
            ImmediateFamilyViewState.Loading -> Unit
            is ImmediateFamilyViewState.PopupError -> {
                //showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
                binding.createGroupButton.isVisible = true
            }

            is ImmediateFamilyViewState.Success -> {
                binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true
                binding.createGroupButton.isGone = true
                viewState.immediateFamilyResponse?.data?.let { setImmediateFamily(it) }
                immediateFamilyId = viewState.immediateFamilyResponse?.data?.id.toString()
            }
        }
    }

    private fun setImmediateFamily(data: GroupListData) = binding.run{
        immediateFamilyLayout.titleTextView.text = data.name
        immediateFamilyLayout.membersTextView.text = if ((data.member_count ?: 0) > 1) {
            "${data.member_count} members"
        } else {
            "${data.member_count} member"
        }
        immediateFamilyLayout.referenceTextView.text = data.qrcode
    }

    private fun setView(userModel: UserModel?) = binding.run {
        //main
        mainLayout.nameTextView.text = userModel?.name
        mainLayout.idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
        mainLayout.dateIssuedTextView.text = userModel?.date_registered?.date_only

        //qr
        qrLayout.qrCodeTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
        qrLayout.qrImageView.setImageBitmap(setQR(requireActivity(), userModel?.qrcode_value))

        //id
        idLayout.nameTextView.text = userModel?.name
        idLayout.idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
        idLayout.qrImageView.setImageBitmap(setQR(requireActivity(), userModel?.qrcode_value))
        idLayout.dateIssuedTextView.text = userModel?.date_registered?.date_only

        if (userModel?.street_name?.isNotEmpty() == true) {
            mainLayout.addressTextView.text =
                "${userModel?.street_name}, ${userModel?.brgy_name},\n${userModel?.city_name}, ${userModel?.province_name}"
            idLayout.addressTextView.text =
                "${userModel?.street_name}, ${userModel?.brgy_name},\n${userModel?.city_name}, ${userModel?.province_name}"
        }

        viewModel.userQrCode = userModel?.qrcode.orEmpty()
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }

    private fun setUpAnimation() {
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.front_animator
        ) as AnimatorSet
        backAnim =
            AnimatorInflater.loadAnimator(requireContext(), R.animator.back_animator) as AnimatorSet

        val scale = resources.displayMetrics.density * 8000
        binding.mainLayout.mainLinearLayout.cameraDistance = scale
        binding.idLayout.virtualIdLinearLayout.cameraDistance = scale
        binding.qrLayout.qrLinearLayout.cameraDistance = scale
    }

    private fun setClickListeners() = binding.run {
        mainLayout.myIdImageView.setOnSingleClickListener {
            qrLayout.qrLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.setTarget(idLayout.virtualIdLinearLayout)
            frontAnim?.start()
            backAnim?.start()
            mainLayout.mainLinearLayout.visibility = View.VISIBLE
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE
        }

        idLayout.myMainLayout.setOnSingleClickListener {
            qrLayout.qrLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE
            mainLayout.mainLinearLayout.visibility = View.VISIBLE
        }

        mainLayout.myQrImageView.setOnSingleClickListener {
            idLayout.virtualIdLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            mainLayout.mainLinearLayout.visibility = View.VISIBLE
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
        }

        idLayout.myQrImageView.setOnSingleClickListener {
            mainLayout.mainLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.setTarget(qrLayout.qrLinearLayout)
            frontAnim?.start()
            backAnim?.start()
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
        }

        qrLayout.myIdImageView.setOnSingleClickListener {
            mainLayout.mainLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE
        }

        qrLayout.myMainLayout.setOnSingleClickListener {
            idLayout.virtualIdLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
            mainLayout.mainLinearLayout.visibility = View.VISIBLE
        }

        mainLayout.requestVerifiedBadgeLinearLayout.setOnSingleClickListener {
            val intent = VerifiedBadgeActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        getVerifiedButton.setOnSingleClickListener {
            val intent = AccountVerificationActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        groupsLinearLayout.setOnSingleClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationGroups()
            findNavController().navigate(action)
        }

        immediateFamilyLayout.adapterLinearLayout.setOnSingleClickListener {
            val intent = GroupDetailsActivity.getIntent(requireActivity(), immediateFamilyId)
            startActivity(intent)
        }

        createGroupButton.setOnSingleClickListener {
            val intent = GroupActivity.getIntent(requireActivity(), START_CREATE_FAMILY)
            startActivity(intent)
        }
        qrLayout.qrCodeTextView.setOnSingleClickListener {
            activity?.copyToClipboard(viewModel.userQrCode)
        }
    }


    companion object {
        private const val START_CREATE_FAMILY = "START_CREATE_FAMILY"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: GroupListData) {
        val intent = GroupDetailsActivity.getIntent(requireActivity())
        startActivity(intent)
    }
}