package dswd.ziacare.app.ui.accountcontrol.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dswd.ziacare.app.R
import dswd.ziacare.app.data.repositories.auth.response.ReasonsData
import dswd.ziacare.app.databinding.FragmentDeactivateOrDeleteFormBinding
import dswd.ziacare.app.ui.accountcontrol.activity.AccountControlActivity
import dswd.ziacare.app.ui.accountcontrol.adapter.ReasonsLOVAdapter
import dswd.ziacare.app.ui.accountcontrol.viewmodel.AccountControlViewModel
import dswd.ziacare.app.ui.accountcontrol.viewmodel.AccountControlViewState
import dswd.ziacare.app.utils.setOnSingleClickListener
import dswd.ziacare.app.utils.showPopupError
import dswd.ziacare.app.utils.showToastError
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeactivateOrDeleteFormFragment : Fragment(), ReasonsLOVAdapter.ReasonCallback  {
    private var _binding : FragmentDeactivateOrDeleteFormBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as AccountControlActivity }
    private var adapter: ReasonsLOVAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private val viewModel : AccountControlViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeactivateOrDeleteFormBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setupList()
        observeDeleteOrDeactivate()
        viewModel.getReasonsList()
    }

    override fun onResume() {
        super.onResume()
        when(activity.selectedChoice){
            AccountControlActivity.DEACTIVATE -> {
                activity.setToolbarTitle(getString(R.string.deactivate_account))
            }
            AccountControlActivity.DELETE -> {
                activity.setToolbarTitle(getString(R.string.delete_account_permanently))
            }
        }
    }
    private fun setOnClickListeners() = binding.run {
        continueButton.setOnSingleClickListener {
            if(activity.reasonId == 6 && reasonEditText.text.toString().isEmpty()){
                reasonEditText.error = "Field Required."
            }else if(activity.reasonId == 0){
                showToastError(requireActivity(), "Error","Please choose a reason")
            }else{
                if(activity.reasonId == 6){
                    activity.reason = binding.reasonEditText.text.toString()
                }
                else{
                    activity.reason = ""
                }
                when(activity.selectedChoice){
                    AccountControlActivity.DEACTIVATE -> {
                        findNavController().navigate(DeactivateOrDeleteFormFragmentDirections.actionDeactivateOrDeleteFormFragmentToDeactivateFragment())
                    }
                    AccountControlActivity.DELETE -> {
                        findNavController().navigate(DeactivateOrDeleteFormFragmentDirections.actionDeactivateOrDeleteFormFragmentToDeleteAccountFragment())
                    }
                }
            }
        }
    }

    private fun setupList() {
        binding.apply {
            adapter = ReasonsLOVAdapter(this@DeactivateOrDeleteFormFragment)
            layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

    private fun observeDeleteOrDeactivate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accountControlSharedFlow.collectLatest { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }
    private fun handleViewState(viewState: AccountControlViewState) {
        when (viewState) {
            is AccountControlViewState.Loading -> {
                binding.recyclerView.isGone = true
                binding.progressContainer.isVisible = true
            }
            is AccountControlViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )

            }
            is AccountControlViewState.SuccessGetReasons -> {
                binding.recyclerView.isVisible = true
                binding.progressContainer.isGone = true

                viewState.response.data?.let { adapter?.appendData(it) }
            }
            else -> Unit
        }
    }

    override fun onItemClicked(data: ReasonsData, position: Int) {
        if(data.id != 6 &&  binding.reasonTextInputLayout.isVisible ){
            hideSoftKeyboard()
        }

        adapter?.setSelectedItem(data)
        activity.reason = data.reason
        activity.reasonId = data.id?:0
        binding.reasonTextInputLayout.isVisible = activity.reasonId == 6
        binding.reasonEditText.setText("")
    }

    private fun hideSoftKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}