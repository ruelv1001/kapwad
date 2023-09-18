package com.lionscare.app.ui.register.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.databinding.FragmentRegistrationOtpBinding
import com.lionscare.app.ui.register.activity.RegisterActivity
import com.lionscare.app.ui.register.dialog.RegisterSuccessDialog
import com.lionscare.app.ui.register.viewmodel.RegisterViewModel
import com.lionscare.app.ui.register.viewmodel.RegisterViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.GenericKeyEvent
import com.lionscare.app.utils.GenericTextWatcher
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterOTPFragment: Fragment(), RegisterSuccessDialog.RegisterSuccessCallBack {
    private var _binding: FragmentRegistrationOtpBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as RegisterActivity }
    private var countDownTimer: CountDownTimer? = null
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistrationOtpBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOTP()
        setClickListeners()
        setView()
        onResume()
        startCountdown()
    }

    private fun setDoReqOTP() {
        viewModel.doRequestOTP(activity.otpModel)
    }

    private fun observeOTP() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: RegisterViewState) = binding.run{
        when (viewState) {
            is RegisterViewState.Loading -> showLoadingDialog(R.string.loading)
            is RegisterViewState.Success -> {
                hideLoadingDialog()
                val snackbar = Snackbar.make(binding.root, viewState.message, Snackbar.LENGTH_LONG)
                snackbar.setTextMaxLines(3)
                snackbar.view.translationY = -(binding.confirmButton.height + snackbar.view.height).toFloat()
                snackbar.show()
            }
            is RegisterViewState.SuccessReg -> {
                hideLoadingDialog()
                RegisterSuccessDialog.newInstance(callback = this@RegisterOTPFragment)
                    .show(childFragmentManager, RegisterSuccessDialog.TAG)
            }
            is RegisterViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            is RegisterViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }

            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.otp?.get(0)?.isNotEmpty() == true){
            Toast.makeText(requireActivity(),errorsData.otp?.get(0),Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as RegisterActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as RegisterActivity).hideLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_reg_otp))
    }


    private fun setView() = binding.run{
        activity.alignTitleToCenter()
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
    }

    private fun setClickListeners() = binding.run {
        confirmButton.setOnSingleClickListener {
            val modelReg = activity.requestModel
            modelReg.otp = "${otpFirstEdittext.text}${otpSecondEdittext.text}${otpThirdEdittext.text}${otpFourthEdittext.text}${otpFifthEdittext.text}${otpSixthEdittext.text}"
            viewModel.doReg(modelReg)
        }
        resendTextView.setOnSingleClickListener {
            startCountdown()
        }

        //remove backbbutton
        activity.removeBackButton()

        //remove hardware back button
        requireActivity().onBackPressedDispatcher.addCallback {
           null
        }

    }

    private fun startCountdown() = binding.run {
        setDoReqOTP()
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

    override fun onMyAccountClicked(cityName: String, citySku: String, zipCode: String) {
        findNavController().navigate(RegisterOTPFragmentDirections.actionNavigationCompleteProfile())
    }
}