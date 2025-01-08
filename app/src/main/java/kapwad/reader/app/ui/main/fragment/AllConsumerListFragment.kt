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
import kapwad.reader.app.data.viewmodels.ConsumerViewModel
import kapwad.reader.app.databinding.FragmentAllConsumerBinding
import kapwad.reader.app.databinding.FragmentConsumerListBinding
import kapwad.reader.app.databinding.FragmentSyncListBinding
import kapwad.reader.app.databinding.FragmentSyncMenuBinding
import kapwad.reader.app.ui.main.adapter.AllConsumerListAdapter
import kapwad.reader.app.ui.main.adapter.ConsumerListAdapter
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.main.viewmodel.BillingViewState
import kapwad.reader.app.ui.main.viewmodel.ConsumerViewState
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter
import kapwad.reader.app.ui.phmarket.fragment.ViewBasketFragmentDirections
import kapwad.reader.app.utils.setOnSingleClickListener

import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllConsumerListFragment : Fragment(), AllConsumerListAdapter.ConsumerCallback {

    private var _binding: FragmentAllConsumerBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: AllConsumerListAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: ConsumerViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAllConsumerBinding.inflate(
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
        viewModel.getConsumer()
    }

    private fun observeBilling() {
        lifecycleScope.launch {
            viewModel.consumerStateFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: ConsumerViewState) = binding.run {
        when (viewState) {
            is ConsumerViewState.Loading -> showLoadingDialog(R.string.loading)
            is ConsumerViewState.SuccessOfflineGetOrder -> {
                hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.data?.orEmpty() as List<ConsumerListModelData>)
                Log.d("AdapterData", "Appending data: ${viewState.data}")
            }
            is ConsumerViewState.SuccessOfflineGetSearch -> {
                hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.data?.orEmpty() as List<ConsumerListModelData>)
                Log.d("AdapterData", "Appending data: ${viewState.data}")
            }


            is ConsumerViewState.SuccessDelete -> {
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
            adapter = AllConsumerListAdapter(requireActivity(), this@AllConsumerListFragment)
            linearLayoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter

        }
    }


    private fun setClickListeners() = binding.run {
        searchButton.setOnSingleClickListener { viewModel.searchConsumer(refEditText.text.toString()) }
    }


    companion object {

    }


    override fun onItemClicked(data: ConsumerListModelData, position: Int) {

    }

}