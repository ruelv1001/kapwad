package kapwad.reader.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import kapwad.reader.app.databinding.ActivityTopUpPointsBinding
import kapwad.reader.app.utils.dialog.CommonDialog
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