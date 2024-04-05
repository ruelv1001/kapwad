package dswd.ziacare.app.ui.onboarding.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import dswd.ziacare.app.R
import dswd.ziacare.app.data.model.ErrorsData
import dswd.ziacare.app.databinding.ActivityForgotPasswordBinding
import dswd.ziacare.app.ui.onboarding.viewmodel.LoginViewModel
import dswd.ziacare.app.ui.onboarding.viewmodel.LoginViewState
import dswd.ziacare.app.utils.dialog.CommonDialog
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private var loadingDialog: CommonDialog? = null
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
        observeLogin()
        setView()
    }

    private fun setView() = binding.run{
        emailEditText.doOnTextChanged { text, start, before, count ->
            emailTextInputLayout.error = ""
        }
    }

    private fun setupClickListener() = binding.run{

        toolbar.setOnSingleClickListener {
            val intent = LoginActivity.getIntent(this@ForgotPasswordActivity)
            startActivity(intent)
        }

        proceedButton.setOnSingleClickListener {
            viewModel.doForgotPass(
                emailEditText.text.toString(),
            )
        }

    }

    private fun observeLogin() {
        lifecycleScope.launch {
            viewModel.loginSharedFlow.collect {
                handleViewState(it)
            }
        }
    }

    private fun handleViewState(viewState: LoginViewState) {
        when (viewState) {
            is LoginViewState.Loading -> showLoadingDialog(R.string.loading)
            is LoginViewState.SuccessForgotPassword -> {
                hideLoadingDialog()
                toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                this.finish()
            }

            is LoginViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    this@ForgotPasswordActivity,
                    supportFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is LoginViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }
            else->Unit
        }
    }
    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        if (errorsData.email?.get(0)?.isNotEmpty() == true) binding.emailTextInputLayout.error = errorsData.email?.get(0)
    }
    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ForgotPasswordActivity::class.java)
        }
    }
}