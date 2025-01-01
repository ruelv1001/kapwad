package kapwad.reader.app.ui.phmarket.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.R
import kapwad.reader.app.data.model.OrderListModelData
import kapwad.reader.app.data.viewmodels.OrderViewModel
import kapwad.reader.app.databinding.FragmentPhMarketWalletBinding
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.adapter.OrderListAdapter
import kapwad.reader.app.ui.phmarket.viewmodel.OrderViewState
import kapwad.reader.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PHMarketWalletFragment : Fragment(), OrderListAdapter.OrderCallback {

    private var _binding: FragmentPhMarketWalletBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null

    private val activity by lazy { requireActivity() as PHMarketActivity }

    private var adapter: OrderListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel: OrderViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhMarketWalletBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClickListeners()
        observeStart()
        setupList()
    }

    private fun observeStart() {
        lifecycleScope.launch {
            viewModel.orderStateFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }


    private fun handleViewState(viewState: OrderViewState) = binding.run {
        when (viewState) {
            is OrderViewState.Loading -> showLoadingDialog(R.string.loading)
            else -> Unit
        }
    }
    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(childFragmentManager)
        }
    }


    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }


    private fun setupList() {
        binding?.apply {
            adapter = OrderListAdapter(requireActivity(), this@PHMarketWalletFragment)
            layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            val dateList = listOf(
                OrderListModelData(
                    1,
                    "NIA UPRIS",
                    "NIA UPRIS",
                    "November 12, 2024",
                    "1000",
                    "pending"
                ),
                OrderListModelData(
                    1,
                    "NIA UPRIS",
                    "NIA UPRIS",
                    "November 13, 2024",
                    "1100",
                    "pending"
                ),
                OrderListModelData(
                    1,
                    "NIA UPRIS",
                    "NIA UPRIS",
                    "November 13, 2024",
                    "1200",
                    "pending"
                ),
            )
            adapter?.appendData(dateList)
        }
    }


    private fun setClickListeners() = binding.run {
        nextTextView.setOnClickListener {
            findNavController().navigate(PHMarketWalletFragmentDirections.actionPhMarketPreOrder())
        }
    }



    companion object {
        fun newInstance() = PHMarketWalletFragment()
        private const val PARTICIPANT = "participant"
    }

    override fun onItemClicked(data: OrderListModelData, position: Int) {

    }

}