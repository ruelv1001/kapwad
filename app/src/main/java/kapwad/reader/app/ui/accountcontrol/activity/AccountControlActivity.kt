package kapwad.reader.app.ui.accountcontrol.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.R
import kapwad.reader.app.databinding.ActivityAccountControlBinding
import kapwad.reader.app.utils.dialog.CommonDialog

@AndroidEntryPoint
class AccountControlActivity : AppCompatActivity() {

    private var _binding : ActivityAccountControlBinding? = null
    private val binding get() = _binding!!
    private var loadingDialog: CommonDialog? = null
    var reasonId : Int = 0
    var reason : String? = null

    var selectedChoice = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountControlBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        setClickListener()
    }

    private fun setupNavigationComponent() {
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.account_control_nav_graph)
        navController.setGraph(navGraph, null)
        val appBarConfig = AppBarConfiguration.Builder(INVALID_ID)
            .setFallbackOnNavigateUpListener {
                onBackPressedDispatcher.onBackPressed()
                true
            }.build()
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfig)
    }

    private fun setClickListener() = binding.run {

    }

    fun setToolbarTitle(title : String){
        binding.titleTextView.text = title
    }

    fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(supportFragmentManager)
        }
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
        _binding = null
    }

    companion object {
        private const val INVALID_ID = -1
        const val DELETE = "delete"
        const val DEACTIVATE = "deactivate"
        fun getIntent(context: Context): Intent {
            return Intent(context, AccountControlActivity::class.java)
        }
    }
}