package dswd.ziacare.app.ui.wallet.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dswd.ziacare.app.R
import dswd.ziacare.app.data.model.AddFundsModel
import dswd.ziacare.app.databinding.FragmentTopUpBinding
import dswd.ziacare.app.ui.wallet.activity.TopUpPointsActivity
import dswd.ziacare.app.ui.wallet.adapter.AddFundsAdapter
import dswd.ziacare.app.ui.wallet.viewmodel.WalletViewModel
import dswd.ziacare.app.ui.wallet.viewmodel.WalletViewState
import dswd.ziacare.app.utils.currencyFormat
import dswd.ziacare.app.utils.dialog.WebviewDialog
import dswd.ziacare.app.utils.setAmountFormat
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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