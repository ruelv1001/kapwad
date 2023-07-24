package com.lionscare.app.ui.badge.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.lionscare.app.databinding.ActivityVerifiedBadgeBinding

class VerifiedBadgeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifiedBadgeBinding
    var accountType = ""
    var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifiedBadgeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, VerifiedBadgeActivity::class.java)
        }
    }
}