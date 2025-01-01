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
import kapwad.reader.app.data.viewmodels.OrderViewModel
import kapwad.reader.app.databinding.FragmentPhMarketBinding
import kapwad.reader.app.ui.phmarket.viewmodel.OrderViewState
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.utils.dialog.CommonDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PHMarketFragment : Fragment() {

    private var _binding: FragmentPhMarketBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val viewModel: OrderViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhMarketBinding.inflate(
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
            is OrderViewState.SuccessDelete -> {
                findNavController().navigate(PHMarketFragmentDirections.actionPhMarketWallet())
                hideLoadingDialog()

            }
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

    private fun setClickListeners() = binding.run {
    nextTextView.setOnClickListener {
        findNavController().navigate(PHMarketFragmentDirections.actionPhMarketWallet())
    }
    }


}