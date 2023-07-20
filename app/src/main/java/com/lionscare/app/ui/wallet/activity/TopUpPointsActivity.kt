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

class TopUpPointsActivity : AppCompatActivity(), AddFundsAdapter.AddFundCallback {

    private lateinit var binding: ActivityTopUpPointsBinding
    private var gridLayoutManager: GridLayoutManager? = null
    private var adapter: AddFundsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPointsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupAddFundsList()
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
        backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupAddFundsList() = binding.run {
        adapter = AddFundsAdapter(this@TopUpPointsActivity, this@TopUpPointsActivity)
        gridLayoutManager = GridLayoutManager(this@TopUpPointsActivity, 4)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter

        val fundList = listOf(
            AddFundsModel("100"),
            AddFundsModel("200"),
            AddFundsModel("500"),
            AddFundsModel("1,000"),
            AddFundsModel("2,000"),
            AddFundsModel("5,000"),
            AddFundsModel("10,000")
        )
        adapter?.appendData(fundList)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TopUpPointsActivity::class.java)
        }
    }

    override fun onItemClicked(data: AddFundsModel, position: Int) {
        adapter?.setSelectedItem(data)
        binding.pointsEditText.setText(data.title.toString())
    }
}