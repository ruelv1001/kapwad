package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.repositories.group.response.GroupData
import com.lionscare.app.databinding.FragmentGroupSearchBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.ui.wallet.adapter.GroupsAdapter
import com.lionscare.app.ui.wallet.viewmodel.WalletViewModel
import com.lionscare.app.utils.dialog.ScannerDialog
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupSearchFragment : Fragment(),
    GroupsAdapter.OnClickCallback {

    private var _binding: FragmentGroupSearchBinding? = null
    private val binding get() = _binding!!
    private var groupLinearLayoutManager: LinearLayoutManager? = null
    private var groupsAdapter : GroupsAdapter? = null
    private val activity by lazy { requireActivity() as GroupActivity }
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupSearchBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGroupAdapter()
        setupClickListener()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_group_search))
    }

    private fun setupGroupAdapter() = binding.run {
        groupsAdapter = GroupsAdapter(requireActivity(), this@GroupSearchFragment)
        groupLinearLayoutManager = LinearLayoutManager(requireActivity())
        groupRecyclerView.layoutManager = groupLinearLayoutManager
        groupRecyclerView.adapter = groupsAdapter
    }

    private fun setupClickListener() = binding.run{

        searchEditText.doAfterTextChanged {
            applyTextView.isVisible = it.toString().isNotEmpty()
        }

        activity.getScanImageView().setOnSingleClickListener {
            ScannerDialog.newInstance( object : ScannerDialog.ScannerListener{
                override fun onScannerSuccess(qrValue: String) {
                    //TODO to be updated when QR value is already in QR
                    viewModel.doScanQr(qrValue)
                }
            }, "Scan QR")
                .show(childFragmentManager, ScannerDialog.TAG)
        }

        applyTextView.setOnSingleClickListener {
            viewModel.doSearchUser(searchEditText.text.toString())
            viewModel.doSearchGroup(searchEditText.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGroupItemClickListener(data: GroupData) {
        binding.searchEditText.setText("")
//        findNavController().navigate(WalletSearchFragmentDirections.actionNavigationWalletSearchToNavigationWalletInput())
    }

}