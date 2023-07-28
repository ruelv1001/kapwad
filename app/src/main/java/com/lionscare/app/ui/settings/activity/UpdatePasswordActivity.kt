package com.lionscare.app.ui.settings.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityUpdatePasswordBinding
import com.lionscare.app.utils.setOnSingleClickListener

class UpdatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClickListener()
    }

    private fun setClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        continueButton.setOnSingleClickListener {
            if (oldPasswordEditText.text?.isEmpty() == true){
                oldPasswordTextInputLayout.error = getString(R.string.required_error_message)
            }else if(newPasswordEditText.text?.isEmpty() == true){
                newPasswordTextInputLayout.error = getString(R.string.required_error_message)
            }else if(confirmPasswordEditText.text?.isEmpty() == true){
                confirmPasswordTextInputLayout.error = getString(R.string.required_error_message)
            }else{
                finish()
            }
        }

        oldPasswordEditText.doOnTextChanged { text, start, before, count ->
            oldPasswordTextInputLayout.error = ""
        }
        newPasswordEditText.doOnTextChanged { text, start, before, count ->
            newPasswordTextInputLayout.error = ""
        }
        confirmPasswordEditText.doOnTextChanged { text, start, before, count ->
            confirmPasswordTextInputLayout.error = ""
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UpdatePasswordActivity::class.java)
        }
    }
}