package dswd.ziacare.app.ui.register.dialog

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
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.address.response.AddressData
import dswd.ziacare.app.databinding.DialogAddressBinding
import dswd.ziacare.app.ui.register.adapter.AddressListAdapter
import dswd.ziacare.app.ui.register.viewmodel.AddressViewModel
import dswd.ziacare.app.ui.register.viewmodel.AddressViewState
import dswd.ziacare.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProvinceDialog : BottomSheetDialogFragment(), AddressListAdapter.AddressCallback {

    private var viewBinding: DialogAddressBinding? = null
    private var adapter: AddressListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel: AddressViewModel by viewModels()
    private var callback: AddressCallBack? = null

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
        viewModel.getProvinceList()
        setClickListener()
    }

    private fun setClickListener() {
        viewBinding?.addressTitleTextView?.text = getString(R.string.lbl_province)
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
            is AddressViewState.Idle -> Unit
            is AddressViewState.SuccessGetMunicipalityList -> Unit
            is AddressViewState.Success -> Unit
            is AddressViewState.InputError -> Unit
            is AddressViewState.LoadingRegister -> Unit
            is AddressViewState.SuccessRegister -> Unit
            else-> Unit
        }
    }

    private fun setupList() {
        viewBinding?.apply {
            adapter = AddressListAdapter(requireActivity(), this@ProvinceDialog)
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
        fun onAddressClicked(provinceName: String, provinceSku: String, reference: String)
    }

    override fun onItemClicked(data: AddressData, position: Int) {
        callback?.onAddressClicked(data.name.orEmpty(), data.code.toString(), data.code.toString())
        dismiss()
    }

    companion object {
        fun newInstance(callback: AddressCallBack? = null) = ProvinceDialog()
            .apply {
                this.callback = callback
            }

        val TAG: String = ProvinceDialog::class.java.simpleName
    }
}