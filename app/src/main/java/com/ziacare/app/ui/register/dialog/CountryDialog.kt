package com.ziacare.app.ui.register.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager


import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ziacare.app.R
import com.ziacare.app.data.repositories.address.response.AddressData
import com.ziacare.app.databinding.DialogAddressBinding
import com.ziacare.app.ui.register.adapter.AddressListAdapter
import com.ziacare.app.ui.register.adapter.CountryListAdapter
import com.ziacare.app.ui.register.viewmodel.AddressViewModel
import com.ziacare.app.ui.register.viewmodel.AddressViewState
import com.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryDialog : BottomSheetDialogFragment(), CountryListAdapter.AddressCallback {

    private var viewBinding: DialogAddressBinding? = null
    private var adapter: CountryListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel: AddressViewModel by viewModels()
    private var callback: AddressCallBack? = null
    private var displayCountryCode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        viewModel.getCountryList(displayCountryCode)
        setClickListener()
    }

    private fun setClickListener() {
        viewBinding?.addressTitleTextView?.text = getString(R.string.address_country_hint)
    }

    private fun observeProvinceList() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addressSharedFlow.collect { viewState ->
                handleViewState(viewState)
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
            is AddressViewState.SuccessGetProvinceList -> {
                viewBinding?.addressRecyclerView?.isVisible = true
                viewBinding?.progressContainer?.isGone = true
                adapter?.appendData(viewState.provinceList.toMutableList())
            }
            else -> Unit
        }
    }

    private fun setupList() {
        viewBinding?.apply {
            adapter = CountryListAdapter(requireActivity(), this@CountryDialog, displayCountryCode)
            layoutManager = LinearLayoutManager(context)
            addressRecyclerView.layoutManager = layoutManager
            addressRecyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface AddressCallBack {
        fun onAddressClicked(countryName: String, code: String, phone_code: String)
    }

    override fun onItemClicked(data: AddressData, position: Int) {
        callback?.onAddressClicked(data.name.orEmpty(), data.code.orEmpty(), data.phone_code.orEmpty())
        dismiss()
    }

    companion object {
        fun newInstance(callback: AddressCallBack? = null, displayCountryCode: Boolean = false) = CountryDialog()
            .apply {
                this.callback = callback
                this.displayCountryCode = displayCountryCode
            }

        val TAG: String = CountryDialog::class.java.simpleName
    }
}