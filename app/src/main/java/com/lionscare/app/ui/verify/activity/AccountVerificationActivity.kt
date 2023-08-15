package com.lionscare.app.ui.verify.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityAccountVerificationBinding
import com.lionscare.app.utils.setOnSingleClickListener

class AccountVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountVerificationBinding
    private var isIdVerified : Boolean? = false
    private var isAddressVerified : Boolean? = false
    private var focusedEditTextId: Int = 0
    private var imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedFileName = data?.data?.let { getFileNameFromUri(it) }
                val focusedEditText = findViewById<TextInputEditText>(focusedEditTextId)
                focusedEditText.setText(selectedFileName)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClickListener()
        setUpDetails()
    }

    private fun setUpDetails() = binding.run{
        if(isIdVerified == true){
            idArrowImageView.visibility = View.GONE
            idCheckImageView.visibility = View.VISIBLE
        }
        if(isAddressVerified == true){
            addressArrowImageView.visibility = View.GONE
            addressCheckImageView.visibility = View.VISIBLE
        }
    }
    private fun setClickListener() = binding.run {
        validIdLinearLayout.setOnSingleClickListener {

        }

        addressLinearLayout.setOnSingleClickListener {

        }

        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun openCamera(editText: TextInputEditText) {
        focusedEditTextId = editText.id
        editText.clearFocus()

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            imageCaptureLauncher.launch(takePictureIntent)
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("_display_name"))
            }
        }
        return uri.lastPathSegment ?: ""
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AccountVerificationActivity::class.java)
        }
    }
}