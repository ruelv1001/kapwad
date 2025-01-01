package kapwad.reader.app.ui.geotagging.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.R
import kapwad.reader.app.databinding.ActivityGeotaggingBinding
import kapwad.reader.app.ui.profile.adapter.NotificationsAdapter
import kapwad.reader.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeoTaggingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeotaggingBinding
    private var loadingDialog: CommonDialog? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: NotificationsAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeotaggingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setOnClickListener()
        setupNavigationComponent()
    }

    private fun setupNavigationComponent() {
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.tabNavHostFragment) as NavHostFragment
        var navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration.Builder(INVALID_ID)
            .setFallbackOnNavigateUpListener {
                onBackPressed()
                true
            }.build()
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfig)

    }


    private fun setOnClickListener() = binding.run {

    }

    fun handleBackNavigation() {
        onBackPressedDispatcher.onBackPressed()
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


    override fun onResume() {
        super.onResume()

    }


    private fun clearList() {
        adapter?.submitData(lifecycle, PagingData.empty())
    }

    companion object {
        private const val INVALID_ID = -1
        fun getIntent(context: Context): Intent {
            return Intent(context, GeoTaggingActivity::class.java)
        }
    }
}