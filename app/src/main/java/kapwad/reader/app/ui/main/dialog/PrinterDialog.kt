package kapwad.reader.app.ui.main.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.R
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.data.model.PrinterModel
import kapwad.reader.app.data.viewmodels.MeterViewModel
import kapwad.reader.app.databinding.DialogPrinterBinding
import kapwad.reader.app.ui.main.adapter.PrinterAdapter
import kapwad.reader.app.utils.dialog.CommonDialog

@AndroidEntryPoint
class PrinterDialog : DialogFragment(),PrinterAdapter.ConsumerCallback {

    private var viewBinding: DialogPrinterBinding? = null
    private var callback: SuccessCallBack? = null
    private val viewModel: MeterViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var printerDialog: PrinterDialog? = null


    private var adapter: PrinterAdapter ? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_printer, container, false)
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
        viewBinding = DialogPrinterBinding.bind(view)
        setupList()
        setClickListener()
    }

    private fun setupList() {
        viewBinding?.apply {
            adapter = PrinterAdapter(requireActivity(), this@PrinterDialog)
            linearLayoutManager = LinearLayoutManager(requireActivity())
            viewBinding?.printerListRecyclerView?.layoutManager = linearLayoutManager
            printerListRecyclerView.adapter = adapter
            val printerModel = listOf(
                PrinterModel(
                    "Printer 1",
                    "86:67:7A:E6:54:76" ,
                    "Active",
                    ),
                PrinterModel(
                    "Printer 2",
                    "86:67:7A:E6:54:76" ,
                    "Active",
                ),

            )
            adapter?.appendData(printerModel)
        }
    }

    private fun setClickListener() {
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
        fun onSuccess(mymac:String, dialog: PrinterDialog?)
        fun onCancel(dialog: PrinterDialog)
    }

    companion object {
        fun newInstance(callback: SuccessCallBack? = null, imagePos: String) =
            PrinterDialog().apply {
                this.callback = callback
            }

        val TAG: String = PrinterDialog::class.java.simpleName
    }

    override fun onItemClicked(data: PrinterModel, position: Int) {
        callback?.onSuccess(data.mac.toString(),printerDialog)
        dismiss()
    }
}
