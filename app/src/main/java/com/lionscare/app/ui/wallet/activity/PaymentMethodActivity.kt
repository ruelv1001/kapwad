package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lionscare.app.databinding.ActivtyPaymentMethodBinding

class PaymentMethodActivity : AppCompatActivity()  {

    private lateinit var binding: ActivtyPaymentMethodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivtyPaymentMethodBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{

    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PaymentMethodActivity::class.java)
        }
    }
}