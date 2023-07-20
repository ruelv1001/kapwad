package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityTopUpPointsBinding
import com.lionscare.app.utils.setOnSingleClickListener

class TopUpPointsActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityTopUpPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPointsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        h1Button.setOnSingleClickListener {
            pointsEditText.setText(getString(R.string.top_up_h1_text))
        }
        h2Button.setOnSingleClickListener {
            pointsEditText.setText(getString(R.string.top_up_h2_text))
        }
        h5Button.setOnSingleClickListener {
            pointsEditText.setText(getString(R.string.top_up_h5_text))
        }
        k1Button.setOnSingleClickListener {
            pointsEditText.setText(getString(R.string.top_up_k1_text))
        }
        k2Button.setOnSingleClickListener {
            pointsEditText.setText(getString(R.string.top_up_k2_text))
        }
        k10Button.setOnSingleClickListener {
            pointsEditText.setText(getString(R.string.top_up_k10_text))
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TopUpPointsActivity::class.java)
        }
    }
}