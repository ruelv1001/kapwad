package com.lionscare.app.ui.group.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.lionscare.app.databinding.ActivityGroupDetailsBinding
import com.lionscare.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailsBinding
    private var loadingDialog: CommonDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
    }

    private fun setupClickListener() = binding.run{
//        registerButton.setOnSingleClickListener {
//            val intent = RegisterActivity.getIntent(this@GroupDetailsActivity)
//            startActivity(intent)
//        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }



    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, GroupDetailsActivity::class.java)
        }
    }
}