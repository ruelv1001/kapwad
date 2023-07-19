package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lionscare.app.databinding.ActivityCashInBinding

class CashInActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityCashInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{

    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CashInActivity::class.java)
        }
    }
}