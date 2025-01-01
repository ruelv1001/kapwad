package kapwad.reader.app.ui.geotagging.dialog

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
import kapwad.reader.app.databinding.DialogUploadImageGeotaggingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubmitImageDialog : DialogFragment() {

    private var viewBinding: DialogUploadImageGeotaggingBinding? = null
    private var callback: SuccessCallBack? = null
    private var imageUri: Uri? = null
    private var imagePos = ""
    // Register for camera capture result
    private val cameraCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let { callback?.onSuccess(it) }
            dismiss()
        }
    }

    // Register for gallery selection result
    private val gallerySelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let { callback?.onSuccess(it) }
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_upload_image_geotagging, container, false)
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
        viewBinding = DialogUploadImageGeotaggingBinding.bind(view)
        setClickListener()
    }

    private fun setClickListener() {
        viewBinding?.imageTextView?.text=imagePos
        viewBinding?.imageTextView?.setOnClickListener {
            showImageOptions()
        }
        viewBinding?.closeImageView?.setOnClickListener {
            dismiss()
        }
    }

    private fun showImageOptions() {
        val options = arrayOf("Take Photo", "Select from Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Upload Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> captureImage() // Take a photo
                    1 -> selectImageFromGallery() // Select from gallery
                }
            }
            .show()
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraCaptureLauncher.launch(intent)
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        gallerySelectLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface SuccessCallBack {
        fun onSuccess(imageUri: Uri)
        fun onCancel(dialog: SubmitImageDialog)
    }

    companion object {
        fun newInstance(callback: SuccessCallBack? = null, imagePos: String) =
            SubmitImageDialog().apply {
                this.callback = callback
                this.imagePos = imagePos
            }

        val TAG: String = SubmitImageDialog::class.java.simpleName
    }
}
