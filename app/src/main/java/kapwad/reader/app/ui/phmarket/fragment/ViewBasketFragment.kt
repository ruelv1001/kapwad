package kapwad.reader.app.ui.phmarket.fragment

import android.app.AlertDialog
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
import kapwad.reader.app.data.model.ProductOrderListModelData
import kapwad.reader.app.data.viewmodels.OrderViewModel
import kapwad.reader.app.databinding.FragmentViewBaseketBinding
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.adapter.ViewBasketAdapter
import kapwad.reader.app.ui.phmarket.viewmodel.OrderViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewBasketFragment : Fragment(), ViewBasketAdapter.ViewBasketCallback {

    private var _binding: FragmentViewBaseketBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as PHMarketActivity }
    private var adapter: ViewBasketAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentViewBaseketBinding.inflate(
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
        observeOrder()
        viewModel.getOrder()
        viewModel.getTotal()
    }

    private fun observeOrder() {
        lifecycleScope.launch {
            viewModel.orderStateFlow.collect { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: OrderViewState) = binding.run {
        when (viewState) {
            is OrderViewState.Loading -> showLoadingDialog(R.string.loading)
            is OrderViewState.SuccessOfflineGetOrder -> {
                hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.data?.orEmpty() as List<ProductOrderListModelData>)
            }

            is OrderViewState.SuccessTotal -> {
                binding.daCreditTextView.setText(viewState.total)
            }

            is OrderViewState.SuccessDelete -> {
                hideLoadingDialog()
                findNavController().navigate(ViewBasketFragmentDirections.actionSuccessOrder())
            }
            else -> Unit
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as PHMarketActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as PHMarketActivity).hideLoadingDialog()
    }

    private fun setupList() {
        binding.apply {
            adapter = ViewBasketAdapter(requireActivity(), this@ViewBasketFragment)
            layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }


    private fun setClickListeners() = binding.run {
        nextTextView.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Confirm Order")
            builder.setMessage("Are you sure you want to place an order?")
            builder.setPositiveButton("Yes") { dialog, which ->
                viewModel.deleteAllOrder()
            }
            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    companion object {
        fun newInstance() = ViewBasketFragment()
    }


    override fun onItemClicked(data: ProductOrderListModelData, position: Int) {

    }
}
