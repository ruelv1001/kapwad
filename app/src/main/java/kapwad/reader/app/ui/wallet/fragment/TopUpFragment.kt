package kapwad.reader.app.ui.wallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kapwad.reader.app.data.model.AddFundsModel
import kapwad.reader.app.databinding.FragmentTopUpBinding
import kapwad.reader.app.ui.wallet.activity.TopUpPointsActivity
import kapwad.reader.app.ui.wallet.adapter.AddFundsAdapter
import kapwad.reader.app.utils.setAmountFormat
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopUpFragment : Fragment(), AddFundsAdapter.AddFundCallback {

    private var _binding: FragmentTopUpBinding? = null
    private val binding get() = _binding!!
    private var gridLayoutManager: GridLayoutManager? = null
    private var adapter: AddFundsAdapter? = null
    private val activity by lazy { requireActivity() as TopUpPointsActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAddFundsList()
        setupClickListener()
    }

    private fun setupClickListener() = binding.run {
        pointsEditText.setAmountFormat()

        backImageView.setOnSingleClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if (pointsEditText.text.toString().isNotEmpty() && pointsEditText.text.toString()
                    .replace(",", "").toDouble() != 0.0
            ) {
                activity.amount = pointsEditText.text.toString()
                findNavController().navigate(TopUpFragmentDirections.actionNavigationTopUpToNavigationPaymentMethod())
            } else {
                pointsEditText.error = "Enter Number of Points"
            }
        }
    }




    private fun setupAddFundsList() = binding.run {
        adapter = AddFundsAdapter(requireActivity(), this@TopUpFragment)
        gridLayoutManager = GridLayoutManager(requireActivity(), 4)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter

        val fundList = listOf(
            AddFundsModel("100"),
            AddFundsModel("200"),
            AddFundsModel("500"),
            AddFundsModel("1,000"),
            AddFundsModel("2,000"),
            AddFundsModel("5,000"),
            AddFundsModel("10,000")
        )
        adapter?.appendData(fundList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: AddFundsModel, position: Int) {
        adapter?.setSelectedItem(data)
        binding.pointsEditText.setText(data.title.toString())
    }

}