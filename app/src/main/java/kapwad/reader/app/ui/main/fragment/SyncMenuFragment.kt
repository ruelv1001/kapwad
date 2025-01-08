package kapwad.reader.app.ui.main.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentWalletMenuBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.utils.adapter.CustomViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.OtherListModelData
import kapwad.reader.app.data.model.ProductListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateBListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.SyncListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.viewmodels.BillingViewModel
import kapwad.reader.app.data.viewmodels.ConsumerViewModel
import kapwad.reader.app.data.viewmodels.OthersViewModel
import kapwad.reader.app.data.viewmodels.RateViewModel
import kapwad.reader.app.data.viewmodels.TempViewModel
import kapwad.reader.app.databinding.FragmentSyncBinding
import kapwad.reader.app.databinding.FragmentSyncListBinding
import kapwad.reader.app.databinding.FragmentSyncMenuBinding
import kapwad.reader.app.ui.geotagging.dialog.SubmitImageDialog
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.main.dialog.LoginDialog
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.main.viewmodel.OthersViewState
import kapwad.reader.app.ui.main.viewmodel.RateViewState
import kapwad.reader.app.ui.main.viewmodel.TempViewState
import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter
import kapwad.reader.app.utils.getFileFromUri
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SyncMenuFragment : Fragment() {

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!

    private val consumerViewModel: ConsumerViewModel by viewModels()
    private val tempViewModel: TempViewModel by viewModels()
    private val otherViewModel: OthersViewModel by viewModels()
    private val rateViewModel: RateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSyncBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeSyncConsumer()
        observeSyncTemp()
        observeSyncOther()

        observeSyncRate()


    }

    override fun onResume() {
        super.onResume()
        LoginDialog.newInstance(object :
            LoginDialog.SuccessCallBack {
            override fun onSuccess() {

            }

            override fun onCancel(dialog: LoginDialog) {

            }
        }, "Select Image").show(childFragmentManager, SubmitImageDialog.TAG)
    }

    private fun observeSyncConsumer() {
        lifecycleScope.launch {
            consumerViewModel.consumerStateFlow.collect { viewState ->
                handleViewStateConsumer(viewState)
            }
        }
    }

    private fun observeSyncRate() {
        lifecycleScope.launch {
            rateViewModel.rateStateFlow.collect { viewState ->
                handleViewStateRate(viewState)
            }
        }
    }

    private fun handleViewStateRate(viewState: RateViewState) = binding.run {
        when (viewState) {
            is RateViewState.Loading -> showLoadingDialog(R.string.loading)
            is RateViewState.SuccessOfflineCreate -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Commercial  Downloaded")
                hideLoadingDialog()

            }

            is RateViewState.SuccessOfflineCreateA -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Commercial A Downloaded")
                hideLoadingDialog()

            }

            is RateViewState.SuccessOfflineCreateB -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Commercial B Downloaded")
                hideLoadingDialog()

            }

            is RateViewState.SuccessOfflineCreateC -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Commercial C Downloaded")
                hideLoadingDialog()

            }

            is RateViewState.SuccessOfflineCreateRe -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Residential  Downloaded")
                hideLoadingDialog()

            }

            is RateViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }

            is RateViewState.SuccessOnlineRate -> {
                Log.d("All consumer online", viewState.rateListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)
                val gson = Gson()
                val listType = object : TypeToken<List<RateListModelData>>() {}.type
                val consumerList: List<RateListModelData> = gson.fromJson(json, listType)
                showToastSuccess(requireActivity(), description = consumerList.toString())
                rateViewModel.insertWR(consumerList)
                hideLoadingDialog()
            }

            is RateViewState.SuccessOnlineRateA -> {
                Log.d("All consumer online", viewState.rateListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)
                val gson = Gson()
                val listType = object : TypeToken<List<RateAListModelData>>() {}.type
                val consumerList: List<RateAListModelData> = gson.fromJson(json, listType)
                showToastSuccess(requireActivity(), description = consumerList.toString())
                rateViewModel.insertWRA(consumerList)
                hideLoadingDialog()
            }


            is RateViewState.SuccessOnlineRateB -> {
                Log.d("All consumer online", viewState.rateListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)
                val gson = Gson()
                val listType = object : TypeToken<List<RateBListModelData>>() {}.type
                val consumerList: List<RateBListModelData> = gson.fromJson(json, listType)
                showToastSuccess(requireActivity(), description = consumerList.toString())
                rateViewModel.insertWRB(consumerList)
                hideLoadingDialog()
            }

            is RateViewState.SuccessOnlineRateC -> {
                Log.d("All consumer online", viewState.rateListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)
                val gson = Gson()
                val listType = object : TypeToken<List<RateCListModelData>>() {}.type
                val consumerList: List<RateCListModelData> = gson.fromJson(json, listType)
                showToastSuccess(requireActivity(), description = consumerList.toString())
                rateViewModel.insertWRC(consumerList)
                hideLoadingDialog()
            }

            is RateViewState.SuccessOnlineRateRe -> {
                Log.d("All consumer online", viewState.rateListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)
                val gson = Gson()
                val listType = object : TypeToken<List<RateReListModelData>>() {}.type
                val consumerList: List<RateReListModelData> = gson.fromJson(json, listType)
                showToastSuccess(requireActivity(), description = consumerList.toString())
                rateViewModel.insertWRRe(consumerList)
                hideLoadingDialog()
            }

            is RateViewState.SuccessOfflineGetOrder -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }

            is RateViewState.SuccessOfflineGetOrderA -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }

            is RateViewState.SuccessOfflineGetOrderB -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }

            is RateViewState.SuccessOfflineGetOrderC -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }

            is RateViewState.SuccessOfflineGetOrderRe -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }


            else -> Unit
        }
    }


    private fun observeSyncOther() {
        lifecycleScope.launch {
            otherViewModel.otherStateFlow.collect { viewState ->
                handleViewStateOther(viewState)
            }
        }
    }

    private fun handleViewStateConsumer(viewState: ConsumerViewState) = binding.run {
        when (viewState) {
            is ConsumerViewState.Loading -> showLoadingDialog(R.string.loading)
            is ConsumerViewState.SuccessOfflineCreateOrder -> {
                showToastSuccess(requireActivity(), description = "Consumer Downloaded")
                hideLoadingDialog()
            }

            is ConsumerViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()
            }

            is ConsumerViewState.SuccessOnlineConsumer -> {
                Log.d("All consumer online", viewState.consumerListModelData.toString())
                val gson = Gson()
                val listType = object : TypeToken<List<ConsumerListModelData>>() {}.type

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val consumerList: List<ConsumerListModelData> = gson.fromJson(viewState.jsonData, listType)
                        consumerViewModel.insertConsumers(consumerList)
                        withContext(Dispatchers.Main) {
                            showToastSuccess(requireActivity(), description = "Consumers added successfully")
                            hideLoadingDialog()
                        }
                    } catch (e: Exception) {
                        Log.e("Error", "Failed to process consumer list", e)
                        withContext(Dispatchers.Main) {
                            showToastError(requireActivity(), description = "Error processing consumers")
                            hideLoadingDialog()
                        }
                    }
                }
            }

            is ConsumerViewState.SuccessOfflineGetOrder -> {

                hideLoadingDialog()
            }


            else -> Unit
        }
    }


    private fun handleViewStateOther(viewState: OthersViewState) = binding.run {
        when (viewState) {
            is OthersViewState.Loading -> showLoadingDialog(R.string.loading)
            is OthersViewState.SuccessOfflineCreate -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Consumer Downloaded")
                hideLoadingDialog()

            }

            is OthersViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }

            is OthersViewState.SuccessOnlineOther -> {
                Log.d("All consumer online", viewState.otherListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)
                val gson = Gson()
                val listType = object : TypeToken<List<OtherListModelData>>() {}.type
                val consumerList: List<OtherListModelData> = gson.fromJson(json, listType)
                showToastSuccess(requireActivity(), description = consumerList.toString())
                otherViewModel.insertOther(consumerList)
                hideLoadingDialog()
            }

            is OthersViewState.SuccessOfflineGetOrder -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }


            else -> Unit
        }
    }

    private fun observeSyncTemp() {
        lifecycleScope.launch {
            tempViewModel.tempStateFlow.collect { viewState ->
                handleViewStateTemp(viewState)
            }
        }
    }

    private fun handleViewStateTemp(viewState: TempViewState) = binding.run {
        when (viewState) {
            is TempViewState.Loading -> showLoadingDialog(R.string.loading)
            is TempViewState.SuccessOfflineCreate -> {
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")
                // Logs each item with the tag "FruitList"
                showToastSuccess(requireActivity(), description = "Temp Downloaded")
                hideLoadingDialog()

            }

            is TempViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }

            is TempViewState.SuccessOnlineTemp -> {
                Log.d("All temp online", viewState.tempListModelData.toString())
                val json = viewState.jsonData
                Log.d("JSON Data online", json)

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        // Deserialize JSON in the background
                        val gson = Gson()
                        val listType = object : TypeToken<List<TempListModelData>>() {}.type
                        val tempList: List<TempListModelData> = gson.fromJson(json, listType)

                        // Insert data into the database
                        tempViewModel.insertTemp(tempList)

                        // Update UI on the main thread
                        withContext(Dispatchers.Main) {
                            showToastSuccess(requireActivity(), description = "Temps added successfully")
                            hideLoadingDialog()
                        }
                    } catch (e: Exception) {
                        Log.e("Error", "Failed to process temp list", e)
                        withContext(Dispatchers.Main) {
                            showToastError(requireActivity(), description = "Error processing temps")
                            hideLoadingDialog()
                        }
                    }
                }
            }

            is TempViewState.SuccessOfflineGetOrder -> {
                // showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }


            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }

    private fun setClickListeners() = binding.run {
        consumerButton.setOnSingleClickListener {
            consumerViewModel.getConsumerOnlineList()
        }
        tempButton.setOnClickListener {
            tempViewModel.getTempOnlineList()
        }

        otherButton.setOnClickListener {
            otherViewModel.getOtherOnlineList()
        }
        waterRateButton.setOnClickListener {
            rateViewModel.getWROnlineList()
        }
        wateraRateButton.setOnClickListener {
            rateViewModel.getWRAOnlineList()

        }

        waterBRateButton.setOnClickListener {
            rateViewModel.getWRBOnlineList()

        }

        waterCRateButton.setOnClickListener {
            rateViewModel.getWRCOnlineList()

        }
        waterReRateButton.setOnClickListener {
            rateViewModel.getWRReOnlineList()

        }
    }


    companion object {

    }


}