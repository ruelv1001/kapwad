package com.lionscare.app.ui.main.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
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
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment: Fragment(), GroupsYourGroupAdapter.GroupCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var frontAnim: AnimatorSet? = null
    var backAnim: AnimatorSet? = null

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

    private fun handleViewState(viewState: SettingsViewState) {
        when (viewState) {
            is SettingsViewState.Loading -> showLoadingDialog(R.string.loading)
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
                setView(viewState.userModel)
            }
            else -> hideLoadingDialog()
        }
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
                binding.placeHolderTextView.isVisible = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }

            is ImmediateFamilyViewState.Success -> {
                binding.placeHolderTextView.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true
                binding.immediateFamilyLayout.titleTextView.text =
                    viewState.immediateFamilyResponse?.data?.group_name
            }
        }
    }

    private fun setView(userModel: UserModel?)=binding.run {
        mainLayout.nameTextView.text = userModel?.getFullName()
        if (userModel?.street_name?.isNotEmpty() == true){
            mainLayout.addressTextView.text = "${userModel?.street_name}, ${userModel?.brgy_name},\n${userModel?.city_name}, ${userModel?.province_name}"
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }

    private fun setUpAnimation() {
        frontAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.front_animator) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(requireContext(), R.animator.back_animator) as AnimatorSet

        val scale = resources.displayMetrics.density * 8000
        binding.mainLayout.mainLinearLayout.cameraDistance = scale
        binding.idLayout.virtualIdLinearLayout.cameraDistance = scale
        binding.qrLayout.qrLinearLayout.cameraDistance = scale
    }

    private fun setClickListeners() = binding.run {
        mainLayout.myIdImageView.setOnSingleClickListener {
            frontAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.setTarget(idLayout.virtualIdLinearLayout)
            frontAnim?.start()
            backAnim?.start()

            qrLayout.qrLinearLayout.visibility = View.GONE
        }

        idLayout.myMainLayout.setOnSingleClickListener {
            frontAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.start()
            frontAnim?.start()

        }

        mainLayout.myQrImageView.setOnSingleClickListener {
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
            idLayout.virtualIdLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.start()
            frontAnim?.start()
        }

        idLayout.myQrImageView.setOnSingleClickListener {
            qrLayout.qrLinearLayout.visibility = View.VISIBLE

            frontAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.setTarget(qrLayout.qrLinearLayout)
            frontAnim?.start()
            backAnim?.start()
        }

        qrLayout.myIdImageView.setOnSingleClickListener {
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE

            frontAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.start()
            frontAnim?.start()
        }

        qrLayout.myMainLayout.setOnSingleClickListener {
            frontAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.start()
            frontAnim?.start()
        }

        mainLayout.requestVerifiedBadgeButton.setOnSingleClickListener {
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

        binding.immediateFamilyLayout.adapterLinearLayout.setOnSingleClickListener {
            // clicked immediate family
        }
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