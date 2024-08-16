package da.farmer.app.ui.verify.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import da.farmer.app.R
import da.farmer.app.databinding.FragmentChooseKycProcessBinding
import da.farmer.app.ui.profile.viewmodel.ProfileViewState
import da.farmer.app.ui.verify.VerifyViewModel
import da.farmer.app.ui.verify.activity.AccountVerificationActivity
import da.farmer.app.utils.PopupErrorState
import da.farmer.app.utils.dialog.CommonDialog
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.showPopupError
import kotlinx.coroutines.launch

class ChooseKYCProcessFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener  {

    private var _binding: FragmentChooseKycProcessBinding? = null
    private val binding get() = _binding!!
    private var isIdVerified : Boolean? = false
    private var isAddressVerified : Boolean? = false
    private val activity by lazy { requireActivity() as AccountVerificationActivity }

    private val viewModel : VerifyViewModel by activityViewModels()
    private var loadingDialog: CommonDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseKycProcessBinding.inflate(
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
//        setUpDetails()\
        //show loading here immediately
        showLoadingDialog(R.string.loading)
        viewModel.getVerificationStatus()
        binding.swipeRefreshLayout.setOnRefreshListener(this@ChooseKYCProcessFragment)
    }



    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.kycSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleViewState(viewState: ProfileViewState) {
        when (viewState) {
            is ProfileViewState.LoadingVerificationStatus -> {
                showLoadingDialog(R.string.loading)
            }
            is ProfileViewState.SuccessGetVerificationStatus -> {
                binding.swipeRefreshLayout.isRefreshing = false

                //if all kyc is completed, update sharedpref value
                if (viewState.profileVerificationResponse.data?.id_status == "approved" &&
                    viewState.profileVerificationResponse.data?.address_status  == "approved" &&
                    viewState.profileVerificationResponse.data?.facial_id_status == "approved"){
                    viewModel.updateKYCStatus("completed")
                }else{
                    viewModel.updateKYCStatus("pending")
                }

                val idStatus = "Valid ID"
                val idDate =  "Submitted on: \n${viewState.profileVerificationResponse.data?.id_submitted_date?.date_db?.ifEmpty { "Not applicable" }}"

                val addressStatus = "Proof of Address"
                val addressDate =  "Submitted on: \n${viewState.profileVerificationResponse.data?.address_submitted_date?.date_db?.ifEmpty { "Not applicable" }}"


                val facialStatus = "Facial Identification"
                val facialDate =  "Submitted on: \n${viewState.profileVerificationResponse.data?.facial_id_submitted_date?.date_db?.ifEmpty { "Not applicable" }}"

                //for id
                binding.badgeIdStatus.text = "${viewState.profileVerificationResponse.data?.id_status?.replaceFirstChar(Char::titlecase)}"
                binding.validIdButtonTextView.text = idStatus
                binding.validIdDateTextView.text = idDate

                //for address
                binding.badgeAddressStatus.text = "${viewState.profileVerificationResponse.data?.address_status?.replaceFirstChar(Char::titlecase)}"
                binding.addressButtonTextView.text = addressStatus
                binding.addressDateTextView.text = addressDate

                //for facial
                binding.badgeFacialStatus.text = "${viewState.profileVerificationResponse.data?.facial_id_status?.replaceFirstChar(Char::titlecase)}"
                binding.facialButtonTextView.text = facialStatus
                binding.facialDateTextView.text = facialDate

                when( viewState.profileVerificationResponse.data?.id_status ){
                    "pending" -> {
                        binding.badgeIdStatus.setBackgroundResource(R.drawable.bg_rounded_pending)
                        binding.badgeIdStatus.visibility = View.VISIBLE
                        binding.validIdLinearLayout.isClickable = false
                        binding.validIdDateTextView.visibility = View.VISIBLE
                        binding.idArrowImageView.visibility = View.GONE
                    }
                    "declined" ->{
                        binding.badgeIdStatus.setBackgroundResource(R.drawable.bg_rounded_declined)
                        binding.badgeIdStatus.visibility = View.VISIBLE
                        binding.badgeIdStatus.text = viewState.profileVerificationResponse.data.id_status.replaceFirstChar(Char::titlecase)
                        binding.validIdLinearLayout.isClickable = true
                        binding.validIdButtonTextView.text = idStatus
                        binding.validIdDateTextView.text = idDate
                        binding.idArrowImageView.visibility = View.VISIBLE
                    }
                    "approved" -> {
                        binding.idArrowImageView.visibility = View.VISIBLE
                        binding.badgeIdStatus.visibility = View.GONE
                        binding.validIdLinearLayout.isClickable = false
                        binding.validIdDateTextView.text = "Verified on:\n${viewState.profileVerificationResponse.data.id_verified_date?.date_db?.ifEmpty { "Not applicable" }}"   /// activated date
                        binding.validIdDateTextView.visibility = View.VISIBLE
                        binding.idArrowImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_check))
                    }
                    else -> {
                        binding.badgeIdStatus.visibility = View.GONE
                        binding.validIdLinearLayout.isClickable = true
                        binding.validIdButtonTextView.text = getString(R.string.account_verification_valid_id_text)
                        binding.validIdDateTextView.visibility = View.GONE
                        binding.idArrowImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_forward_arrow))
                    }
                }
                //for address
                when( viewState.profileVerificationResponse.data?.address_status ){
                    "pending" -> {
                        binding.badgeAddressStatus.setBackgroundResource(R.drawable.bg_rounded_pending)
                        binding.badgeAddressStatus.visibility = View.VISIBLE
                        binding.addressLinearLayout.isClickable = false
                        binding.addressDateTextView.visibility = View.VISIBLE
                        binding.addressArrowImageView.visibility = View.GONE
                    }
                    "declined" ->{
                        binding.badgeAddressStatus.setBackgroundResource(R.drawable.bg_rounded_declined)
                        binding.badgeAddressStatus.visibility = View.VISIBLE
                        binding.badgeAddressStatus.text = viewState.profileVerificationResponse.data.address_status.replaceFirstChar(Char::titlecase)
                        binding.addressLinearLayout.isClickable = true
                        binding.addressButtonTextView.text = addressStatus
                        binding.addressDateTextView.visibility = View.GONE
                        binding.addressArrowImageView.visibility = View.VISIBLE
                    }
                    "approved" -> {
                        binding.addressArrowImageView.visibility = View.VISIBLE
                        binding.badgeAddressStatus.visibility = View.GONE
                        binding.addressLinearLayout.isClickable = false
                        binding.addressDateTextView.text =  "Verified on:\n${viewState.profileVerificationResponse.data.address_verified_date?.date_db?.ifEmpty { "Not applicable" }}"   /// activated date
                        binding.addressDateTextView.visibility = View.VISIBLE
                        binding.addressArrowImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_check))
                    }
                    else -> {
                        binding.badgeAddressStatus.visibility = View.GONE
                        binding.addressLinearLayout.isClickable = true
                        binding.addressButtonTextView.text = getString(R.string.account_verification_proof_of_address_title)
                        binding.addressDateTextView.visibility = View.GONE
                        binding.addressArrowImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_forward_arrow))
                    }
                }
                //for facial TODO
                when( viewState.profileVerificationResponse.data?.facial_id_status) {
                    "pending" -> {
                        binding.badgeFacialStatus.setBackgroundResource(R.drawable.bg_rounded_pending)
                        binding.badgeFacialStatus.visibility = View.VISIBLE
                        binding.facialLinearLayout.isClickable = false
                        binding.facialDateTextView.visibility = View.VISIBLE
                        binding.facialArrowImageView.visibility = View.GONE
                    }
                    "declined" ->{
                        binding.badgeFacialStatus.setBackgroundResource(R.drawable.bg_rounded_declined)
                        binding.badgeFacialStatus.visibility = View.VISIBLE
                        binding.badgeFacialStatus.text = viewState.profileVerificationResponse.data.facial_id_status.replaceFirstChar(Char::titlecase)
                        binding.facialLinearLayout.isClickable = true
                        binding.facialButtonTextView.text = facialStatus
                        binding.facialDateTextView.visibility = View.GONE
                        binding.facialArrowImageView.visibility = View.VISIBLE
                    }
                    "approved" -> {
                        binding.facialArrowImageView.visibility = View.VISIBLE
                        binding.badgeFacialStatus.visibility = View.GONE
                        binding.facialLinearLayout.isClickable = false
                        binding.facialDateTextView.text =  "Verified on:\n${viewState.profileVerificationResponse.data.facial_id_verified_date?.date_db?.ifEmpty { "Not applicable" }}"   /// activated date
                        binding.facialDateTextView.visibility = View.VISIBLE
                        binding.facialArrowImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_check))
                    }
                    else -> {
                        binding.badgeFacialStatus.visibility = View.GONE
                        binding.facialLinearLayout.isClickable = true
                        binding.facialButtonTextView.text = getString(R.string.facial_identification)
                        binding.facialDateTextView.visibility = View.GONE
                        binding.facialArrowImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_forward_arrow))
                    }
                }
                hideLoadingDialog()

            }
            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                //do not show popup on first run or if nothing was sent yet that was part of kyc
                //based on api flow
                if ( viewState.errorCode != PopupErrorState.HttpError && viewState.message != "Record not found."){
                    showPopupError(requireContext(), childFragmentManager, viewState.errorCode, viewState.message)
                }
            }
            else ->  hideLoadingDialog()
        }
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


//    private fun setUpDetails() = binding.run{
//        activity.setTitle(getString(R.string.verify_account_title))
//
//        if(isIdVerified == true){
//            idArrowImageView.visibility = View.GONE
//            idCheckImageView.visibility = View.VISIBLE
//        }
//        if(isAddressVerified == true){
//            addressArrowImageView.visibility = View.GONE
//            addressCheckImageView.visibility = View.VISIBLE
//        }
//    }

    private fun setupClickListener() = binding.run {
        validIdLinearLayout.setOnSingleClickListener {
            findNavController().navigate(ChooseKYCProcessFragmentDirections.actionNavigationChooseKycToNavigationUploadId())
        }

        addressLinearLayout.setOnSingleClickListener {
            findNavController().navigate(ChooseKYCProcessFragmentDirections.actionNavigationChooseKycToNavigationProofOfAddress())
        }

        facialLinearLayout.setOnSingleClickListener {
            findNavController().navigate(ChooseKYCProcessFragmentDirections.actionNavigationChooseKycToUploadSelfieFragment())
        }
    }
    override fun onRefresh() {
        viewModel.getVerificationStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}