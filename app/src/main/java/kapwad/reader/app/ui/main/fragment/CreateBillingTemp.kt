package kapwad.reader.app.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Json
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentWalletMenuBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.utils.adapter.CustomViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.ProductListModelData
import kapwad.reader.app.data.model.RateAListModelData
import kapwad.reader.app.data.model.RateListModelData
import kapwad.reader.app.data.model.SyncListModelData
import kapwad.reader.app.data.model.TempListModelData
import kapwad.reader.app.data.viewmodels.BillingViewModel
import kapwad.reader.app.data.viewmodels.ConsumerViewModel
import kapwad.reader.app.data.viewmodels.OrderViewModel
import kapwad.reader.app.data.viewmodels.RateViewModel
import kapwad.reader.app.data.viewmodels.TempViewModel
import kapwad.reader.app.databinding.FragmentCreateBillBinding
import kapwad.reader.app.databinding.FragmentSyncListBinding
import kapwad.reader.app.databinding.FragmentSyncMenuBinding
import kapwad.reader.app.ui.crops.fragment.CropsListFragmentDirections
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState

import kapwad.reader.app.ui.main.viewmodel.TempViewState
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity

import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter
import kapwad.reader.app.ui.phmarket.viewmodel.OrderViewState
import kapwad.reader.app.ui.wallet.dialog.Scan2PayDialog
import kapwad.reader.app.utils.dialog.ScannerDialog
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class CreateBillingTemp : Fragment(), SyncListAdapter.DateCallback {

    private var _binding: FragmentCreateBillBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: SyncListAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: BillingViewModel by viewModels()

    private val billingViewModel: BillingViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateBillBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClickListeners()
        scan()
        observeBilling()
        observeConsumer()

    }


    private fun observeBilling() {
        lifecycleScope.launch {
            viewModel.billingStateFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }


    private fun handleViewState(viewState: BillingViewState) = binding.run {
        when (viewState) {
            is BillingViewState.Loading -> showLoadingDialog(R.string.loading)
            is BillingViewState.SuccessOfflineCreateOrder -> {


                showToastSuccess(requireActivity(), description = "Temp Created")
                hideLoadingDialog()

            }

            is BillingViewState.SuccessOrderList -> {
                showToastSuccess(requireActivity(), description = viewState.data.toString())
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

    private fun observeConsumer() {
        lifecycleScope.launch {
            billingViewModel.billingStateFlow.collect { viewState ->
                handleViewStateConsumer(viewState)
            }
        }
    }

    private fun handleViewStateConsumer(viewState: BillingViewState) = binding.run {
        when (viewState) {
            is BillingViewState.Loading -> showLoadingDialog(R.string.loading)
            is BillingViewState.SuccessOfflineCreateOrder -> {

              //  consumerViewModel.getRateA()
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")

                   // Logs each item with the tag "FruitList"

                showToastSuccess(requireActivity(), description = "Consumer Created")
                hideLoadingDialog()

            }
            is BillingViewState.SuccessDelete -> {
               // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }

//            is BillingViewState.SuccessOrderList -> {
//                Log.d("All consumer online", viewState.rateListModelData.toString())
//
//                val json = viewState.jsonData
//                Log.d("JSON Data online", json)
//
//
//
//                    val gson = Gson()
//                val listType = object : TypeToken<List<RateAListModelData>>() {}.type
//                val consumerList: List<RateAListModelData> = gson.fromJson(json, listType)
//                showToastSuccess(requireActivity(), description = consumerList.toString())
//                consumerViewModel.insertWRA(consumerList)
//                hideLoadingDialog()
//            }
//            is BillingViewState.SuccessOfflineGetOrderA -> {
//
//                showToastSuccess(requireActivity(), description = viewState.data.toString())
//                hideLoadingDialog()
//            }


            else -> Unit
        }
    }


    private fun scan() {
        val scanDialog = ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
            override fun onScannerSuccess(qrValue: String) {
           // consumerViewModel.getConsumer()
                billingViewModel.getBilling()
            }

        })
        scanDialog.show(childFragmentManager, ScannerDialog.TAG)
    }


    private fun setClickListeners() = binding.run {
        reprintButton.setOnClickListener {
viewModel.deleteAllOrder()

        }

        createButton.setOnClickListener {

            viewModel.insertBilling(
                CreatedBillListModelData(
                    id = null,
                    duedate = "1",
                    disdate = "2",
                    backdate = "2",
                    month = "2",
                    mrname = "2",
                    date = "2",
                    pres = "2",
                    prev = "2",
                    arrears_prev = "2",
                    deduct_arrears = "2",
                    arrears_pres = "2",
                    arrears_date = "2",
                    others_prev = "2",
                    deduct_others = "2",
                    others_pres = "2",
                    others_date = "2",
                    convenience_fee = "2",
                    consume = "2",
                    total = "2",
                    bill_amount = "2",
                    address = "2",
                    clas = "2",
                    meternumber = "100",
                    name = "2",
                    accountnumber = "2",
                    owners_id = "2",
                    amountrate = "2",
                    zone = "2",
                    barangay = "2",
                    senior_citizen_rate = "2",
                    wmmf = "2",
                    penalty = "2",
                    amount_paid = "2",
                    date_of_payment = "2",
                    paid = "2",
                    amount_balance = "2",
                    amount_advance = "2",
                    teller_name = "2",
                    refno = "2",
                    Service_Status = "2",
                    num_of_months = "2",
                    franchise_tax = "2",
                    Ftax_total = "2",
                    pocatotal = "2",
                    image = "2",


                    )
            )

        }
    }


    companion object {

    }

    override fun onItemClicked(data: SyncListModelData, position: Int) {

    }

}