package kapwad.reader.app.ui.main.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.DecimalFormat
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import kapwad.reader.app.R
import kapwad.reader.app.ui.main.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.data.model.ConsumerListModelData
import kapwad.reader.app.data.model.CreatedBillListModelData
import kapwad.reader.app.data.model.OtherListModelData
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
import kapwad.reader.app.databinding.FragmentCreateBillBinding
import kapwad.reader.app.ui.geotagging.dialog.SubmitImageDialog
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.main.dialog.LoginDialog
import kapwad.reader.app.ui.main.dialog.PrinterDialog
import kapwad.reader.app.ui.main.fragment.ConsumerListFragment.Companion
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.main.viewmodel.OthersViewState
import kapwad.reader.app.ui.main.viewmodel.RateViewState
import kapwad.reader.app.ui.main.viewmodel.TempViewState
import kapwad.reader.app.utils.CreateBillHelper
import kapwad.reader.app.utils.XP380PTPrinter

import kapwad.reader.app.utils.dialog.ScannerDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showToastError
import kapwad.reader.app.utils.showToastSuccess
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList

@AndroidEntryPoint
class CreateBillingFragment : Fragment() {

    private var _binding: FragmentCreateBillBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: SyncListAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: BillingViewModel by viewModels()

    private val billingViewModel: BillingViewModel by viewModels()

    private val consumerViewModel: ConsumerViewModel by viewModels()
    private val tempViewModel: TempViewModel by viewModels()
    private val othersViewModel: OthersViewModel by viewModels()
    private val rateViewModel: RateViewModel by viewModels()
    private var consumerListModelData: ConsumerListModelData? = null
    private var createdBillListModelData: CreatedBillListModelData? = null
    private var tempListModelData: TempListModelData? = null
    private var otherListModelData: OtherListModelData? = null

    private var rateListModelData: RateListModelData? = null
    private var rateaListModelData: RateAListModelData? = null
    private var ratebListModelData: RateBListModelData? = null
    private var ratecListModelData: RateCListModelData? = null
    private var ratereListModelData: RateReListModelData? = null
    private var macAdd: String? = null
    private var printData: CreatedBillListModelData =
        CreatedBillListModelData()

