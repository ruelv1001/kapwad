package da.farmer.app.ui.profile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import da.farmer.app.R
import da.farmer.app.data.repositories.wallet.response.QRData
import da.farmer.app.databinding.ActivityProfileBinding
import da.farmer.app.ui.main.activity.MainActivity
import da.farmer.app.ui.profile.viewmodel.ProfileViewModel
import da.farmer.app.ui.wallet.activity.WalletActivity
import da.farmer.app.utils.dialog.CommonDialog
import da.farmer.app.utils.getParcelable
import da.farmer.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var loadingDialog: CommonDialog? = null

    private val viewModel: ProfileViewModel by viewModels()
    var isFromLogin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        isFromLogin = intent.getBooleanExtra(EXTRA_IS_FROM_LOGIN, false)
        setContentView(view)
        setupNavigationComponent()
        setOnClickListener()
    }

    private fun setOnClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            if(isFromLogin){
                this@ProfileActivity.finishAffinity()
            }else{
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupNavigationComponent()  {
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.profile_nav_graph)
        if (isFromLogin){
            navGraph.setStartDestination(R.id.navigation_profile_update)
        }else{
            navGraph.setStartDestination(R.id.navigation_profile_preview)
        }
        navController.setGraph(navGraph, null)
        val appBarConfig = AppBarConfiguration.Builder(INVALID_ID)
            .setFallbackOnNavigateUpListener {
                if(isFromLogin){
                    this@ProfileActivity.finishAffinity()
                }else{
                    onBackPressedDispatcher.onBackPressed()
                }
                true
            }.build()
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfig)
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

    fun setTitlee(title: String) = binding.run {
        titleTextView.text = title
    }

    companion object {
        private const val INVALID_ID = -1
        private const val EXTRA_START = "EXTRA_START"
        private const val EXTRA_IS_FROM_LOGIN = "EXTRA_IS_FROM_LOGIN"
        fun getIntent(context: Context, isFromLogin: Boolean?= false): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(EXTRA_IS_FROM_LOGIN,isFromLogin)
            return intent
        }
    }
}