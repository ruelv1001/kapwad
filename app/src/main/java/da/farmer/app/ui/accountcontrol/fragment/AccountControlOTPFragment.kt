package da.farmer.app.ui.accountcontrol.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import da.farmer.app.ui.accountcontrol.activity.AccountControlActivity
import da.farmer.app.ui.accountcontrol.viewmodel.AccountControlViewModel
import da.farmer.app.ui.accountcontrol.viewmodel.AccountControlViewState
import dagger.hilt.android.AndroidEntryPoint
import da.farmer.app.R
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.databinding.FragmentAccountControlOtpBinding
import da.farmer.app.security.AuthEncryptedDataManager
import da.farmer.app.ui.onboarding.activity.LoginActivity
import da.farmer.app.utils.GenericKeyEvent
import da.farmer.app.utils.GenericTextWatcher
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.showPopupError
import da.farmer.app.utils.showToastError
import da.farmer.app.utils.showToastSuccess
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountControlOTPFragment:  Fragment() {
    private var _binding: FragmentAccountControlOtpBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as AccountControlActivity }
    private var countDownTimer: CountDownTimer? = null
    private val viewModel : AccountControlViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAccountControlOtpBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDeleteOrDeactivate()
        setupClickListener()
        setView()
        startCountdown()
    }
    private fun setupClickListener() = binding.run{
        confirmButton.setOnSingleClickListener {
            val otp = "${otpFirstEdittext.text}${otpSecondEdittext.text}${otpThirdEdittext.text}${otpFourthEdittext.text}${otpFifthEdittext.text}${otpSixthEdittext.text}"
            viewModel.deleteOrDeactivateAccountOTP(reasonId = activity.reasonId.toString(), other = activity.reason.toString(), type = activity.selectedChoice.toString(), otp = otp)
        }
        resendTextView.setOnSingleClickListener {
            viewModel.deleteOrDeactivateAccount(reasonId = activity.reasonId.toString(), other = activity.reason.toString(), type = activity.selectedChoice.toString())
        }

    }

    private fun startCountdown() = binding.run {
        resendTextView.visibility = View.GONE
        resendTimeLinearLayout.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(
            MILLI_SEC,
            COUNTDOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val sourceString = ""+ millisUntilFinished / COUNTDOWN_INTERVAL +""
                timeTextView.text = HtmlCompat.fromHtml(sourceString,
                    HtmlCompat.FROM_HTML_MODE_COMPACT)
            }

            override fun onFinish() {
                resendTextView.visibility = View.VISIBLE
                resendTimeLinearLayout.visibility = View.GONE
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeDeleteOrDeactivate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accountControlSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }
    private fun handleViewState(viewState: AccountControlViewState) {
        when (viewState) {
            is AccountControlViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is AccountControlViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )

            }

            is AccountControlViewState.InputError ->{
                activity.hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }
            is AccountControlViewState.SuccessDeleteOrDeactivateAccountOTP -> {
                activity.hideLoadingDialog()
                showToastSuccess(requireActivity(), description = viewState.response.msg.toString())
                val intent = LoginActivity.getIntent(requireActivity())
                startActivity(intent)
                requireActivity().finishAffinity()
            }
            is AccountControlViewState.SuccessDeleteOrDeactivateAccount -> {
                activity.hideLoadingDialog()
                showToastSuccess(requireActivity(), description = viewState.response.msg.toString())
                startCountdown()
            }
            else -> activity.hideLoadingDialog()
        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.otp?.get(0)?.isNotEmpty() == true)
            showToastError(requireActivity(), description = "Invalid OTP")
    }

    override fun onResume() {
        super.onResume()
        activity.setToolbarTitle(getString(R.string.lbl_reg_otp))
    }


    private fun setView() = binding.run{
        //GenericTextWatcher here works only for moving to next EditText when a number is entered
//first parameter is the current EditText and second parameter is next EditText
        otpFirstEdittext.addTextChangedListener(GenericTextWatcher(otpFirstEdittext, otpSecondEdittext))
        otpSecondEdittext.addTextChangedListener(GenericTextWatcher(otpSecondEdittext, otpThirdEdittext))
        otpThirdEdittext.addTextChangedListener(GenericTextWatcher(otpThirdEdittext, otpFourthEdittext))
        otpFourthEdittext.addTextChangedListener(GenericTextWatcher(otpFourthEdittext, otpFifthEdittext))
        otpFifthEdittext.addTextChangedListener(GenericTextWatcher(otpFifthEdittext, otpSixthEdittext))
        otpSixthEdittext.addTextChangedListener(GenericTextWatcher(otpSixthEdittext, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
//first parameter is the current EditText and second parameter is previous EditText
        otpFirstEdittext.setOnKeyListener(GenericKeyEvent(otpFirstEdittext, null))
        otpSecondEdittext.setOnKeyListener(GenericKeyEvent(otpSecondEdittext, otpFirstEdittext))
        otpThirdEdittext.setOnKeyListener(GenericKeyEvent(otpThirdEdittext, otpSecondEdittext))
        otpFourthEdittext.setOnKeyListener(GenericKeyEvent(otpFourthEdittext,otpThirdEdittext))
        otpFifthEdittext.setOnKeyListener(GenericKeyEvent(otpFifthEdittext,otpFourthEdittext))
        otpSixthEdittext.setOnKeyListener(GenericKeyEvent(otpSixthEdittext,otpFifthEdittext))

//        phoneNumberTextView.text = activity.otpModel.phone_number
    }


    companion object {
        private const val MILLI_SEC = 60000L
        private const val COUNTDOWN_INTERVAL = 1000L
    }
}

