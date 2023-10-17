package com.lionscare.app.ui.verify.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityFaceScannerBinding
import com.lionscare.app.utils.facedetect.facecentercircleview.FaceCenterCrop
import com.lionscare.app.utils.facedetect.facedetectionutil.FaceDetectionProcessor
import com.lionscare.app.utils.facedetect.facedetectionutil.FaceDetectionResultListener
import com.lionscare.app.utils.facedetect.facedetectionutil.common.CameraSource
import com.lionscare.app.utils.facedetect.facedetectionutil.common.FrameMetadata
import com.lionscare.app.utils.facedetect.facedetectionutil.common.GraphicOverlay
import com.lionscare.app.utils.facedetect.utils.FaceDetectionScanner.Constants.KEY_CAMERA_PERMISSION_GRANTED
import com.lionscare.app.utils.facedetect.utils.FaceDetectionScanner.Constants.PERMISSION_REQUEST_CAMERA
import com.lionscare.app.utils.facedetect.utils.Imageutils
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class FaceScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceScannerBinding

    private var mCameraSource: CameraSource? = null

    var faceDetectionProcessor: FaceDetectionProcessor? = null
    var faceDetectionResultListener: FaceDetectionResultListener? = null

    var bmpCapturedImage: Bitmap? = null
    var capturedFaces: List<FirebaseVisionFace>? = null

    var faceCenterCrop: FaceCenterCrop? = null
    var faceCenterCropListener: FaceCenterCrop.FaceCenterCropListener? = null

    var hasFrontCam = false
    var cameraFacing: Int = CameraSource.CAMERA_FACING_FRONT

    var isSmiling = false
    var isLookingLeft = false
    var isLookingRight = false

    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceScannerBinding.inflate(layoutInflater)
        val view = binding.root

        //for dev release version, since it produces error of
        //FirebaseApp must be initialized
        FirebaseApp.initializeApp(this)

        setContentView(view)
        setupFaceScanner()
        setClickListener()
    }

    private fun setupFaceScanner(){
        faceCenterCrop = FaceCenterCrop(this, 100, 100, 1)

        if (binding.preview.isPermissionGranted(true, mMessageSender)) {
            Thread(mMessageSender).start()
        }
    }

    private fun setClickListener() = binding.run {
        captureButton.setOnSingleClickListener {
            if (faceCenterCrop != null) {
                faceCenterCrop?.transform(
                    bmpCapturedImage,
                    faceCenterCrop?.getCenterPoint(capturedFaces),
                    getFaceCropResult()
                )
            }
        }
    }

    private fun createCameraSource() {

        // To initialise the detector
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .enableTracking()
            .build()
        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        //resolvess problem of camera preview suddenly crashing or not previewing anymore,
        //also crashes on first permission request. this solves it by keeping an instance
        // of the surface the holds the camera preview
        //reference : https://stackoverflow.com/questions/23807086/surfacetexture-has-been-abandoned
        var surfaceTexture: SurfaceTexture? = SurfaceTexture(CameraSource.DUMMY_TEXTURE_NAME);


        // To connect the camera resource with the detector
        mCameraSource = CameraSource(this, binding.barcodeOverlay, surfaceTexture)
        mCameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)


        // FaceContourDetectorProcessor faceDetectionProcessor = new FaceContourDetectorProcessor(detector);
        faceDetectionProcessor = FaceDetectionProcessor(detector)
        faceDetectionProcessor?.faceDetectionResultListener = getFaceDetectionListener()
        mCameraSource?.setMachineLearningFrameProcessor(faceDetectionProcessor)
        startCameraSource()
    }

    private fun getFaceDetectionListener(): FaceDetectionResultListener? {
        if (faceDetectionResultListener == null) faceDetectionResultListener =
            object : FaceDetectionResultListener {
                override fun onSuccess(
                    originalCameraImage: Bitmap?,
                    faces: List<FirebaseVisionFace>,
                    frameMetadata: FrameMetadata,
                    graphicOverlay: GraphicOverlay,
                ) {
                    val isEnable: Boolean = faces.isNotEmpty()
                    Log.e("num", num.toString())
                    for (face in faces) {
                        when (num) {
                            1 -> {
                                binding.textTextView.text = getString(R.string.scanner_smile_lbl)
                                isSmiling = face.smilingProbability > 0.5
                            }
                            2 -> {
                                binding.textTextView.text = getString(R.string.scanner_left_lbl)
                                isLookingRight = face.headEulerAngleY > 12
                            }
                            3 -> {
                                binding.textTextView.text = getString(R.string.scanner_right_lbl)
                                isLookingLeft = face.headEulerAngleY < -12
                            }
                            else -> {
                                isSmiling = false
                                isLookingLeft = false
                                isLookingRight = false
                            }
                        }

                        // To get the results
                        Log.d(TAG, "Face bounds : " + face.boundingBox)

                        // To get this, we have to set the ClassificationMode attribute as ALL_CLASSIFICATIONS
                        Log.d(TAG, "Left eye open probability : " + face.leftEyeOpenProbability)
                        Log.d(TAG, "Right eye open probability : " + face.rightEyeOpenProbability)
                        Log.d(TAG, "Smiling probability : " + face.smilingProbability)

                        // To get this, we have to enableTracking
                        Log.d(TAG, "Face ID : " + face.trackingId)
                    }
                    runOnUiThread {
                        Log.d(TAG, "button enable true ")
                        bmpCapturedImage =
                            originalCameraImage
                        capturedFaces = faces
                        when (num) {
                            1 -> {
                                binding.captureButton.isEnabled = isSmiling
                            }
                            2 -> {
                                binding.captureButton.isEnabled = isLookingRight
                            }
                            3 -> {
                                binding.captureButton.isEnabled = isLookingLeft
                            }
                            else -> {
                                binding.captureButton.isEnabled = isEnable
                            }
                        }
                    }
                }

                override fun onFailure(e: Exception) {}
            }
        return faceDetectionResultListener
    }

    private fun startCameraSource() {

        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext)
        Log.d(TAG, "startCameraSource: $code")
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, code, PERMISSION_REQUEST_CAMERA)
            dlg!!.show()
        }
        if (mCameraSource != null && binding.preview != null && binding.barcodeOverlay != null) {
            try {
                Log.d(TAG, "startCameraSource: ")
                binding.preview.start(mCameraSource, binding.barcodeOverlay)
            } catch (e: IOException) {
                mCameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
                Log.d(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }
        } else Log.d(TAG, "startCameraSource: not started")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $requestCode")
        binding.preview.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        binding.preview.stop()
    }

    override fun onDestroy() {
        if (mCameraSource != null) {
            try {
                mCameraSource!!.release()
            } catch (e: java.lang.Exception) {
                mCameraSource!!.stop()
                e.printStackTrace()
            }
        }
        super.onDestroy()
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            Log.d(TAG, "handleMessage: ")
            createCameraSource()
        }
    }

    private val mMessageSender = Runnable {
        Log.d(TAG, "mMessageSender: ")
        val msg = mHandler.obtainMessage()
        val bundle = Bundle()
        bundle.putBoolean(KEY_CAMERA_PERMISSION_GRANTED, false)
        num = intent.extras?.getInt("num")?: 0
        msg.data = bundle
        mHandler.sendMessage(msg)
    }

    private fun getFaceCropResult(): FaceCenterCrop.FaceCenterCropListener? {
        if (faceCenterCropListener == null) faceCenterCropListener =
            object : FaceCenterCrop.FaceCenterCropListener {
                override fun onTransform(updatedBitmap: Bitmap?) {
                    Log.d(TAG, "onTransform: ")
                    try {
                        val capturedFile = File(filesDir, "newImage.jpg")
                        val imageutils = Imageutils(this@FaceScannerActivity)
                        imageutils.store_image(capturedFile, updatedBitmap)
                        val currentIntent = intent
                        currentIntent.putExtra("image", capturedFile.absolutePath)
                        setResult(RESULT_OK, currentIntent)
                        finish()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure() {
                    Toast.makeText(this@FaceScannerActivity, "No face found", Toast.LENGTH_SHORT).show()
                }
            }
        return faceCenterCropListener
    }

    companion object {
        private const val INVALID_ID = -1
        val TAG: String = FaceScannerActivity::class.java.name
        fun getIntent(context: Context): Intent {
            return Intent(context, FaceScannerActivity::class.java)
        }
    }
}