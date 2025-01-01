package kapwad.reader.app.ui.phmarket.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.data.model.ProductListModelData

import kapwad.reader.app.databinding.FragmentMarketPreOrderBinding
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MarketPlaceFragment : Fragment(), PreOrderListAdapter.PreOrderCallback {

    private var _binding: FragmentMarketPreOrderBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null

    private val activity by lazy { requireActivity() as PHMarketActivity }

    private var adapter: PreOrderListAdapter? = null
    private var gridLayoutManager: GridLayoutManager? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMarketPreOrderBinding.inflate(
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

    private fun setupList() {
        binding?.apply {
            adapter = PreOrderListAdapter(requireActivity(), this@MarketPlaceFragment)
            gridLayoutManager = GridLayoutManager(requireActivity(), 2)
            recyclerView.layoutManager = gridLayoutManager
            recyclerView.adapter = adapter
            val preOrderList = listOf(
                ProductListModelData(
                    "1",
                    "NIA UPRIS",
                    " DEVAST 1",
                    "https://cdn-icons-png.flaticon.com/512/5102/5102542.png",
                    "1000",

                    ),
                ProductListModelData(
                    "2",
                    "NIA UPRIS",
                    " DEVAST 2",
                    "https://cdn-icons-png.flaticon.com/512/5102/5102542.png",
                    "1100",

                    ),
                ProductListModelData(
                    "3",
                    "NIA UPRIS",
                    " DEVAST 3",
                    "https://cdn-icons-png.flaticon.com/512/5102/5102542.png",
                    "1200",

                    ),
                ProductListModelData(
                    "4",
                    "NIA UPRIS",
                    " DEVAST 4",
                    "https://cdn-icons-png.flaticon.com/512/5102/5102542.png",
                    "1200",

                    ),
            )
            adapter?.appendData(preOrderList)
        }
    }


    private fun setClickListeners() = binding.run {
        viewTextView.setOnClickListener {
            findNavController().navigate(MarketPlaceFragmentDirections.actionPhMarketViewBasket())
        }
    }

    companion object {
        fun newInstance() = MarketPlaceFragment()
        private const val PARTICIPANT = "participant"
    }

    override fun onItemClicked(data: ProductListModelData, position: Int) {

        findNavController().navigate(
            MarketPlaceFragmentDirections.actionPhMarketAddToCart(
                data.id.toString(),
                data?.avatar.toString(),
                data.amount.toString(),
                data?.name.toString()
            )
        )
    }


}