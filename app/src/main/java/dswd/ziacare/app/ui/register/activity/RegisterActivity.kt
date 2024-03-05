package dswd.ziacare.app.ui.register.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.baseresponse.UserModel
import dswd.ziacare.app.data.repositories.registration.request.OTPRequest
import dswd.ziacare.app.data.repositories.registration.request.RegistrationRequest
import dswd.ziacare.app.data.repositories.registration.response.OnboardingScanQRData
import dswd.ziacare.app.databinding.ActivityRegisterBinding
import dswd.ziacare.app.utils.dialog.CommonDialog
import dswd.ziacare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var loadingDialog: CommonDialog? = null

    var requestModel = RegistrationRequest()
    var otpModel = OTPRequest()

    var onBoardingData = OnboardingScanQRData()
    var onBoardingUserModel = UserModel()
    var code = ""
    var password = ""
    var passwordConfirm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        setOnClickListener()
    }

    private fun setOnClickListener() = binding.run {
        backImageView.setOnSingleClickListener {
            onBackPressed()
        }
    }

    fun removeBackButton(){
        binding.backImageView.visibility= View.GONE
    }

    override fun onBackPressed() {
        if(binding.backImageView.isVisible)
            super.onBackPressed()
    }
    private fun setupNavigationComponent() {
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.register_nav_graph)
        navController.setGraph(navGraph, null)
        val appBarConfig = AppBarConfiguration.Builder(INVALID_ID)
            .setFallbackOnNavigateUpListener {
                onBackPressed()
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
        scanQRImageView.isVisible = title == getString(R.string.lbl_primary_info)
    }

    fun getScanQRImageView(): ImageView {
        return binding.scanQRImageView
    }

    fun  alignTitleToCenter(){
        binding.titleTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    }
    companion object {
        private const val INVALID_ID = -1
        private const val EXTRA_CODE = "EXTRA_CODE"
        fun getIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }
}