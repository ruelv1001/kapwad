package com.lionscare.app.ui.billing.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.lionscare.app.BillingNavGraphDirections
import com.lionscare.app.R
import com.lionscare.app.databinding.ActivityBillingBinding
import com.lionscare.app.databinding.ActivityProfileBinding
import com.lionscare.app.ui.billing.viewmodel.BillingViewModel
import com.lionscare.app.ui.profile.activity.ProfileActivity
import com.lionscare.app.utils.dialog.CommonDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillingActivity : AppCompatActivity() {
    private var _binding: ActivityBillingBinding? = null
    private val binding get() = _binding!!
    private var loadingDialog: CommonDialog? = null

    private val viewModel: BillingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBillingBinding.inflate(layoutInflater)
        setContentView( binding.root)

        setupNavigationComponent()
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.backImageView.setOnSingleClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.scanQrImageView.setOnSingleClickListener {
            findNavController(R.id.billingNavFragmentContainerView).navigate(BillingNavGraphDirections.actionGlobalScanBillingStatementFragment())
        }
    }
    fun setToolbarTitle(title: String) = binding.run {
        titleTextView.text = title
    }
    fun getToolbarTitle() : String = binding.run {
       return titleTextView.text.toString()
    }
    fun toggleToolbar(value : Boolean, color: Int, text : String){
        binding.customToolbar.setBackgroundColor(ContextCompat.getColor(this, color))
        binding.scanQrImageView.isVisible = value
        binding.titleTextView.text = text
    }
    private fun setupNavigationComponent() {
        binding.run {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.billingNavFragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            val appBarConfig = AppBarConfiguration.Builder(INVALID_ID)
                .setFallbackOnNavigateUpListener {
                    onBackPressedDispatcher.onBackPressed()
                    true
                }.build()
            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfig)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val INVALID_ID = -1
        fun getIntent(context: Context): Intent {
            return Intent(context, BillingActivity::class.java)
        }
    }
}