package dswd.ziacare.app.ui.accountcontrol.fragment

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
import dswd.ziacare.app.ui.accountcontrol.activity.AccountControlActivity
import dswd.ziacare.app.ui.accountcontrol.viewmodel.AccountControlViewModel
import dswd.ziacare.app.ui.accountcontrol.viewmodel.AccountControlViewState
import dagger.hilt.android.AndroidEntryPoint
import dswd.ziacare.app.R
import dswd.ziacare.app.databinding.FragmentAccountControlOtpBinding
import dswd.ziacare.app.security.AuthEncryptedDataManager
import dswd.ziacare.app.ui.onboarding.activity.LoginActivity
import dswd.ziacare.app.utils.GenericKeyEvent
import dswd.ziacare.app.utils.GenericTextWatcher
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dswd.ziacare.app.utils.showToastSuccess
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
        observeOTP()
        setOnClickListeners()
        setView()
        startCountdown()
    }

    private fun observeOTP(){
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
            is AccountControlViewState.Loading -> showLoadingDialog(R.string.loading)
            is AccountControlViewState.SuccessClearLocalData -> {
                hideLoadingDialog()
                showToastSuccess(requireActivity(), description = "Successfully Deactivated/Deleted Your Account");

                val intent = LoginActivity.getIntent(requireActivity())
                startActivity(intent)
                requireActivity().finishAffinity()
            }
            is AccountControlViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            else -> hideLoadingDialog()

        }
    }
    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as AccountControlActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as AccountControlActivity).hideLoadingDialog()
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

    private fun setOnClickListeners() = binding.run {
        confirmButton.setOnSingleClickListener {
//            val modelReg = activity.requestModel
//            modelReg.otp = "${otpFirstEdittext.text}${otpSecondEdittext.text}${otpThirdEdittext.text}${otpFourthEdittext.text}${otpFifthEdittext.text}${otpSixthEdittext.text}"
//            viewModel.doReg(modelReg)
            //tODO
            AuthEncryptedDataManager().clearUserInfo()
            showToastSuccess(requireActivity(), description = "Succesfully Deactivated/Deleted Your Account");
            val intent = LoginActivity.getIntent(requireActivity())
            startActivity(intent)
            requireActivity().finishAffinity()
        }
        resendTextView.setOnSingleClickListener {
            startCountdown()
        }
    }

    private fun startCountdown() = binding.run {
//        setDoReqOTP()
        resendTextView.visibility = View.GONE
        resendTimeLinearLayout.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(MILLI_SEC, COUNTDOWN_INTERVAL) {
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

    companion object {
        private const val MILLI_SEC = 60000L
        private const val COUNTDOWN_INTERVAL = 1000L
    }
}
