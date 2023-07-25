package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.model.AddFundsModel
import com.lionscare.app.databinding.ActivityTopUpPointsBinding
import com.lionscare.app.ui.wallet.adapter.AddFundsAdapter
import com.lionscare.app.utils.setOnSingleClickListener

class TopUpPointsActivity : AppCompatActivity(){

    private lateinit var binding: ActivityTopUpPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPointsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TopUpPointsActivity::class.java)
        }
    }
}