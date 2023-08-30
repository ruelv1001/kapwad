package com.lionscare.app.ui.wallet.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.lionscare.app.R
import com.lionscare.app.data.model.SampleData
import com.lionscare.app.databinding.ActivityWalletBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var navController: NavController
    var data = SampleData()
    var mode = ""
    var amount = ""
    var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        mode = intent.getStringExtra(MODE).toString()
    }

    private fun setupNavigationComponent() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.tabNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    companion object {

        private const val MODE = "MODE"

        fun getIntent(context: Context, mode : String? = null): Intent {
            val intent = Intent(context, WalletActivity::class.java)
            intent.putExtra(MODE,mode)
            return intent
        }
    }
}