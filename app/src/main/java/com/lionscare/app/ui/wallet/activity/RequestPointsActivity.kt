package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lionscare.app.databinding.ActivityRequestPointsBinding
import com.lionscare.app.utils.setOnSingleClickListener

class RequestPointsActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityRequestPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPointsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RequestPointsActivity::class.java)
        }
    }
}