    private var bill_amount_final = 0.0
    private lateinit var printer: XP380PTPrinter
    val billHelper = CreateBillHelper()
    private var nowprint = false


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
        observeTemp()
        observeOthers()
        observeRate()
//        printer = XP380PTPrinter(requireActivity(),macAdd.toString())
//        printer.connectPrinter(requireActivity())
        checkBluetoothAndPermissions()

    }


    private fun checkBluetoothAndPermissions() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(
                requireContext(),
                "Bluetooth is not supported on this device",
                Toast.LENGTH_SHORT
            ).show()
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
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
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
                Toast.makeText(
                    requireContext(),
                    "Permissions are required for printing",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun observeRate() {
        lifecycleScope.launch {
            rateViewModel.rateStateFlow.collect { viewState ->
                handleViewStateRate(viewState)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observePrinter()

    }

    private fun observePrinter() {
        PrinterDialog.newInstance(object :
            PrinterDialog.SuccessCallBack {

            override fun onSuccess(mymac: String, dialog: PrinterDialog?) {
                macAdd = mymac
                printer = XP380PTPrinter(requireActivity(), mymac)
                printer.connectPrinter(requireActivity())

                if (printer.isPrinterConnected()) {
                    showToastSuccess(requireActivity(), description = "Printer Connected")

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

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
        printer.closePrinterConnection()
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

                printData = viewState.data
                nowprint = true
                bluprint(printData)
                showToastSuccess(requireActivity(), description = "Billing Created")
                totalEditText.setText("")
                hideLoadingDialog()

            }

            is BillingViewState.SuccessOrderList -> {
                showToastSuccess(requireActivity(), description = viewState.data.toString())
                hideLoadingDialog()
            }

            is BillingViewState.SuccessExisted -> {
                showToastError(requireActivity(), description = "Bill Already Created")
                hideLoadingDialog()
            }

            is BillingViewState.Error -> {
                showToastError(
                    requireActivity(),
                    description = "Not Exist"
                )
                // Check if the current value is less than the previous value

                val previousValue = tempListModelData?.Prev?.toIntOrNull() ?: 0
                val currentValue = presentEditText.text.toString().toIntOrNull() ?: 0
                if (currentValue < previousValue) {
                    showToastError(
                        requireActivity(),
                        description = "Invalid Present Reading"
                    )
                    binding.presentEditText.error = "Invalid Present Reading"
                } else if (binding.presentEditText.text.toString() == "") {
                    showToastError(
                        requireActivity(),
                        description = "Invalid Present Reading"
                    )
                    binding.presentEditText.error = "Invalid Present Reading"
                } else {

                    createBill()
                }
                hideLoadingDialog()
            }

            is BillingViewState.SuccessDelete -> {
                showToastSuccess(requireActivity(), description = "All Deleted")
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

    private fun observeOthers() {
        lifecycleScope.launch {
            othersViewModel.otherStateFlow.collect { viewState ->
                handleViewStateOthers(viewState)
            }
        }
    }

    private fun handleViewStateRate(viewState: RateViewState) = binding.run {
        when (viewState) {
            is RateViewState.Loading -> showLoadingDialog(R.string.loading)
            is RateViewState.SuccessRateById -> {
                rateListModelData = viewState.data
                hideLoadingDialog()
            }

            is RateViewState.SuccessRateAById -> {
                rateaListModelData = viewState.data
                hideLoadingDialog()
            }

            is RateViewState.SuccessRateBById -> {
                ratebListModelData = viewState.data
                hideLoadingDialog()
            }

            is RateViewState.SuccessRateCById -> {
                ratecListModelData = viewState.data
                hideLoadingDialog()
            }

            is RateViewState.SuccessRateReById -> {
                ratereListModelData = viewState.data
                hideLoadingDialog()
            }

            is RateViewState.SuccessDelete -> {
                showToastSuccess(requireActivity(), description = "All Deleted")
                hideLoadingDialog()
            }


            else -> Unit
        }
    }

    private fun handleViewStateOthers(viewState: OthersViewState) = binding.run {
        when (viewState) {
            is OthersViewState.Loading -> showLoadingDialog(R.string.loading)
            is OthersViewState.SuccessOtherById -> {


                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")


                otherListModelData = viewState.data
                hideLoadingDialog()

            }

            is OthersViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }


            else -> Unit
        }
    }

    private fun observeConsumer() {
        lifecycleScope.launch {
            consumerViewModel.consumerStateFlow.collect { viewState ->
                handleViewStateConsumer(viewState)
            }
        }
    }

    private fun handleViewStateConsumer(viewState: ConsumerViewState) = binding.run {
        when (viewState) {
            is ConsumerViewState.Loading -> showLoadingDialog(R.string.loading)
            is ConsumerViewState.SuccessConsumerById -> {
                showToastSuccess(
                    requireActivity(),
                    description = viewState.data?.is_employee.toString()
                )
                Log.d("All consumer offline", "Data: ${viewState.data.toString()}")

                if (viewState.data?.consumersid?.equals(null) == false) {
                    consumerListModelData = viewState.data
                    setUpData(viewState.data)
                    hideLoadingDialog()
                } else {
                    showToastError(requireActivity(), description = "Data not found")

                    val scanDialog =
                        ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
                            override fun onScannerSuccess(qrValue: String) {
                                if (printer.isPrinterConnected()) {

                                    consumerViewModel.getConsumerById(qrValue)
                                    tempViewModel.getTempById(qrValue)
                                } else {
                                    printer = XP380PTPrinter(requireActivity(), macAdd.toString())
                                    printer.connectPrinter(requireActivity())
                                }

                            }

                        })
                    scanDialog.show(childFragmentManager, ScannerDialog.TAG)
                }
            }

            is ConsumerViewState.SuccessDelete -> {
                // showToastSuccess(requireActivity(), description = viewState.msg)
                hideLoadingDialog()

            }


            else -> Unit
        }
    }

    private fun observeTemp() {
        lifecycleScope.launch {
            tempViewModel.tempStateFlow.collect { viewState ->
                handleViewStateTemp(viewState)
            }
        }
    }

    private fun handleViewStateTemp(viewState: TempViewState) = binding.run {
        when (viewState) {
            is TempViewState.Loading -> showLoadingDialog(R.string.loading)
            is TempViewState.SuccessTempById -> {
                tempListModelData = viewState.data
                hideLoadingDialog()

            }


            else -> Unit
        }
    }

    private fun setUpData(data: ConsumerListModelData?) = binding.run {
        idEditText.setText(data?.consumersid.toString())
        customerNameEditText.setText(data?.firstname + " " + data?.middlename + " " + data?.lastname)
        barangayEditText.setText(data?.barangay.toString())
    }

    private fun scan() {
        val scanDialog = ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
            override fun onScannerSuccess(qrValue: String) {
                consumerViewModel.getConsumerById(qrValue)
                tempViewModel.getTempById(qrValue)
                othersViewModel.getTempById("1")
                rateViewModel.getRateById("1")
                rateViewModel.getRateAById("1")
                rateViewModel.getRateBById("1")
                rateViewModel.getRateCById("1")
                rateViewModel.getRateReById("1")
            }

        })
        scanDialog.show(childFragmentManager, ScannerDialog.TAG)
    }


    private fun setClickListeners() = binding.run {
        calculateButton.setOnClickListener {
            val previousValue = tempListModelData?.Prev?.toIntOrNull() ?: 0
            val currentValue = presentEditText.text.toString().toIntOrNull() ?: 0
            if (currentValue < previousValue) {
                showToastError(
                    requireActivity(),
                    description = "Invalid Present Reading"
                )
                binding.presentEditText.error = "Invalid Present Reading"
            } else if (binding.presentEditText.text.toString() == "") {
                showToastError(
                    requireActivity(),
                    description = "Invalid Present Reading"
                )
                binding.presentEditText.error = "Invalid Present Reading"
            } else {
                val result = currentValue - previousValue

                if (consumerListModelData?.is_employee.toString().equals("1")) {
                    calculateRe(result - 15)
                } else {
                    calculateRe(result)
                }


            }
        }
        reprintButton.setOnSingleClickListener {
            if (printer.isPrinterConnected()) {
                bluprint(printData)

            } else {
                // Handle connection error
                printer = XP380PTPrinter(requireActivity(), macAdd.toString())
                printer.connectPrinter(requireActivity())
            }

        }




        createButton.setOnClickListener {
            viewModel.getValidatedOnData(
                billHelper.getMonthAndYear(),
                tempListModelData?.Meternumber.toString()
            )
        }


    }

    fun calculateRe(points: Int) {

        if (consumerListModelData?.class_type.equals("COMMERCIAL - 0")) {
            var setA = 0
            var setB = 0
            var setC = 0
            var setD = 0
            when {
                points in 11..20 -> setA = (points - 10).coerceAtMost(10)
                points in 21..30 -> {
                    setA = 10
                    setB = (points - 20).coerceAtMost(10)
                }

                points in 31..40 -> {
                    setA = 10
                    setB = 10
                    setC = (points - 30).coerceAtMost(10)
                }

                points >= 41 -> {
                    setA = 10
                    setB = 10
                    setC = 10
                    setD = (points - 40).coerceAtMost(10)
                }
            }

            // Weights for each set (with default values for null or invalid cases)
            val weightA = rateListModelData?.wr_11_20?.toDoubleOrNull() ?: 0.0
            val weightB = rateListModelData?.wr_21_30?.toDoubleOrNull() ?: 0.0
            val weightC = rateListModelData?.wr_31_40?.toDoubleOrNull() ?: 0.0
            val weightD = rateListModelData?.wr_41_up?.toDoubleOrNull() ?: 0.0

            val total =
                (setA * weightA) + (setB * weightB) + (setC * weightC) + (setD * weightD) + (consumerListModelData?.waterrate?.toDoubleOrNull()
                    ?: 0.0)
            val decimalFormat = DecimalFormat("#.00")
            bill_amount_final = total.toDouble()
            binding.totalEditText.setText(total.toString())
        }
        if (consumerListModelData?.class_type.equals("COMMERCIAL - A")) {
            var setA = 0
            var setB = 0
            var setC = 0
            var setD = 0
            when {
                points in 11..20 -> setA = (points - 10).coerceAtMost(10)
                points in 21..30 -> {
                    setA = 10
                    setB = (points - 20).coerceAtMost(10)
                }

                points in 31..40 -> {
                    setA = 10
                    setB = 10
                    setC = (points - 30).coerceAtMost(10)
                }

                points >= 41 -> {
                    setA = 10
                    setB = 10
                    setC = 10
                    setD = (points - 40).coerceAtMost(10)
                }
            }

            // Weights for each set (with default values for null or invalid cases)
            val weightA = rateaListModelData?.wr_11_20?.toDoubleOrNull() ?: 0.0
            val weightB = rateaListModelData?.wr_21_30?.toDoubleOrNull() ?: 0.0
            val weightC = rateaListModelData?.wr_31_40?.toDoubleOrNull() ?: 0.0
            val weightD = rateaListModelData?.wr_41_up?.toDoubleOrNull() ?: 0.0

            val total =
                (setA * weightA) + (setB * weightB) + (setC * weightC) + (setD * weightD) + (consumerListModelData?.waterrate?.toDoubleOrNull()
                    ?: 0.0)
            val decimalFormat = DecimalFormat("#.00")
            bill_amount_final = total.toDouble()
            binding.totalEditText.setText(total.toString())
        }

        if (consumerListModelData?.class_type.equals("COMMERCIAL - B")) {
            var setA = 0
            var setB = 0
            var setC = 0
            var setD = 0
            when {
                points in 11..20 -> setA = (points - 10).coerceAtMost(10)
                points in 21..30 -> {
                    setA = 10
                    setB = (points - 20).coerceAtMost(10)
                }

                points in 31..40 -> {
                    setA = 10
                    setB = 10
                    setC = (points - 30).coerceAtMost(10)
                }

                points >= 41 -> {
                    setA = 10
                    setB = 10
                    setC = 10
                    setD = (points - 40).coerceAtMost(10)
                }
            }

            // Weights for each set (with default values for null or invalid cases)
            val weightA = ratebListModelData?.wr_11_20?.toDoubleOrNull() ?: 0.0
            val weightB = ratebListModelData?.wr_21_30?.toDoubleOrNull() ?: 0.0
            val weightC = ratebListModelData?.wr_31_40?.toDoubleOrNull() ?: 0.0
            val weightD = ratebListModelData?.wr_41_up?.toDoubleOrNull() ?: 0.0

            val total =
                (setA * weightA) + (setB * weightB) + (setC * weightC) + (setD * weightD) + (consumerListModelData?.waterrate?.toDoubleOrNull()
                    ?: 0.0)
            val decimalFormat = DecimalFormat("#.00")
            bill_amount_final = total.toDouble()


            binding.totalEditText.setText(total.toString())


        }
        if (consumerListModelData?.class_type.equals("COMMERCIAL - C")) {
            var setA = 0
            var setB = 0
            var setC = 0
            var setD = 0
            when {
                points in 11..20 -> setA = (points - 10).coerceAtMost(10)
                points in 21..30 -> {
                    setA = 10
                    setB = (points - 20).coerceAtMost(10)
                }

                points in 31..40 -> {
                    setA = 10
                    setB = 10
                    setC = (points - 30).coerceAtMost(10)
                }

                points >= 41 -> {
                    setA = 10
                    setB = 10
                    setC = 10
                    setD = (points - 40).coerceAtMost(10)
                }
            }

            // Weights for each set (with default values for null or invalid cases)
            val weightA = ratecListModelData?.wr_11_20?.toDoubleOrNull() ?: 0.0
            val weightB = ratecListModelData?.wr_21_30?.toDoubleOrNull() ?: 0.0
            val weightC = ratecListModelData?.wr_31_40?.toDoubleOrNull() ?: 0.0
            val weightD = ratecListModelData?.wr_41_up?.toDoubleOrNull() ?: 0.0

            val total =
                (setA * weightA) + (setB * weightB) + (setC * weightC) + (setD * weightD) + (consumerListModelData?.waterrate?.toDoubleOrNull()
                    ?: 0.0)
            val decimalFormat = DecimalFormat("#.00")
            bill_amount_final = total.toDouble()


            binding.totalEditText.setText(total.toString())

        }

        if (consumerListModelData?.class_type.equals("RESIDENTIAL")) {
            var setA = 0
            var setB = 0
            var setC = 0
            var setD = 0
            when {
                points in 11..20 -> setA = (points - 10).coerceAtMost(10)
                points in 21..30 -> {
                    setA = 10
                    setB = (points - 20).coerceAtMost(10)
                }

                points in 31..40 -> {
                    setA = 10
                    setB = 10
                    setC = (points - 30).coerceAtMost(10)
                }

                points >= 41 -> {
                    setA = 10
                    setB = 10
                    setC = 10
                    setD = (points - 40).coerceAtMost(10)
                }
            }

            // Weights for each set (with default values for null or invalid cases)
            val weightA = ratereListModelData?.wr_11_20?.toDoubleOrNull() ?: 0.0
            val weightB = ratereListModelData?.wr_21_30?.toDoubleOrNull() ?: 0.0
            val weightC = ratereListModelData?.wr_31_40?.toDoubleOrNull() ?: 0.0
            val weightD = ratereListModelData?.wr_41_up?.toDoubleOrNull() ?: 0.0

            val total =
                (setA * weightA) + (setB * weightB) + (setC * weightC) + (setD * weightD) + (consumerListModelData?.waterrate?.toDoubleOrNull()
                    ?: 0.0)
            val decimalFormat = DecimalFormat("#.00")
            bill_amount_final = total.toDouble()


            binding.totalEditText.setText(total.toString())

        }


    }


    companion object {
        private const val INVALID_ID = -1
        private const val REQUEST_ENABLE_BT = 1
    }


    private fun bluprint(data: CreatedBillListModelData) {
        if (nowprint) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.apply {
                setTitle("Print")
                setMessage("Do you want to Print")
                setPositiveButton("Yes") { _, _ ->

                    printer.printTextHeader("Republic of the Philippines\\nKAPATAGAN WATER DISTRICT\\nKapatagan Lanao Del Norte \\n\\n\\n  BILLING STATEMENT\n")
                    printer.printTextNormalHeader("\n\n\nAccount number: " + data?.accountnumber.toString())
                    printer.printTextNormalHeader("\nClassification:  " + data?.clas.toString())
                    printer.printTextNormalHeader("\nClient Name::  " + data?.name.toString())
                    printer.printTextNormalHeader("\nAddress:  " + data?.address.toString())
                    printer.printTextNormalHeader("\nMonth:  " + data?.month.toString())
                    printer.printTextNormalHeader("\n\nPeriod Covered:  ")
                    printer.printTextNormalHeader("\nFrom:  " + data?.backdate.toString())
                    printer.printTextNormalHeader("\nTo:  " + data?.date.toString())
                    printer.printTextNormalHeader("\n\nReading:  ")
                    printer.printTextNormalHeader("\nPres:  " + data?.pres.toString())
                    printer.printTextNormalHeader("\nPrev:  " + data?.prev.toString())
                    printer.printTextNormalHeader("\nWater Usage:  " + data?.consume.toString())
                    printer.printTextNormalHeader(
                        "\nWater Sales:  ${
                            data?.bill_amount?.toDouble()
                        }"
                    )
                    printer.printTextNormalHeader(
                        "\nArrears: ${
                            (data?.amount_balance?.toDouble() ?: 0.0) +
                                    (data?.deduct_arrears?.toDouble() ?: 0.0)
                        }"
                    )
                    printer.printTextNormalHeader("\nOthers:  " + data?.deduct_others.toString())
                    printer.printTextNormalHeader("\nF-TAX:  " + data?.Ftax_total.toString())
                    printer.printTextNormalHeader("\nPCA:  " + data?.pocatotal.toString())
                    printer.printTextNormalHeader("\n\n\nIMPORTANT NOTICE")
                    printer.printTextNormalHeader("\nREADING :" + data?.date.toString())
                    printer.printTextNormalHeader("\nAll payments are applied first against prior unpaid Water Bills. Failure to receive a Bill does not relieve the concessionaire of his liability.")
                    printer.printTextNormalHeader("\nDUE DATE :" + data?.duedate.toString())
                    printer.printTextNormalHeader("\nA surcharge of 10% shall be added after Due Date.")
                    printer.printTextNormalHeader("\nDISCONNECTION DATE :" + data?.disdate.toString())
                    printer.printTextNormalHeader("\n\nThis serves as a Final Notice.  Disconnection shall be implemented on the specified date." + data?.disdate.toString())
                    printer.printTextNormalHeader("\nKapatagan Water District || Copyright 2021 - 2030 || All Rights Reserved.")
                    printer.printTextNormalHeader(
                        "\nCreated by: Ruel Velasquez Senior Full Stack Developer",
                        bold = true
                    )




                    printer.printTextNormalHeader("\nThank you for choosing us\n\n\n\n")

                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else {
            Toast.makeText(requireContext(), "No Data found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createBill() = binding.run {
        if (totalEditText.text?.toString()?.isEmpty() == false) {
            var arrears_presTemp: Double? =
                ((tempListModelData?.Stag_Arrears_Prev?.toDoubleOrNull()
                    ?: 0.0) - (tempListModelData?.Arrears_Deduct?.toDoubleOrNull() ?: 0.0))
            var others_presTemp: Double? =
                (tempListModelData?.Stag_Others_Prev?.toDoubleOrNull()
                    ?: 0.0) - (tempListModelData?.Others_Deduct?.toDoubleOrNull() ?: 0.0)

            var consumeTemp: Double? = (binding.presentEditText.text.toString().toDoubleOrNull()
                ?: 0.0) - (tempListModelData?.Prev?.toDoubleOrNull() ?: 0.0)
            var totalTemp: Double? = (tempListModelData?.Arrears_Deduct?.toDoubleOrNull()
                ?: 0.0) + (tempListModelData?.Others_Deduct?.toDoubleOrNull()
                ?: 0.0) + (tempListModelData?.Amount_Balance?.toDoubleOrNull() ?: 0.0)

            viewModel.insertBilling(

                CreatedBillListModelData(
                    id = null,
                    duedate = billHelper.getCurrentMinusTenDisDate(),
                    disdate = billHelper.getCurrentDisDate(),
                    backdate = billHelper.getCurrentBackDate(),
                    month = billHelper.getMonthAndYear(),
                    mrname = viewModel.user.username.toString(),
                    date = billHelper.getCurrentDate(),
                    pres = binding.presentEditText.text.toString(),
                    prev = tempListModelData?.Prev,
                    arrears_prev = tempListModelData?.Stag_Arrears_Prev,
                    deduct_arrears = tempListModelData?.Arrears_Deduct,
                    arrears_pres = arrears_presTemp.toString(),
                    arrears_date = tempListModelData?.Date_Arrears,
                    others_prev = tempListModelData?.Stag_Others_Prev,
                    deduct_others = tempListModelData?.Others_Deduct,
                    others_pres = others_presTemp.toString(),
                    others_date = tempListModelData?.Date_Others,
                    convenience_fee = "0",
                    consume = consumeTemp.toString(),
                    total = totalTemp.toString(),
                    bill_amount = bill_amount_final.toString(),
                    address = tempListModelData?.Address,
                    clas = consumerListModelData?.class_type,
                    meternumber = consumerListModelData?.meternumber,
                    name = tempListModelData?.Concessionaire,
                    accountnumber = tempListModelData?.account_number,
                    owners_id = tempListModelData?.id.toString(),
                    amountrate = billHelper.getMonthAndYear() + " " + consumerListModelData?.class_type + " " + consumerListModelData?.waterrate,
                    zone = tempListModelData?.zone.toString(),
                    barangay = billHelper.getMonthAndYear() + " - " + consumerListModelData?.barangay,
                    senior_citizen_rate = consumerListModelData?.senior_citizen_rate,
                    wmmf = "20",
                    penalty = "0",
                    amount_paid = "0",
                    date_of_payment = "0",
                    paid = "Unknown",
                    amount_balance = "0",
                    amount_advance = "0",
                    teller_name = "0",
                    refno = "0",
                    Service_Status = "ACTIVE",
                    num_of_months = ((tempListModelData?.Num_of_Months?.toIntOrNull()
                        ?: 0) + 1).toString(),
                    franchise_tax = otherListModelData?.franchise_tax,
                    Ftax_total = ((otherListModelData?.franchise_tax?.toDoubleOrNull()
                        ?: 0.0) * (bill_amount_final)).toString(),
                    pocatotal = ((otherListModelData?.poca?.toDoubleOrNull()
                        ?: 0.0) * consumeTemp!!).toString(),
                    image = "2",


                    )
            )


        } else {
            showToastError(
                requireActivity(),
                description = "Invalid Data"
            )
        }
    }
}