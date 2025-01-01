package kapwad.reader.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kapwad.reader.app.R
import kapwad.reader.app.data.model.SampleData
import kapwad.reader.app.data.repositories.group.response.GroupData
import kapwad.reader.app.data.repositories.wallet.response.QRData
import kapwad.reader.app.data.repositories.wallet.response.TransactionData
import kapwad.reader.app.databinding.ActivityWalletBinding
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.getParcelable
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
    var startInputScreen = ""
    var assistanceId = ""
    private var loadingDialog: CommonDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mode = intent.getStringExtra(MODE).toString()
        groupSenderId = intent.getStringExtra(GROUP_SENDER_ID).toString()
        assistanceId = intent.getStringExtra(ASSISTANCE_ID).toString()
        isFromGroupWallet = intent.getBooleanExtra(IS_FROM_GROUP, false)
        startInputScreen = intent.getStringExtra(START_INPUT).orEmpty()
        setupNavigationComponent()
    }

    private fun setupNavigationComponent() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.tabNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.wallet_nav_graph)

        if (startInputScreen.isNotEmpty()){
            qrData = intent.getParcelable(EXTRA_QR_DATA)?: QRData()
            navGraph.setStartDestination(R.id.navigation_wallet_input)
        }else{
            navGraph.setStartDestination(R.id.navigation_wallet_search)
        }
        navController.setGraph(navGraph, null)
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
        private const val START_INPUT = "START_INPUT"
        private const val EXTRA_QR_DATA = "EXTRA_QR_DATA"
        private const val ASSISTANCE_ID = "ASSISTANCE_ID"

        fun getIntent(
            context: Context, mode : String? = null,
            isFromGroup: Boolean? = false,
            groupSenderId: String? = "",
            assistanceId: String? = null,
            qrData: QRData = QRData(),
            start: String = ""
        ): Intent {
            val intent = Intent(context, WalletActivity::class.java)
            intent.putExtra(MODE,mode)
            intent.putExtra(IS_FROM_GROUP,isFromGroup)
            intent.putExtra(GROUP_SENDER_ID, groupSenderId)
            intent.putExtra(EXTRA_QR_DATA, qrData)
            intent.putExtra(START_INPUT, start)
            intent.putExtra(ASSISTANCE_ID, assistanceId)
            return intent
        }
    }
}