package com.lionscare.app.ui.register.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lionscare.app.R
import com.lionscare.app.data.repositories.address.response.AddressData
import com.lionscare.app.data.repositories.profile.response.LOVData
import com.lionscare.app.databinding.DialogAddressBinding
import com.lionscare.app.ui.register.adapter.AddressListAdapter
import com.lionscare.app.ui.register.adapter.LionsClubLovListAdapter
import com.lionscare.app.ui.register.viewmodel.AddressViewModel
import com.lionscare.app.ui.register.viewmodel.AddressViewState
import com.lionscare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegionDialog : BottomSheetDialogFragment(), LionsClubLovListAdapter.RegionCallback {

    private var viewBinding: DialogAddressBinding? = null
    private var adapter: LionsClubLovListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel: AddressViewModel by viewModels()
    private var callback: RegionCallBack? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_address,
            container,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogAddressBinding.bind(view)
        setupList()
        observeProvinceList()
        viewModel.getRegionList()
        setClickListener()
    }

    private fun setClickListener() {
        viewBinding?.addressTitleTextView?.text = getString(R.string.lbl_region)
    }

    private fun observeProvinceList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.addressSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: AddressViewState) {
        when (viewState) {
            is AddressViewState.Loading -> {
                viewBinding?.addressRecyclerView?.isGone = true
                viewBinding?.progressContainer?.isVisible = true
            }
            is AddressViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is AddressViewState.SuccessGetLionsClubLOV -> {
                viewBinding?.addressRecyclerView?.isVisible = true
                viewBinding?.progressContainer?.isGone = true
                adapter?.differ?.submitList(viewState.lovResponse)
            }
            else-> Unit
        }
    }

    private fun setupList() {
        viewBinding?.apply {
            adapter = LionsClubLovListAdapter(this@RegionDialog)
            layoutManager = LinearLayoutManager(context)
            addressRecyclerView.layoutManager = layoutManager
            addressRecyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }


    interface RegionCallBack {
        fun onRegionClicked(data: LOVData)
    }

    override fun onItemClicked(data: LOVData, position: Int) {
        callback?.onRegionClicked(data)
        dismiss()
    }

    companion object {
        fun newInstance(callback: RegionCallBack? = null) = RegionDialog()
            .apply {
                this.callback = callback
            }

        val TAG: String = RegionDialog::class.java.simpleName
    }
}