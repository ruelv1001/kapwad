package da.farmer.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emrekotun.toast.CpmToast
import com.emrekotun.toast.CpmToast.Companion.toastError
import com.emrekotun.toast.CpmToast.Companion.toastSuccess
import da.farmer.app.R
import da.farmer.app.data.repositories.wallet.response.QRData
import da.farmer.app.databinding.FragmentGroupInviteBinding
import da.farmer.app.ui.group.activity.GroupActivity
import da.farmer.app.ui.group.adapter.SearchInviteMemberAdapter
import da.farmer.app.ui.group.dialog.InviteMemberDetailsDialog
import da.farmer.app.ui.group.viewmodel.MemberViewModel
import da.farmer.app.ui.group.viewmodel.MemberViewState
import da.farmer.app.utils.dialog.ScannerDialog
import da.farmer.app.utils.setOnSingleClickListener
import da.farmer.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class GroupInviteFragment : Fragment(), SearchInviteMemberAdapter.SearchCallback {
    private var _binding: FragmentGroupInviteBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }
    private var adapter: SearchInviteMemberAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val viewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupInviteBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setClickListeners()
        setView()
        onResume()
        observeMember()
    }

    private fun observeMember() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberSharedFlow.collectLatest { viewState ->
                handleViewState(viewState)
            }
        }
    }

    private fun handleViewState(viewState: MemberViewState) {
        when (viewState) {
            MemberViewState.Loading -> activity.showLoadingDialog(R.string.loading)
            is MemberViewState.PopupError -> {
                activity.hideLoadingDialog()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }
            is MemberViewState.SuccessSearchUser -> {
                activity.hideLoadingDialog()
                adapter?.clear()
                adapter?.appendData(viewState.listData)
                if (adapter?.getData()?.size == 0) {
                    binding.memberPlaceHolderTextView.isVisible = true
                    binding.recyclerView.isGone = true
                } else {
                    binding.memberPlaceHolderTextView.isGone = true
                    binding.recyclerView.isVisible = true
                }
            }

            is MemberViewState.SuccessInviteMember -> {
                //show toast first before going back
                requireActivity().toastSuccess(viewState.data?.msg.toString(), CpmToast.LONG_DURATION)
                activity.hideLoadingDialog()
                activity.onBackPressedDispatcher.onBackPressed()
            }

            else -> Unit
        }
    }

    private fun setClickListeners() = binding.run {
        activity.getScanImageView().setOnSingleClickListener {
            ScannerDialog.newInstance(object : ScannerDialog.ScannerListener {
                override fun onScannerSuccess(qrValue: String) {
                    val jsonObject: JSONObject
                    val res = qrValue.replace("\\", "")
                    try {
                        jsonObject = JSONObject(res)
                        val type = jsonObject.getString("type")
                        val value = jsonObject.getString("value")
                        viewModel.doSearchUser(value)
                    } catch (e: JSONException) {
                        requireActivity().toastError(getString(R.string.invalid_qr_code_msg), CpmToast.SHORT_DURATION)
                        e.printStackTrace()
                    }
                }
            }, "Scan QR").show(childFragmentManager, ScannerDialog.TAG)
        }

        applyTextView.setOnSingleClickListener {
            viewModel.doSearchUser(searchEditText.text.toString())
        }
    }

    private fun setupAdapter() = binding.run {
        adapter = SearchInviteMemberAdapter(requireActivity(), this@GroupInviteFragment)
        linearLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        activity.setTitlee("Invite to ${activity.groupDetails?.name}")
    }

    private fun setView() = binding.run {
        searchEditText.doAfterTextChanged {
            applyTextView.isVisible = it.toString().isNotEmpty()
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.doSearchUser(searchEditText.text.toString())
                true
            }
            false
        }
    }

    override fun onItemClicked(data: QRData) {
        callInviteMemberDialog(data)
    }

    private fun callInviteMemberDialog(data: QRData) {
        InviteMemberDetailsDialog.newInstance(
            object : InviteMemberDetailsDialog.MembershipCallback {
                override fun onInviteMember(memberListData: QRData) {
                    viewModel.inviteMember(data.id.toString(),activity.groupDetails?.id.toString())
                }

            }, data
        ).show(childFragmentManager, InviteMemberDetailsDialog.TAG)
    }

}