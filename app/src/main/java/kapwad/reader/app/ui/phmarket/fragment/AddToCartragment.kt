package kapwad.reader.app.ui.phmarket.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.R
import kapwad.reader.app.data.model.ProductOrderListModelData

import kapwad.reader.app.data.viewmodels.OrderViewModel
import kapwad.reader.app.databinding.FragmentAddToCartBinding
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.viewmodel.OrderViewState
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.showToastSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddToCartragment : Fragment() {

    private var _binding: FragmentAddToCartBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val viewModel: OrderViewModel by viewModels()
    private val args: AddToCartragmentArgs by this.navArgs()
    private var count = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddToCartBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        setDetails()
        observeOrder()
    }


    private fun setClickListeners() = binding.run {

        countTextView.text = count.toString()
        val amount = args.amount
        val totalAmount = count * (amount?.toInt() ?: 0)
        totalTextView.text = getString(R.string.add_to_basket, totalAmount.toString())
        productImageView.setOnClickListener {

        }

        totalTextView.setOnClickListener {
            // Create a CartLocalData instance

            val quantity = count

            // Check if quantity is greater than 0 before adding to cart
            if (quantity >= 1) {
                // Add item to cart using ViewModel
                viewModel.insertOrder(
                    ProductOrderListModelData(
                        id = null,
                        product_id = args.id,
                        name = args.name,
                        amount = args.amount,
                        quantity = countTextView.text.toString()
                    )
                )


            } else {

                Toast.makeText(
                    requireActivity(),
                    "Please add at least one item.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        addImageView.setOnClickListener {
            count += 1
            countTextView.text = count.toString()
            val amount = args.amount
            val totalAmount = count * (amount?.toInt() ?: 0)
            totalTextView.text = getString(R.string.add_to_basket, totalAmount.toString())
        }
        subtractImageView.setOnClickListener {
            count -= 1
            countTextView.text = count.toString()
            val amount = args.amount
            val totalAmount = count * (amount?.toInt() ?: 0)
            totalTextView.text = getString(R.string.add_to_basket, totalAmount.toString())
        }
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
            is OrderViewState.SuccessOfflineCreateOrder -> {


                showToastSuccess(requireActivity(), description = "Added To Cart")
                hideLoadingDialog()

            }

            is OrderViewState.SuccessOrderList -> {
                hideLoadingDialog()
            }

            else -> Unit
        }
    }

    private fun setDetails() = binding.run {

        productImageView.loadAvatar(args.avatar, requireActivity())
        nameTextView.setText(args.name)
        priceTextView.setText(args.amount)

    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as PHMarketActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as PHMarketActivity).hideLoadingDialog()
    }


}