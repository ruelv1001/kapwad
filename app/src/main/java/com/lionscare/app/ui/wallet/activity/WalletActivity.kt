package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.data.repositories.wallet.response.QRData
import com.lionscare.app.data.repositories.wallet.response.TransactionData
import com.lionscare.app.databinding.ActivityWalletBinding
import com.lionscare.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var navController: NavController
    var data = SampleData()
    var qrData = QRData()
    var groupData = GroupData()
    var transactionData = TransactionData()
    var mode = ""
    var amount = ""
    var message = ""
    var isGroupId = false
    var isFromGroupWallet = false
    var groupSenderId = ""
    private var loadingDialog: CommonDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        mode = intent.getStringExtra(MODE).toString()
        groupSenderId = intent.getStringExtra(GROUP_SENDER_ID).toString()
        isFromGroupWallet = intent.getBooleanExtra(IS_FROM_GROUP, false)
    }

    private fun setupNavigationComponent() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.tabNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
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

        private const val MODE = "MODE"
        private const val IS_FROM_GROUP = "IS_FROM_GROUP"
        private const val GROUP_SENDER_ID = "GROUP_SENDER_ID"

        fun getIntent(
            context: Context, mode : String? = null,
            isFromGroup: Boolean? = false,
            groupSenderId: String? = ""
        ): Intent {
            val intent = Intent(context, WalletActivity::class.java)
            intent.putExtra(MODE,mode)
            intent.putExtra(IS_FROM_GROUP,isFromGroup)
            intent.putExtra(GROUP_SENDER_ID, groupSenderId)
            return intent
        }
    }
}