package kapwad.reader.app.ui.crops.dialog

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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kapwad.reader.app.R
import kapwad.reader.app.databinding.DialogUploadVideoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubmitUploadVideoDialog : DialogFragment() {

    private var viewBinding: DialogUploadVideoBinding? = null
    private var callback: SuccessCallBack? = null
    private var videoPos = ""
    private var videoUri: Uri? = null

    // Register for video capture result
    private val videoCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            videoUri = result.data?.data
            videoUri?.let { callback?.onSuccess(it) }
            dismiss()
        }
    }

    // Register for gallery selection result
    private val gallerySelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            videoUri = result.data?.data
            videoUri?.let { callback?.onSuccess(it) }
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_upload_video, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogUploadVideoBinding.bind(view)
        setClickListener()
    }

    private fun setClickListener() {
        viewBinding?.videoTextView?.text=videoPos
        viewBinding?.videoTextView?.setOnClickListener {
            showVideoOptions()
        }
        viewBinding?.closeImageView?.setOnClickListener {
            dismiss()
        }

        viewBinding?.closeImageView?.setOnClickListener {
            dismiss()
        }
    }

    private fun showVideoOptions() {
        val options = arrayOf("Record Video", "Select from Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Upload Video")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureVideo() // Record video
                    1 -> selectVideoFromGallery() // Select from gallery
                }
            }
            .show()
    }

    private fun captureVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10) // Limit to 10 seconds
        }
        videoCaptureLauncher.launch(intent)
    }

    private fun selectVideoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
        }
        gallerySelectLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface SuccessCallBack {
        fun onSuccess(videoUri: Uri)
        fun onCancel(dialog: SubmitUploadVideoDialog)
    }

    companion object {
        fun newInstance(callback: SuccessCallBack? = null, videoPos: String) =
            SubmitUploadVideoDialog().apply {
                this.callback = callback
                this.videoPos = videoPos
            }

        val TAG: String = SubmitUploadVideoDialog::class.java.simpleName
    }
}
