package kapwad.reader.app.ui.main.fragment

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
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
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.model.RateCListModelData
import kapwad.reader.app.data.model.RateReListModelData
import kapwad.reader.app.data.model.SyncListModelData
import kapwad.reader.app.data.viewmodels.BillingViewModel
import kapwad.reader.app.databinding.FragmentConsumerListBinding
import kapwad.reader.app.databinding.FragmentSyncListBinding
import kapwad.reader.app.databinding.FragmentSyncMenuBinding
import kapwad.reader.app.ui.geotagging.dialog.SubmitImageDialog
import kapwad.reader.app.ui.main.adapter.ConsumerListAdapter
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.main.dialog.LoginTempDialog
import kapwad.reader.app.ui.main.dialog.PrinterDialog

import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter
import kapwad.reader.app.ui.phmarket.fragment.ViewBasketFragmentDirections
import kapwad.reader.app.utils.XP380PTPrinter
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kapwad.reader.app.utils.showToastWarning

import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsumerListFragment : Fragment(), ConsumerListAdapter.ConsumerCallback {

    private var _binding: FragmentConsumerListBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: ConsumerListAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: BillingViewModel by viewModels()
    private var macAdd: String? = null
    private var printData: CreatedBillListModelData =
        CreatedBillListModelData()
    private var uploadJson: String? = null
    var prettyJson: String = ""
    private lateinit var printer: XP380PTPrinter
    private var printerDialog: PrinterDialog? = null

    private var isReset: String?=null
    // Permissions needed for Android 11+ and ColorOS 14
    private val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION, // Required for Bluetooth scanning
        Manifest.permission.BLUETOOTH_CONNECT // Android 12+
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConsumerListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClickListeners()
        setupList()
//        printer = XP380PTPrinter(requireActivity(), macAdd.toString())
//        printer.connectPrinter(requireActivity())
       // checkBluetooth()
        //checkBluetoothAndPermissions()
        isReset="false"
    }

    override fun onResume() {
        super.onResume()
        observeBilling()
        viewModel.getBilling()
       // observePrinter()
    }

    private fun observePrinter() {
        PrinterDialog.newInstance(object :
            PrinterDialog.SuccessCallBack {

            override fun onSuccess(mymac: String,dialog: PrinterDialog?) {
                macAdd=mymac
                printer = XP380PTPrinter(requireActivity(), mymac)
                printer.connectPrinter(requireActivity())

                if (printer.isPrinterConnected()) {
                    showToastSuccess(requireActivity(), description = "Printer Connected")
                    viewModel.getBilling()
                    dialog?.dismiss()

                } else {
                    showToastError(requireActivity(), description = "Printer Disconnected")
                    observePrinter()
                }
            }

            override fun onCancel(dialog: PrinterDialog) {

            }
        }, "Select Image").show(childFragmentManager, PrinterDialog.TAG)
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
            is BillingViewState.SuccessOfflineGetOrder -> {
                hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.data?.orEmpty() as List<CreatedBillListModelData>)


                val recordsRegex = Regex("""CreatedBillListModelData\((.*?)\)""")
                val records =
                    recordsRegex.findAll(viewState.data.toString()).map { it.groupValues[1] }

// Convert each record to a map and then to JSON


// Print JSON Array
                val jsonArray = records.map { record ->
                    val keyValueRegex = Regex("""(\w+)=([^,]+)""")
                    val map = keyValueRegex.findAll(record).associate { match ->
                        val key = match.groupValues[1]
                        val value = match.groupValues[2]
                        key to value
                    }
                    map
                }.toList()


                prettyJson = Gson().toJson(jsonArray)

// Log the JSON array for debugging
                Log.d("All Bill", prettyJson)

// Send the JSON data to the ViewModel

                uploadJson = prettyJson
            }
            is BillingViewState.SuccessOfflineGetSearch -> {
                hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.data?.orEmpty() as List<CreatedBillListModelData>)
            }


            is BillingViewState.SuccessDelete -> {
                hideLoadingDialog()

                showToastSuccess(
                    requireActivity(),
                    description = "Data Deleted"
                )
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)

            }

            is BillingViewState.SuccessUpload -> {
                showToastSuccess(
                    requireActivity(),
                    description = viewState.billUploadedResponse.message
                )
                hideLoadingDialog()
            }

            is BillingViewState.Error -> {
                // Show the error message
                showToastSuccess(requireActivity(), description = viewState.errorMessage.toString())
                Log.d("All Error on upload", viewState.toString())
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


    private fun setupList() {
        binding?.apply {
            adapter = ConsumerListAdapter(requireActivity(), this@ConsumerListFragment)
            linearLayoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter

        }
    }


    private fun setClickListeners() = binding.run {
        uploadButton.setOnSingleClickListener {
            isReset="upload"
            setLogin()

        }
        searchButton.setOnSingleClickListener {
            viewModel.searchConsumer(refEditText.text.toString())
        }
        resetButton.setOnSingleClickListener {
            isReset="true"
          setLogin()

        }
    }

    private fun setLogin()=binding.run{
        LoginTempDialog.newInstance(object :
            LoginTempDialog.SuccessCallBack {
            override fun onSuccess() {
                if(isReset=="upload"){
                viewModel.getUploadJson(prettyJson)}
                if(isReset=="true"){
                    isReset="true"
                    viewModel.deleteAllOrder()
                }
            }

            override fun onCancel(dialog: LoginTempDialog) {

            }
        }).show(childFragmentManager, LoginTempDialog.TAG)
    }


    companion object {
        private const val INVALID_ID = -1
        private const val REQUEST_ENABLE_BT = 1
    }


    override fun onItemClicked(data: CreatedBillListModelData, position: Int) {
        val json = data.toString()

        if (printer.isPrinterConnected()) {
            bluprint(data)

        } else {
            // Handle connection error
            printer = XP380PTPrinter(requireActivity(), macAdd.toString())
            printer.connectPrinter(requireActivity())
        }
    }



    private fun bluprint(data: CreatedBillListModelData) {

        /* val alertDialogBuilder = AlertDialog.Builder(context)
         alertDialogBuilder.apply {
             setTitle("Print")
             setMessage("Do you want to Reprint")
             setPositiveButton("Yes") { _, _ ->

                 printer.printTextHeader("Republic of the Philippines\\nKAPATAGAN WATER DISTRICT\\nKapatagan Lanao Del Norte \\n\\n\\n  BILLING STATEMENT\n")
                 printer.printTextNormalHeader("\n\n\nAccount number: " + data?.accountnumber.toString())
                 printer.printTextNormalHeader("\nClassification:  " + data?.clas.toString())
                 printer.printTextNormalHeader("\nClient Name::  " + data?.name.toString())
                 printer.printTextNormalHeader("\nAddress:  " + data?.address.toString())
                 printer.printTextNormalHeader("\nMonth:  " + data?.month.toString())
                 printer.printTextNormalHeader("\n\nPeriod Covered:  " )
                 printer.printTextNormalHeader("\nFrom:  " + data?.backdate.toString())
                 printer.printTextNormalHeader("\nTo:  "+ data?.date.toString())
                 printer.printTextNormalHeader("\n\nReading:  " )
                 printer.printTextNormalHeader("\nPres:  " + data?.pres.toString())
                 printer.printTextNormalHeader("\nPrev:  " + data?.prev.toString())
                 printer.printTextNormalHeader("\nWater Usage:  " + data?.consume.toString())
                 printer.printTextNormalHeader("\nWater Sales:  ${data?.bill_amount?.toDouble()?.minus(data?.wmmf?.toDouble() ?: 0.0)}")
                 printer.printTextNormalHeader("\nArrears:  ${data?.bill_amount?.toDouble()?.minus(data?.deduct_arrears?.toDouble() ?: 0.0)}")
                 printer.printTextNormalHeader("\nOthers:  " + data?.deduct_others.toString())
                 printer.printTextNormalHeader("\nF-TAX:  " + data?.Ftax_total.toString())
                 printer.printTextNormalHeader("\nPCA:  " + data?.pocatotal.toString())
                 printer.printTextNormalHeader("\n\n\nIMPORTANT NOTICE")
                 printer.printTextNormalHeader("\nREADING :"+ data?.date.toString())
                 printer.printTextNormalHeader("\nAll payments are applied first against prior unpaid Water Bills. Failure to receive a Bill does not relieve the concessionaire of his liability.")
                 printer.printTextNormalHeader("\nDUE DATE :"+ data?.duedate.toString())
                 printer.printTextNormalHeader("\nA surcharge of 10% shall be added after Due Date.")
                 printer.printTextNormalHeader("\nDISCONNECTION DATE :"+ data?.disdate.toString())
                 printer.printTextNormalHeader("\n\nThis serves as a Final Notice.  Disconnection shall be implemented on the specified date."+ data?.disdate.toString())
                 printer.printTextNormalHeader("\nKapatagan Water District || Copyright 2021 - 2030 || All Rights Reserved.")
                 printer.printTextNormalHeader("\nCreated by: Ruel Velasquez Senior Full Stack Developer", bold = true)




                 printer.printTextNormalHeader("\nThank you for choosing us\n\n\n\n" )

                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show() */
    }

    private fun checkBluetoothAndPermissions() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        if (!hasPermissions(requiredPermissions)) {
            requestPermissions(requiredPermissions, REQUEST_ENABLE_BT)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
                printer.connectPrinter(requireActivity())
            } else {
                Toast.makeText(requireContext(), "Permissions are required for printing", Toast.LENGTH_LONG).show()
            }
        }
    }



}