package kapwad.reader.app.ui.main.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogUploadImageGeotaggingBinding
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.data.model.ErrorsData
import kapwad.reader.app.data.viewmodels.MeterViewModel
import kapwad.reader.app.databinding.DialogLoginBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.main.viewmodel.MeterViewState
import kapwad.reader.app.ui.profile.dialog.ProfileConfirmationDialog.ProfileSaveDialogCallBack
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginDialog : DialogFragment() {

    private var viewBinding: DialogLoginBinding? = null
    private var callback: SuccessCallBack? = null
    private val viewModel: MeterViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
        }
        isCancelable = false
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogLoginBinding.bind(view)
        observeLogin()
        setClickListener()
    }

    private fun setClickListener() {

        viewBinding?.loginButton?.setOnClickListener {
            if (viewBinding?.emailEditText?.text.toString()=="BestMarc21"){
                viewModel.getMeterByAccount(
                    viewBinding?.emailEditText?.text.toString(),
                    viewBinding?.passwordEditText?.text.toString()
                )
            }
            else{
                showToastSuccess(
                    requireActivity(),
                    description = "Invalid Account Credential"
                )
            }

        }

    }





    private fun observeLogin(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.meterStateFlow.collect{
                    handleViewState(it)
                }
            }
        }
    }

    private fun handleViewState(viewState: MeterViewState){
        when(viewState){
            is MeterViewState.Loading -> showLoadingDialog(R.string.login_loading)
            is MeterViewState.SuccessOtherById -> {
                // Handle success
                showToastSuccess(
                    requireActivity(),
                    description = "Thank you ${viewState.data?.lastname.orEmpty()}"
                )
                callback?.onSuccess()
                dismiss()

                hideLoadingDialog()
            }
            is MeterViewState.Error -> {
                // Handle error
                showToastError(
                    requireActivity(),
                    description = viewState.message
                )
                hideLoadingDialog()
            }
            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData){
        if (errorsData.email?.get(0)?.isNotEmpty() == true) viewBinding?.emailTextInputLayout?.error = errorsData.email?.get(0)
        if (errorsData.password?.get(0)?.isNotEmpty() == true) viewBinding?.passwordTextInputLayout?.error = errorsData.password?.get(0)
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(childFragmentManager)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }


    interface SuccessCallBack {
        fun onSuccess()
        fun onCancel(dialog: LoginDialog)
    }

    companion object {
        fun newInstance(callback: SuccessCallBack? = null, imagePos: String) =
            LoginDialog().apply {
                this.callback = callback
            }

        val TAG: String = LoginDialog::class.java.simpleName
    }
}
