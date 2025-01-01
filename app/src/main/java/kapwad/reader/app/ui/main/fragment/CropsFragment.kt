package kapwad.reader.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentCropsBinding
import kapwad.reader.app.ui.crops.activity.CropsActivity
import kapwad.reader.app.ui.crops.viemodel.CropsViewModel
import kapwad.reader.app.ui.crops.viemodel.CropsViewState
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.utils.dialog.CommonDialog
import kapwad.reader.app.utils.showPopupError
import kapwad.reader.app.utils.showToastSuccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CropsFragment : Fragment() {

    private var _binding: FragmentCropsBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val viewModel: CropsViewModel by activityViewModels()
    private var loadingDialog: CommonDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCropsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDetails()
        setClickListeners()
        observeStart()
    }
    private fun observeStart() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cropsSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: CropsViewState) {
        when (viewState) {
            is CropsViewState.Loading -> showLoadingDialog(R.string.loading)
            is CropsViewState.SuccessStart -> {
                showToastSuccess(requireActivity(), description = viewState.message)
                val intent = CropsActivity.getIntent(requireActivity())
                startActivity(intent)

                hideLoadingDialog()
            }


            is CropsViewState.InputError -> {
                hideLoadingDialog()
            }

            is CropsViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    requireContext(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            else -> {
                hideLoadingDialog()
            }
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(childFragmentManager)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setDetails() = binding.run {
    }

    private fun setClickListeners() = binding.run {
    nextTextView.setOnClickListener {
    viewModel.doStartCrop()
    }
    }


}