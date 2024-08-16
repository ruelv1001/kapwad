package da.farmer.app.ui.main.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import da.farmer.app.R
import da.farmer.app.data.repositories.baseresponse.UserModel
import da.farmer.app.data.repositories.group.response.GroupListData
import da.farmer.app.databinding.FragmentHomeBinding
import da.farmer.app.ui.group.activity.GroupActivity
import da.farmer.app.ui.group.activity.GroupDetailsActivity
import da.farmer.app.ui.main.activity.MainActivity
import da.farmer.app.ui.main.adapter.GroupsYourGroupAdapter
import da.farmer.app.ui.main.viewmodel.ImmediateFamilyViewModel
import da.farmer.app.ui.main.viewmodel.ImmediateFamilyViewState
import da.farmer.app.ui.main.viewmodel.SettingsViewModel
import da.farmer.app.ui.main.viewmodel.SettingsViewState
import da.farmer.app.utils.copyToClipboard
import da.farmer.app.utils.loadAvatar
import da.farmer.app.utils.loadGroupAvatar
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.setQR
import da.farmer.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener {
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
        binding.swipeRefreshLayout.setOnRefreshListener(this@HomeFragment)

        //immediately make this view gone
        //not done in xml as this is a reused layout
        //Reason: so it wont show for a second after finishin api call
        binding.mainLayout.includeBadgeLayout.accountTypeLinearLayout. visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        hideLoadingDialog()
        //call profile  again to check if correct badge and kyc or get updated one
        viewModel.getProfileDetails()
        iFViewModel.getImmediateFamily()
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
                binding.swipeRefreshLayout.isRefreshing = false
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
                //***** -VON
                //response of badge status returns an error of not found
                //so always remove the views if this gets triggered
                //since it'll always get overridden if the popup error comes from another api call
                // this is for if it returned not found then do the ff.
                binding.mainLayout.badgeImageView.visibility = View.GONE //remove badge on picture as it is still pending
                binding.mainLayout.includeBadgeLayout.accountTypeLinearLayout.visibility = View.GONE //remove whole badge
                binding.mainLayout.badgeImageView.visibility = View.GONE //dont show badge on picture as it is still pending
                binding.mainLayout.badgeIdStatus.visibility = View.GONE //badge gone
                binding.mainLayout.badgeIdStatus.visibility = View.GONE // bdage pill dont show
            }

            is SettingsViewState.InputError -> Unit
            is SettingsViewState.SuccessGetUserInfo -> {
                hideLoadingDialog()
                //Only get badge status after getting success info
                viewModel.userKycStatus = viewState.userModel?.kyc_status.toString()
                setView(viewState.userModel)
                binding.swipeRefreshLayout.isRefreshing = false
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
            ImmediateFamilyViewState.Loading -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isVisible = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }
            is ImmediateFamilyViewState.PopupError -> {
                binding.createGroupButton.isVisible = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isGone = true
            }

            is ImmediateFamilyViewState.Success -> {
                binding.createGroupButton.isGone = true
                binding.famShimmerLayout.isGone = true
                binding.immediateFamilyLayout.adapterLinearLayout.isVisible = true

                viewState.immediateFamilyResponse?.data?.let { setImmediateFamily(it) }
                immediateFamilyId = viewState.immediateFamilyResponse?.data?.id.toString()
            }

            else -> Unit
        }
    }

    private fun setImmediateFamily(data: GroupListData) = binding.run{
        immediateFamilyLayout.imageView.loadGroupAvatar(data.avatar?.thumb_path)
        immediateFamilyLayout.titleTextView.text = data.name
        immediateFamilyLayout.membersTextView.text = if ((data.member_count ?: 0) > 1) {
            "${data.member_count} members"
        } else {
            "${data.member_count} member"
        }
        immediateFamilyLayout.referenceTextView.text = data.qrcode
    }

    @SuppressLint("SetTextI18n")
    private fun setView(userModel: UserModel?) = binding.run {
        //main
        mainLayout.profileImageView.loadAvatar(userModel?.avatar?.thumb_path, requireContext())
        mainLayout.nameTextView.text = userModel?.name
        mainLayout.idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
        mainLayout.dateIssuedTextView.text = userModel?.date_registered?.date_only
        mainLayout.dateOfBirthTextView.text = userModel?.birthdate?.date_only_ph

        //qr
        qrLayout.qrCodeTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
        qrLayout.qrImageView.setImageBitmap(setQR(requireActivity(),userModel?.qrcode_value.toString()))

        //id
        idLayout.nameTextView.text = userModel?.name
        idLayout.idNoTextView.text = userModel?.qrcode?.replace("....".toRegex(), "$0 ")
        idLayout.qrImageView.setImageBitmap(setQR(requireActivity(), userModel?.qrcode_value))
        idLayout.dateIssuedTextView.text = userModel?.date_registered?.date_only
        idLayout.profileImageView.loadAvatar(userModel?.avatar?.thumb_path, requireContext())
        idLayout.dateOfBirthTextView.text = userModel?.birthdate?.date_only_ph

        if (userModel?.street_name?.isNotEmpty() == true) {
            mainLayout.addressTextView.text =
                "${userModel.street_name}, ${userModel.brgy_name},\n${userModel.city_name}, ${userModel.province_name}"
            idLayout.addressTextView.text =
                "${userModel.street_name}, ${userModel.brgy_name},\n${userModel.city_name}, ${userModel.province_name}"
        }
        viewModel.userQrCode = userModel?.qrcode.orEmpty()
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }

    private fun setUpAnimation() = binding.run{
        frontAnim = AnimatorInflater.loadAnimator(
            requireContext(),
            R.animator.front_animator
        ) as AnimatorSet
        backAnim =
            AnimatorInflater.loadAnimator(requireContext(), R.animator.back_animator) as AnimatorSet

        val scale = resources.displayMetrics.density * 8000
        mainLayout.mainLinearLayout.cameraDistance = scale
        idLayout.virtualIdLinearLayout.cameraDistance = scale
        qrLayout.qrLinearLayout.cameraDistance = scale
    }

    private fun setClickListeners() = binding.run {
        mainLayout.myIdImageView.setOnSingleClickListener {
            qrLayout.qrLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.setTarget(idLayout.virtualIdLinearLayout)
            frontAnim?.start()
            backAnim?.start()
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE
            mainLayout.mainLinearLayout.visibility = View.GONE
        }

        idLayout.myMainLayout.setOnSingleClickListener {
            qrLayout.qrLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            mainLayout.mainLinearLayout.visibility = View.VISIBLE
            idLayout.virtualIdLinearLayout.visibility = View.GONE
        }

        mainLayout.myQrImageView.setOnSingleClickListener {
            idLayout.virtualIdLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
            mainLayout.mainLinearLayout.visibility = View.GONE
        }

        idLayout.myQrImageView.setOnSingleClickListener {
            mainLayout.mainLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.setTarget(qrLayout.qrLinearLayout)
            frontAnim?.start()
            backAnim?.start()
            qrLayout.qrLinearLayout.visibility = View.VISIBLE
            idLayout.virtualIdLinearLayout.visibility = View.GONE
        }

        qrLayout.myIdImageView.setOnSingleClickListener {
            mainLayout.mainLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.setTarget(idLayout.virtualIdLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            idLayout.virtualIdLinearLayout.visibility = View.VISIBLE
            qrLayout.qrLinearLayout.visibility = View.GONE
        }

        qrLayout.myMainLayout.setOnSingleClickListener {
            idLayout.virtualIdLinearLayout.visibility = View.GONE
            frontAnim?.setTarget(qrLayout.qrLinearLayout)
            backAnim?.setTarget(mainLayout.mainLinearLayout)
            backAnim?.start()
            frontAnim?.start()
            mainLayout.mainLinearLayout.visibility = View.VISIBLE
            qrLayout.qrLinearLayout.visibility = View.GONE
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

    override fun onRefresh() {
        viewModel.getProfileDetails()
    }
}