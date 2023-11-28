package com.ziacare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ziacare.app.R
import com.ziacare.app.data.model.AddFundsModel
import com.ziacare.app.databinding.ActivityTopUpPointsBinding
import com.ziacare.app.ui.wallet.adapter.AddFundsAdapter
import com.ziacare.app.utils.dialog.CommonDialog
import com.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopUpPointsActivity : AppCompatActivity(){

    private lateinit var binding: ActivityTopUpPointsBinding
    var amount = ""
    private var loadingDialog: CommonDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPointsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TopUpPointsActivity::class.java)
        }
    }
}