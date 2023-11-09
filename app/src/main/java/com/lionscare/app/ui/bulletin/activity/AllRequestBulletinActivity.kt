package com.lionscare.app.ui.bulletin.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityAllRequestBulletinBinding
import com.lionscare.app.databinding.ActivityBillingBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener

class AllRequestBulletinActivity : AppCompatActivity() {

    private var _binding: ActivityAllRequestBulletinBinding? = null
    private val binding get() = _binding!!
    private var loadingDialog: CommonDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAllRequestBulletinBinding.inflate(layoutInflater)
        setContentView( binding.root)

        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AllRequestBulletinActivity::class.java)
        }
    }
}