package kapwad.reader.app.ui.main.fragment

import android.os.Bundle
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
import com.google.android.material.tabs.TabLayoutMediator
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
import kapwad.reader.app.data.model.SyncListModelData
import kapwad.reader.app.data.viewmodels.BillingViewModel
import kapwad.reader.app.databinding.FragmentConsumerListBinding
import kapwad.reader.app.databinding.FragmentSyncListBinding
import kapwad.reader.app.databinding.FragmentSyncMenuBinding
import kapwad.reader.app.ui.main.adapter.ConsumerListAdapter
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter
import kapwad.reader.app.ui.phmarket.fragment.ViewBasketFragmentDirections

import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsumerListFragment : Fragment(), ConsumerListAdapter.ConsumerCallback {

    private var _binding: FragmentConsumerListBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: ConsumerListAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: BillingViewModel by viewModels()


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

    }

    override fun onResume() {
        super.onResume()
        observeBilling()
        viewModel.getBilling()
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
            }



            is BillingViewState.SuccessDelete -> {
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

    }


    companion object {

    }


    override fun onItemClicked(data: CreatedBillListModelData, position: Int) {

    }

}