package kapwad.reader.app.ui.onboarding.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import kapwad.reader.app.R
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.databinding.ActivityLoginBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.onboarding.viewmodel.LoginViewModel
import kapwad.reader.app.ui.onboarding.viewmodel.LoginViewState
import kapwad.reader.app.ui.profile.activity.ProfileActivity
import kapwad.reader.app.ui.register.activity.RegisterActivity
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.utils.showToastSuccess
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