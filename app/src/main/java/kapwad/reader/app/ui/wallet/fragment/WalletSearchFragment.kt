package kapwad.reader.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastWarning
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.group.response.GroupData
import kapwad.reader.app.data.repositories.wallet.response.QRData
import kapwad.reader.app.databinding.FragmentWalletSearchBinding
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.ui.wallet.adapter.GroupsAdapter
import kapwad.reader.app.ui.wallet.adapter.MembersAdapter
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewState
import kapwad.reader.app.utils.dialog.ScannerDialog
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class WalletSearchFragment : Fragment(),
    MembersAdapter.OnClickCallback,
    GroupsAdapter.OnClickCallback {

    private var _binding: FragmentWalletSearchBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var groupLinearLayoutManager: LinearLayoutManager? = null
    private var adapter : MembersAdapter?  = null
    private var groupsAdapter : GroupsAdapter? = null
    private val activity by lazy { requireActivity() as WalletActivity }
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupGroupAdapter()
        setupDetails()
        setupClickListener()
        observeWallet()
    }

    private fun observeWallet() {
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.walletSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: WalletViewState) {
        when (viewState) {
            is WalletViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.LoadingScanGroup -> activity.showLoadingDialog(R.string.loading)
            is WalletViewState.SuccessScanQR -> {
                activity.hideLoadingDialog()
                activity.isGroupId = false
                activity.qrData = viewState.scanQRData?: QRData()

            }
            is WalletViewState.SuccessScanGroup -> {
                activity.hideLoadingDialog()
                activity.isGroupId = true
                activity.groupData = viewState.groupData
                findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
            }
            is WalletViewState.SuccessSearchUser -> {
                activity.hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.listData)
                if (adapter?.getData()?.size == 0) {
                    binding.placeHolderTextView.isVisible = true
                    binding.recyclerView.isGone = true
                } else {
                    binding.placeHolderTextView.isGone = true
                    binding.recyclerView.isVisible = true
                }
            }
            is WalletViewState.SuccessSearchGroup -> {
                activity.hideLoadingDialog()
                groupsAdapter?.clear()
                groupsAdapter?.appendData(viewState.listData)
                if (groupsAdapter?.getData()?.size == 0) {
                    binding.groupPlaceHolderTextView.isVisible = true
                    binding.groupRecyclerView.isGone = true
                } else {
                    binding.groupPlaceHolderTextView.isGone = true
                    binding.groupRecyclerView.isVisible = true
                }
            }
            is WalletViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }

            else -> Unit
        }
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_to_title)
            }
            "Post Request" -> {
                titleTextView.text = getString(R.string.wallet_request_points_from_title)
            }
        }

    }

    private fun setupAdapter() = binding.run {
        adapter = MembersAdapter(requireActivity(), this@WalletSearchFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }

    private fun setupGroupAdapter() = binding.run {
        groupsAdapter = GroupsAdapter(requireActivity(), this@WalletSearchFragment)
        groupLinearLayoutManager = LinearLayoutManager(requireActivity())
        groupRecyclerView.layoutManager = groupLinearLayoutManager
        groupRecyclerView.adapter = groupsAdapter
    }

    private fun setupClickListener() = binding.run{

        searchEditText.doAfterTextChanged {
            applyTextView.isVisible = it.toString().isNotEmpty()
        }

        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        qrImageView.setOnSingleClickListener {
            callScanner()
        }

        applyTextView.setOnSingleClickListener {
            viewModel.doSearchUser(searchEditText.text.toString())
            viewModel.doSearchGroup(searchEditText.text.toString())
        }
    }

    private fun callScanner() {
        ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
            override fun onScannerSuccess(qrValue: String) {
                val jsonObject: JSONObject
                val res = qrValue.replace("\\", "")
                try {
                    jsonObject = JSONObject(res)
                    val type = jsonObject.getString("type")
                    val value = jsonObject.getString("value")

                    if (type.equals("lionscare", true)) {
                        viewModel.doScanQr(value)
                    } else {
                        viewModel.doScanGroup(value)
                    }

                } catch (e: JSONException) {
                    requireActivity().toastError( getString(R.string.invalid_qr_code_msg), CpmToast.SHORT_DURATION)
                    e.printStackTrace()
                }
            }
        }, "Scan QR")
            .show(childFragmentManager, ScannerDialog.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemClickListener(data: QRData) {
        activity.qrData = data
        activity.isGroupId = false
        binding.searchEditText.setText("")
        findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
    }

    override fun onGroupItemClickListener(data: GroupData) {
        activity.groupData = data
        activity.isGroupId = true
        binding.searchEditText.setText("")
        findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
    }

}