package com.lionscare.app.ui.settings.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberOTPRequest
import com.lionscare.app.data.repositories.profile.request.UpdatePhoneNumberRequest
import com.lionscare.app.databinding.FragmentRegistrationOtpBinding
import com.lionscare.app.ui.settings.activity.ProfileActivity
import com.lionscare.app.ui.settings.viewmodel.ProfileViewModel
import com.lionscare.app.ui.settings.viewmodel.ProfileViewState
import com.lionscare.app.utils.CommonLogger
import com.lionscare.app.utils.GenericKeyEvent
import com.lionscare.app.utils.GenericTextWatcher
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileOTPFragment: Fragment(){
    private var _binding: FragmentRegistrationOtpBinding? = null
    private val binding get() = _binding!!
    private var countDownTimer: CountDownTimer? = null
    private val viewModel: ProfileViewModel by viewModels()
    val args: ProfileOTPFragmentArgs by navArgs()
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
        requireActivity().setTitle(R.string.lbl_reg_otp)
        observeOTP()
        setClickListeners()
        setView()
        startCountdown()
    }

    private fun sendOtpToUser() {
        viewModel.changePhoneNumber(UpdatePhoneNumberRequest(phone_number = args.phone))
    }

    private fun observeOTP() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileSharedFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: ProfileViewState) = binding.run{
        when (viewState) {
            is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
            is ProfileViewState.SuccessUpdatePhoneNumber -> {
                hideLoadingDialog()
                val snackbar = Snackbar.make(binding.root, viewState.response.msg.toString(), Snackbar.LENGTH_LONG)
                snackbar.setTextMaxLines(3)
                snackbar.view.translationY = -(binding.confirmButton.height + snackbar.view.height).toFloat()
                snackbar.show()
            }
            is ProfileViewState.SuccessUpdatePhoneNumberWithOTP -> {
                hideLoadingDialog()
                val snackbar = Snackbar.make(binding.root, viewState.response.msg.toString(), Snackbar.LENGTH_LONG)
                snackbar.setTextMaxLines(3)
                snackbar.show()
                findNavController().popBackStack()
            }
            is ProfileViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message)
            }
            is ProfileViewState.InputError -> {
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
        if (errorsData.phone_number?.get(0)?.isNotEmpty() == true){
            Toast.makeText(requireActivity(),errorsData.phone_number?.get(0),Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as ProfileActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as ProfileActivity).hideLoadingDialog()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setTitle(R.string.lbl_reg_otp)
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
    }

    private fun setClickListeners() = binding.run {
        confirmButton.setOnSingleClickListener {
            val request = UpdatePhoneNumberOTPRequest(
                phone_number = args.phone,
                otp =  "${otpFirstEdittext.text}${otpSecondEdittext.text}${otpThirdEdittext.text}${otpFourthEdittext.text}${otpFifthEdittext.text}${otpSixthEdittext.text}"
            )
            viewModel.changePhoneNumberWithOTP(request)
        }
        resendTextView.setOnSingleClickListener {
            //only send when resend is clicked and after initial timeout since it was send prior to going to this screen
            sendOtpToUser()
            startCountdown()
        }
    }

    private fun startCountdown() = binding.run {
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