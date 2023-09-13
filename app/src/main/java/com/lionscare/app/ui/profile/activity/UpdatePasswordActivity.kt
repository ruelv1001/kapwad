package com.lionscare.app.ui.profile.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lionscare.app.R
import com.lionscare.app.data.model.ErrorsData
import com.lionscare.app.databinding.ActivityUpdatePasswordBinding
import com.lionscare.app.ui.main.viewmodel.SettingsViewModel
import com.lionscare.app.ui.main.viewmodel.SettingsViewState
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding
    private var loadingDialog: CommonDialog? = null
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClickListener()
        observeSettings()
    }

    private fun setClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        continueButton.setOnSingleClickListener {
            viewModel.doChangePass(
                oldPasswordEditText.text.toString(),
                newPasswordEditText.text.toString(),
                confirmPasswordEditText.text.toString()
            )
        }

        oldPasswordEditText.doOnTextChanged { text, start, before, count ->
            oldPasswordTextInputLayout.error = ""
            oldPasswordEditText.setError(null,null)
        }
        newPasswordEditText.doOnTextChanged { text, start, before, count ->
            newPasswordTextInputLayout.error = ""
            newPasswordEditText.setError(null,null)
        }
        confirmPasswordEditText.doOnTextChanged { text, start, before, count ->
            confirmPasswordTextInputLayout.error = ""
            confirmPasswordEditText.setError(null,null)
        }
    }

    private fun observeSettings() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: SettingsViewState) {
        when (viewState) {
            is SettingsViewState.Loading -> showLoadingDialog(R.string.change_pass_loading)
            is SettingsViewState.Success -> {
                hideLoadingDialog()
                Toast.makeText(this, viewState.message, Toast.LENGTH_SHORT).show()
                this.finish()
            }

            is SettingsViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    this,
                    supportFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is SettingsViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }

            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData) = binding.run {
        val drawableError =
            AppCompatResources.getDrawable(this@UpdatePasswordActivity, R.drawable.ic_error)
        drawableError?.setBounds(
            -60,
            0,
            drawableError.intrinsicWidth - 55,
            drawableError.intrinsicHeight
        )

        if (errorsData.password?.get(0)?.isNotEmpty() == true) binding.newPasswordEditText.setError(
            errorsData.password?.get(0),
            drawableError
        )
        if (errorsData.current_password?.get(0)
                ?.isNotEmpty() == true
        ) binding.oldPasswordEditText.setError(errorsData.current_password?.get(0), drawableError)
        if (errorsData.password_confirmation?.get(0)
                ?.isNotEmpty() == true
        ) binding.confirmPasswordEditText.setError(
            errorsData.password_confirmation?.get(0),
            drawableError
        )
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(supportFragmentManager)
        }
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
            return Intent(context, UpdatePasswordActivity::class.java)
        }
    }
}