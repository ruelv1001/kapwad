package da.farmer.app.ui.onboarding.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import da.farmer.app.R
import da.farmer.app.data.model.ErrorsData
import da.farmer.app.databinding.ActivityLoginBinding
import da.farmer.app.ui.main.activity.MainActivity
import da.farmer.app.ui.onboarding.viewmodel.LoginViewModel
import da.farmer.app.ui.onboarding.viewmodel.LoginViewState
import da.farmer.app.ui.profile.activity.ProfileActivity
import da.farmer.app.ui.register.activity.RegisterActivity
import da.farmer.app.utils.dialog.CommonDialog
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import da.farmer.app.utils.showToastSuccess
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loadingDialog: CommonDialog? = null
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
        observeLogin()
    }

    private fun setupClickListener() = binding.run{
        loginButton.setOnSingleClickListener {
            viewModel.doLoginAccount(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
        registerButton.setOnSingleClickListener {
            val intent = RegisterActivity.getIntent(this@LoginActivity)
            startActivity(intent)
        }
        resetPassTextView.setOnSingleClickListener {
            val intent = ForgotPasswordActivity.getIntent(this@LoginActivity)
            startActivity(intent)
        }
    }

    private fun observeLogin(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loginSharedFlow.collect{
                    handleViewState(it)
                }
            }
        }
    }

    private fun handleViewState(viewState: LoginViewState){
        when(viewState){
            is LoginViewState.Loading -> showLoadingDialog(R.string.login_loading)
            is LoginViewState.Success -> {
                hideLoadingDialog()
                toastSuccess(viewState.message, CpmToast.SHORT_DURATION)
                showToastSuccess(this, description = viewState.message)

                if(viewState.isCompleteProfile){
                    val intent = MainActivity.getIntent(this)
                    startActivity(intent)
                    finishAffinity()
                }else{
                    val intent = ProfileActivity.getIntent(this, true)
                    startActivity(intent)
                    finishAffinity()
                }
            }
            is LoginViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(this@LoginActivity, supportFragmentManager, viewState.errorCode, viewState.message)
            }
            is LoginViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }
            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData){
        if (errorsData.email?.get(0)?.isNotEmpty() == true) binding.emailTextInputLayout.error = errorsData.email?.get(0)
        if (errorsData.password?.get(0)?.isNotEmpty() == true) binding.passwordTextInputLayout.error = errorsData.password?.get(0)
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
            return Intent(context, LoginActivity::class.java)
        }
    }
}