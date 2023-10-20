package com.lionscare.app.ui.main.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lionscare.app.ui.onboarding.activity.SplashScreenActivity
import com.lionscare.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    private var loadingDialog: CommonDialog? = null

    var groupCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        setClickListener()
    }

    private fun setupNavigationComponent() {
        binding.run {
            navView = tabBottomNavigationView
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.tabNavHostFragment) as NavHostFragment
            navController = navHostFragment.navController
            navView.setupWithNavController(navController)
            if (intent.getStringExtra(EXTRA_GO_TO) != ""){
                val navGraph =
                    navController.navInflater.inflate(R.navigation.main_nav_graph)
                val bundle = Bundle()
                navGraph.setStartDestination(R.id.navigation_wallet)
                navController.setGraph(navGraph, bundle)
            }
            // code for adding badge
            //navView.getOrCreateBadge(R.id.navigation_groups).number = number_of_badge
        }
    }

    private fun setClickListener() = binding.run {

    }

    private fun openLogoutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logout_title_lbl))
        builder.setMessage(getString(R.string.logout_desc_lbl))
        builder.setPositiveButton(getString(R.string.logout_btn)) { _, _ ->
            val intent = SplashScreenActivity.getIntent(this)
            startActivity(intent)
            finishAffinity()
        }
        builder.setNegativeButton(getString(R.string.logout_cancel_btn), null)
        builder.show()
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

    companion object {
        private const val EXTRA_GO_TO = "EXTRA_GO_TO"
        fun getIntent(
            context: Context,
            goTo: String = ""
        ): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(EXTRA_GO_TO, goTo)
            return intent
        }


    }
}