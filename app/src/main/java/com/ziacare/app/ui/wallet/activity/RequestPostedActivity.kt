package com.ziacare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ziacare.app.databinding.ActivityRequestPostedBinding

class RequestPostedActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityRequestPostedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPostedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{

    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RequestPostedActivity::class.java)
        }
    }
}