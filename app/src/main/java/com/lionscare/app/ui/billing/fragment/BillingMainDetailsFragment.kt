package com.lionscare.app.ui.billing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.billing.response.DonatorData
import com.lionscare.app.databinding.FragmentBillingMainDetailsBinding
import com.lionscare.app.databinding.FragmentHomeBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.ui.billing.adapter.BillingDonatorsAdapter
import com.lionscare.app.ui.billing.viewmodel.BillingViewModel
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingMainDetailsFragment : Fragment() {
    private var _binding: FragmentBillingMainDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BillingViewModel by activityViewModels()


    private var adapter: BillingDonatorsAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    private val activity by lazy { requireActivity() as BillingActivity }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillingMainDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBillingFlow()
        setOnClickListeners()


        //TODO API
        setupAdapter()
        setContentViews()

    }

    private fun setOnClickListeners() {

    }

    //TODO API
    private fun setContentViews() {
        activity.setToolbarTitle("Billing B-000004")
        val sampleDonatorDataList = mutableListOf<DonatorData>()
        sampleDonatorDataList.add(
            DonatorData(
                date = DateModel(date_only_ph = "11/23/2023"),
                user = UserModel(id = "1", name = "Von Denuelle Tandoc"),
                amount = "100,000.00"
            )
        )
        sampleDonatorDataList.add(
            DonatorData(
                date = DateModel(date_only_ph = "11/25/2023"),
                user = UserModel(id = "2", name = "Justin Edriqe Reyes"),
                amount = "300,000.00"
            )
        )
        sampleDonatorDataList.add(
            DonatorData(
                date = DateModel(date_only_ph = "11/30/2023"),
                user = UserModel(id = "3", name = "Pocholo Gopez"),
                amount = "45,000.00"
            )
        )
        // Add more sample data as needed
        val samplePagingData: PagingData<DonatorData> = PagingData.from(sampleDonatorDataList)
        showList(samplePagingData)

        binding.amountDueText.text = "100,000.00"
        binding.totalDonatedText.text = "30,000.00"
        binding.billingBadge.text = "Public"
        binding.dateUploadedText.text = "10/12/2023"
        binding.dueDateText.text = "10/13/2023"
        binding.downloadableBillingText.text = "samepl file.pdf"
    }

    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            else -> Unit
        }
    }

    private fun observeBillingFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.billingSharedFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun showList(donator: PagingData<DonatorData>) {
        adapter?.submitData(viewLifecycleOwner.lifecycle, donator)
    }

    private fun clearList() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
    }

    private fun setupAdapter() = binding.run {
        adapter = BillingDonatorsAdapter()
        swipeRefreshLayout.setOnRefreshListener {
            //todo
        }
        linearLayoutManager = LinearLayoutManager(context)
        donatorsRecyclerView.layoutManager = linearLayoutManager
        donatorsRecyclerView.adapter = adapter
        adapter?.addLoadStateListener { loadState ->
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    placeHolderTextView.isVisible = false
                    skeletonLayout.isVisible = true
                    donatorsRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.Error -> {
                    placeHolderTextView.isVisible = true
                    skeletonLayout.isVisible = false
                    donatorsRecyclerView.isVisible = false
                }

                loadState.source.refresh is LoadState.NotLoading && adapter?.hasData() == true -> {
                    placeHolderTextView.isVisible = false
                    skeletonLayout.isVisible = false
                    skeletonLayout.stopShimmer()
                    donatorsRecyclerView.isVisible = true
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        adapter?.removeLoadStateListener { requireActivity() }
        _binding = null
    }

}