package kapwad.reader.app.ui.main.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogProfileConfirmationBinding
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.data.viewmodels.MeterViewModel
import kapwad.reader.app.databinding.DialogLoginBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.main.viewmodel.MeterViewState
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginTempDialog : BottomSheetDialogFragment(){

    private var viewBinding: DialogLoginBinding? = null
    private var callback: SuccessCallBack? = null
    private val viewModel: MeterViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_login,
            container,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
        isCancelable = false
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                super.onBackPressed()
                // Do nothing to disable back press
            }
        }.apply {
            setCanceledOnTouchOutside(false) // disables outside touch
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogLoginBinding.bind(view)
        setClickListener()
        setView()
        observeLogin()
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

    private fun setView() = viewBinding?.run {

    }

    private fun setClickListener() {
        viewBinding?.returnButton?.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface SuccessCallBack {
        fun onSuccess()
        fun onCancel(dialog: LoginTempDialog)
    }

    companion object {
        fun newInstance(callback: SuccessCallBack? = null) = LoginTempDialog()
            .apply {
                this.callback = callback
            }

        val TAG: String = LoginTempDialog::class.java.simpleName
    }
}