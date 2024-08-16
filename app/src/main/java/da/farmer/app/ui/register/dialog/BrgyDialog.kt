package da.farmer.app.ui.register.dialog

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
import da.farmer.app.R
import da.farmer.app.data.repositories.address.response.AddressData
import da.farmer.app.databinding.DialogAddressBinding
import da.farmer.app.ui.register.adapter.AddressListAdapter
import da.farmer.app.ui.register.viewmodel.AddressViewModel
import da.farmer.app.ui.register.viewmodel.AddressViewState
import da.farmer.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrgyDialog : BottomSheetDialogFragment(), AddressListAdapter.AddressCallback {

    private var viewBinding: DialogAddressBinding? = null
    private var adapter: AddressListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel: AddressViewModel by viewModels()
    private var callback: AddressCallBack? = null
    private var reference = ""

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
        viewModel.getBrgyList(reference)
        setClickListener()
    }

    private fun setClickListener() {
        viewBinding?.addressTitleTextView?.text = getString(R.string.lbl_barangay)
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
            is AddressViewState.SuccessGetMunicipalityList -> {
                viewBinding?.addressRecyclerView?.isVisible = true
                viewBinding?.progressContainer?.isGone = true
                adapter?.appendData(viewState.provinceList.toMutableList())
            }
            is AddressViewState.SuccessGetProvinceList -> Unit
            is AddressViewState.Idle -> Unit
            is AddressViewState.Success -> Unit
            is AddressViewState.InputError -> Unit
            is AddressViewState.LoadingRegister -> Unit
            is AddressViewState.SuccessRegister -> Unit
            else-> Unit
        }
    }

    private fun setupList() {
        viewBinding?.apply {
            adapter = AddressListAdapter(requireActivity(), this@BrgyDialog)
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
        fun onAddressClicked(brgyName: String, brgySku: String, zipCode: String)
    }

    override fun onItemClicked(data: AddressData, position: Int) {
        callback?.onAddressClicked(data.name.orEmpty(), data.code.orEmpty(),data.zipcode.orEmpty())
        dismiss()
    }

    companion object {
        fun newInstance(callback: AddressCallBack? = null, reference: String) = BrgyDialog()
            .apply {
                this.callback = callback
                this.reference = reference
            }

        val TAG: String = BrgyDialog::class.java.simpleName
    }
}