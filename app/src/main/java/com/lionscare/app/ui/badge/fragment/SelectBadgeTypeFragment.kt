package com.lionscare.app.ui.badge.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import com.lionscare.app.R
import com.lionscare.app.data.model.AccountTypeModel
import com.lionscare.app.data.repositories.profile.request.BadgeRemovalRequest
import com.lionscare.app.databinding.FragmentSelectBadgeTypeBinding
import com.lionscare.app.ui.badge.activity.VerifiedBadgeActivity
import com.lionscare.app.ui.badge.adapter.AccountTypeAdapter
import com.lionscare.app.ui.badge.dialog.ReasonBottomSheetDialog
import com.lionscare.app.ui.badge.viewmodel.BadgeViewModel
import com.lionscare.app.ui.main.activity.MainActivity
import com.lionscare.app.ui.profile.viewmodel.ProfileViewState
import com.lionscare.app.utils.AppConstant.NOT_FOUND
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.PopupErrorState
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectBadgeTypeFragment : Fragment(), AccountTypeAdapter.OnClickCallback {

    private var _binding: FragmentSelectBadgeTypeBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AccountTypeAdapter? = null
    private var hasSelected = false
    private var accountTypeList = emptyList<AccountTypeModel>()
    private val activity by lazy { requireActivity() as VerifiedBadgeActivity }
    private val viewModel : BadgeViewModel by viewModels()
    private var loadingDialog: CommonDialog? =  null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBadgeTypeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        setupClickListener()
        viewModel.getBadgeRemovalStatus()
        viewModel.getBadgeStatus()

        setupList()
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.badgeSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.LoadingBadgeStatus -> Unit
            is ProfileViewState.SuccessGetBadgeStatus -> {
                when(viewState.badgeStatusResponse.data?.badge_type){
                    "non_government_organization" -> {
                        if(viewState.badgeStatusResponse.data.status != "declined"){
                            adapter?.changeIsBadgeRequestedBefore(true)
                            binding.continueButton.isEnabled = false
                            binding.removeBadgeButton.isEnabled = true
                            //call the item clicked to display which has been seelcted prevously by the user
                            onItemClicked( AccountTypeModel(getString(R.string.account_type_npo_text), R.drawable.ic_npo), 2)
                        }

                    }
                    "influencer" -> {
                        if(viewState.badgeStatusResponse.data.status != "declined"){
                            adapter?.changeIsBadgeRequestedBefore(true)
                            binding.continueButton.isEnabled = false
                            binding.removeBadgeButton.isEnabled = true
                            onItemClicked( AccountTypeModel(getString(R.string.account_type_influencer_text), R.drawable.ic_thumbs_up), 0)
                        }
                    }
                    "public_servant" -> {
                        if(viewState.badgeStatusResponse.data.status != "declined"){
                            adapter?.changeIsBadgeRequestedBefore(true)
                            binding.continueButton.isEnabled = false
                            binding.removeBadgeButton.isEnabled = true
                            onItemClicked(  AccountTypeModel(
                                getString(R.string.account_type_public_servant_text),
                                R.drawable.ic_public_servant
                            ), 1)
                        }

                    }
                }
            }
            is ProfileViewState.Success -> { //cancel request of badge removal
                hideLoadingDialog()
                requireActivity().toastSuccess(message = viewState.message)
                viewModel.setBadgeRemovalRequestCancelled(true)
                binding.removeBadgeButton.isEnabled = true
                binding.removeBadgeButton.text = getString(R.string.remove_badge)
                binding.removeBadgeButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            }
            is ProfileViewState.SuccessBadgeRemovalStatus -> {
                hideLoadingDialog()
                when(viewState.badgeRemovalStatus?.status){
                    "pending" -> {
                        viewModel.setBadgeRemovalRequestCancelled(false)
                        binding.removeBadgeButton.isEnabled = true
                        binding.removeBadgeButton.text = getString(R.string.cancel_badge_removal_request)
                        binding.removeBadgeButton.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.pending))
                    }
                    "cancelled" -> {
                        viewModel.setBadgeRemovalRequestCancelled(true)
                        binding.removeBadgeButton.isEnabled = true
                        binding.removeBadgeButton.text = getString(R.string.remove_badge)
                        binding.removeBadgeButton.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                    "declined" -> {
                        viewModel.setBadgeRemovalRequestCancelled(true)
                        binding.removeBadgeButton.isEnabled = true
                        binding.removeBadgeButton.text = getString(R.string.remove_badge)
                        binding.removeBadgeButton.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                }

            }
            is ProfileViewState.SuccessRequestBadgeRemoval -> { //request to remove badge
                hideLoadingDialog()
                viewModel.setBadgeRemovalRequestCancelled(false)
                requireActivity().toastSuccess(message = viewState.message)
                binding.removeBadgeButton.isEnabled = true
                binding.removeBadgeButton.text = getString(R.string.cancel_badge_removal_request)
                binding.removeBadgeButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.commons_lbl_highlight))
            }
            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                if (viewState.badge == "badge_removal"){
                    viewModel.setBadgeRemovalRequestCancelled(true)
                }else if(viewState.badge == "badge"){
                    adapter?.changeIsBadgeRequestedBefore(false)
                    binding.continueButton.isEnabled = true
                    binding.removeBadgeButton.isEnabled = false
                }else{
                    showPopupError(requireContext(), childFragmentManager, viewState.errorCode, viewState.message)
                }
            }
            else -> {
                hideLoadingDialog()
            }
        }
    }

    private fun setupList() {
        adapter = AccountTypeAdapter(requireContext(), this@SelectBadgeTypeFragment)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter

        accountTypeList = listOf(
            AccountTypeModel(
                getString(R.string.account_type_influencer_text),
                R.drawable.ic_thumbs_up
            ),
            AccountTypeModel(
                getString(R.string.account_type_public_servant_text),
                R.drawable.ic_public_servant
            ),
            AccountTypeModel(getString(R.string.account_type_npo_text), R.drawable.ic_npo)
        )

        adapter?.appendData(accountTypeList)
    }

    private fun setupClickListener() =  binding.run {
        viewModel.isBadgeRemovalRequestCancelled.observe(viewLifecycleOwner){
            removeBadgeButton.setOnSingleClickListener {
                if(viewModel.getUserKYC()  != "completed"){
                    //do not allow users kyc is not completed
                    requireActivity().toastWarning(getString(R.string.kyc_status_must_be_verified), 5000)
                }else{
                    if (viewModel.isBadgeRemovalRequestCancelled.value == true){ // badge removal should be enabled if status is cancelled
                        ReasonBottomSheetDialog.newInstance( callback = object : ReasonBottomSheetDialog.ReasonDialogCallback {
                            @SuppressLint("SetTextI18n")
                            override fun onRemoveBadgeButtonClicked(
                                dialog: ReasonBottomSheetDialog,
                                reason: String
                            ) {
                                viewModel.requestBadgeRemoval(request = BadgeRemovalRequest(reason = reason))

                                dialog.dismiss()
                            }
                        }).show(childFragmentManager, ReasonBottomSheetDialog.TAG)
                    } else{
                        viewModel.cancelRequestBadgeRemoval()
                    }
                }


            }
        }
        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if(hasSelected){
                findNavController().navigate(SelectBadgeTypeFragmentDirections.actionNavigationSelectAccountTypeToNavigationVerifyAccountType())
            }else{
                requireActivity().toastError(getString(R.string.account_type_error_message_text), CpmToast.LONG_DURATION)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if(hasSelected){
            val selectedPosition = activity.selectedPosition
            if (selectedPosition != RecyclerView.NO_POSITION) {
                adapter?.setSelectedPosition(selectedPosition)
            }
        }
    }

    override fun onItemClicked(data: AccountTypeModel, position: Int) {
        adapter?.setSelectedItem(data)
        activity.accountType = data.title.toString()
        activity.selectedPosition = position
        adapter?.setSelectedPosition(position)
        hasSelected = true
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null){
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(childFragmentManager)
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

}