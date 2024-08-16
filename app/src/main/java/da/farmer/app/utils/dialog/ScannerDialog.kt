package da.farmer.app.utils.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.zxing.Result
import da.farmer.app.databinding.DialogScannerViewBinding
import da.farmer.app.R
import da.farmer.app.utils.CommonLogger
import da.farmer.app.utils.PermissionChecker
import da.farmer.app.utils.setOnSingleClickListener
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerDialog: DialogFragment(), ZXingScannerView.ResultHandler {

    private var viewBinding: DialogScannerViewBinding? = null
    private var callback: ScannerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_scanner_view,
            container,
            false
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogScannerViewBinding.bind(view)
        setClickListener()
    }

    private fun setClickListener(){
        viewBinding?.dismissImageView?.setOnSingleClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionChecker.checkPermissions(requireActivity(), Manifest.permission.CAMERA, PERMISSION_CAMERA)) {
            startCamera()
        }
    }

    private fun startCamera() {
        viewBinding?.scannerView?.setResultHandler(this)
        viewBinding?.scannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        viewBinding?.scannerView?.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding?.scannerView?.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA) {
            if (isAllPermissionResultGranted(grantResults)) {
                startCamera()
            } else {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    private fun isAllPermissionResultGranted(grantResults: IntArray): Boolean {
        var granted = true
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                granted = false
                break
            }
        }
        return granted
    }

    interface ScannerListener {
        fun onScannerSuccess(qrValue: String)
    }

    companion object {
        fun newInstance(callback: ScannerListener? = null, title: String?=null) = ScannerDialog()
            .apply {
                this.callback = callback
                this.viewBinding?.scannerTitleTextView?.text = title
            }

        val TAG: String = ScannerDialog::class.java.simpleName
        private const val PERMISSION_CAMERA: Int = 787
    }

    override fun handleResult(rawResult: Result?) {
        CommonLogger.devLog(TAG, "QR CODE: ${rawResult?.text}")
        callback?.onScannerSuccess(rawResult?.toString().orEmpty())
        dismiss()
    }
}