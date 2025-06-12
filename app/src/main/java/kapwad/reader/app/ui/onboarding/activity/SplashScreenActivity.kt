package kapwad.reader.app.ui.onboarding.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.databinding.ActivitySplashscreenBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.onboarding.viewmodel.SplashViewModel

import kapwad.reader.app.ui.profile.activity.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.R
import kapwad.reader.app.data.model.MeterReaderListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.viewmodels.MeterViewModel
import kapwad.reader.app.security.AuthEncryptedDataManager
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.main.viewmodel.MeterViewState
import kapwad.reader.app.ui.main.viewmodel.SettingsViewModel
import kapwad.reader.app.ui.main.viewmodel.TempViewState
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.isInternetAvailable
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.launch

private var loadingDialog: CommonDialog? = null

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding
    private val viewModel: MeterViewModel by viewModels()
    private val viewModelNew: SettingsViewModel by viewModels()
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        encryptedDataManager = AuthEncryptedDataManager()
        observeAccount()
        observeConnection()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getMeter()
    }

    private fun observeConnection() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (isInternetAvailable(this@SplashScreenActivity)) {
                    viewModel.getMeterOnlineList()
                    println("Internet is connected")
                } else {
                    showToastError(
                        this@SplashScreenActivity,
                        description = "No internet connection to download data"
                    )
                    viewModel.getMeter()
                    println("No internet connection")
                }
            }
        }
    }

    private fun observeAccount() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meterStateFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: MeterViewState) = binding.run {
        when (viewState) {
            is MeterViewState.Loading -> showLoadingDialog(R.string.loading)
            is MeterViewState.SuccessOfflineCreate -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(this@SplashScreenActivity, description = "Users Downloaded")
                hideLoadingDialog()

            }

            is MeterViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }

            is MeterViewState.SuccessOnlineMeter -> {
                Log.d("All Meter online", viewState.meterReaderListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)


                val gson = Gson()
                val listType = object : TypeToken<List<MeterReaderListModelData>>() {}.type
                val tempList: List<MeterReaderListModelData> = gson.fromJson(json, listType)
                showToastSuccess(this@SplashScreenActivity, description = tempList.toString())
                viewModel.insertMeter(tempList)
                hideLoadingDialog()
            }

            is MeterViewState.SuccessOfflineGetOrder -> {
                val dataList = viewState.data
                val listSize = dataList?.size ?: 0

                if (listSize == 0) {
                    showToastError(
                        this@SplashScreenActivity,
                        description = "Need to Download Meter Reader Account Data first "
                    )
                    observeConnection()
                    hideLoadingDialog()
                } else {
                    if (encryptedDataManager.getIsLogin().equals("isLogin")) {
                        val intent = MainActivity.getIntent(this@SplashScreenActivity)
                        startActivity(intent)
                        hideLoadingDialog()
                    } else {
                        val intent = LoginActivity.getIntent(this@SplashScreenActivity)
                        startActivity(intent)
                        hideLoadingDialog()
                    }

                }


            }


            else -> Unit
        }
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
        private const val DELAY = 3000L
        fun getIntent(context: Context): Intent {
            return Intent(context, SplashScreenActivity::class.java)
        }
    }
}