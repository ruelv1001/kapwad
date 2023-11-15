package com.lionscare.app.ui.billing.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.lionscare.app.R
import com.lionscare.app.data.repositories.baseresponse.DateModel
import com.lionscare.app.data.repositories.baseresponse.UserModel
import com.lionscare.app.data.repositories.billing.response.DonatorData
import com.lionscare.app.databinding.FragmentBillingMainDetailsBinding
import com.lionscare.app.ui.billing.activity.BillingActivity
import com.lionscare.app.ui.billing.activity.MyBillingStatementsActivity
import com.lionscare.app.ui.billing.adapter.BillingDonatorsAdapter
import com.lionscare.app.ui.billing.dialog.DonateDialog
import com.lionscare.app.ui.billing.dialog.OptionDonateDialog
import com.lionscare.app.ui.billing.viewmodel.BillingViewModel
import com.lionscare.app.ui.billing.viewstate.BillingViewState
import com.lionscare.app.utils.setOnSingleClickListener
import com.lionscare.app.utils.setQR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingMainDetailsFragment : Fragment() {
    private var _binding: FragmentBillingMainDetailsBinding ? = null
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

    private fun setOnClickListeners() = binding.run {
        amountDueIconImageButton.setOnSingleClickListener {
            findNavController().navigate(BillingMainDetailsFragmentDirections.actionBillingMainDetailsFragmentToAskForDonationsFragment())
        }

        donateButton.setOnSingleClickListener {
            if(viewModel.isRequestFromGroups){
                OptionDonateDialog.newInstance(callback = object : OptionDonateDialog.OptionDonateAmountCallback {
                    override fun onSend(amount: String) {
                        //TODO
                    }
                }, personalWallet = "40000", groupWallet = "30000")
                    .show(childFragmentManager, OptionDonateDialog.TAG)
            }else{
                DonateDialog.newInstance(callback = object : DonateDialog.DonateAmountCallback {
                    override fun onSend(amount: String, isAnonymous: Boolean) {
                        //TODO
                    }
                }, walletBalance = "40000")
                    .show(childFragmentManager, DonateDialog.TAG)
            }

        }
        viewAllTextButton.setOnSingleClickListener {
            findNavController().navigate(BillingMainDetailsFragmentDirections.actionBillingMainDetailsFragmentToAllDonatorsFragment())
        }

        billingBadge.setOnSingleClickListener {
            //TODO remove this later on
            val intent = MyBillingStatementsActivity.getIntent(requireActivity())
            startActivity(intent)
        }

        exitQrIconImage.setOnSingleClickListener {
            val rotationAnimator = backLayout.animate().rotationYBy(-180f).setDuration(600).setInterpolator(
                AccelerateDecelerateInterpolator()
            )
            rotationAnimator.start()

            val fadeOutAnimator = ObjectAnimator.ofFloat(backLayout, View.ALPHA, 1f, 0f)
            fadeOutAnimator.duration = rotationAnimator.duration
            fadeOutAnimator.start()

            // Delay the visibility change until halfway through the animation
            backLayout.postDelayed({
                // Toggle the visibility of front and back layouts
                frontLayout.visibility = if (frontLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                backLayout.visibility = if (backLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE

                frontLayout.alpha = 1f // Reset alpha after visibility change
                frontLayout.rotationY = 0f // Reset rotation
            }, rotationAnimator.duration )
        }

        qrIconImageView.setOnSingleClickListener {
            val rotationAnimator = frontLayout.animate().rotationYBy(180f).setDuration(600).setInterpolator(
                AccelerateDecelerateInterpolator()
            )
            rotationAnimator.start()

            val fadeOutAnimator = ObjectAnimator.ofFloat(frontLayout, View.ALPHA, 1f, 0f)
            fadeOutAnimator.duration = rotationAnimator.duration
            fadeOutAnimator.start()

            // Delay the visibility change until halfway through the animation
            frontLayout.postDelayed({
                // Toggle the visibility of front and back layouts
                frontLayout.visibility = if (frontLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                backLayout.visibility = if (backLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE

                backLayout.alpha = 1f // Reset alpha after visibility change
                backLayout.rotationY = 0f // Reset rotation
            }, rotationAnimator.duration )
        }

    }

    //TODO API
    private fun handleViewState(viewState: BillingViewState) {
        when (viewState) {
            else -> Unit
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setContentViews() {
        activity.setToolbarTitle(viewModel.billingStatementNumber)
        val sampleDonatorDataList = mutableListOf<DonatorData>()
        sampleDonatorDataList.add(
            DonatorData(
                date = DateModel(date_only_ph = "11/23/2023"),
                user = UserModel(id = "1", name = "Von Denuelle Tandoc"),
                amount = "P100,000.00"
            )
        )
        sampleDonatorDataList.add(
            DonatorData(
                date = DateModel(date_only_ph = "11/25/2023"),
                user = UserModel(id = "2", name = "Justin Edriqe Reyes"),
                amount = "P300,000.00"
            )
        )
        sampleDonatorDataList.add(
            DonatorData(
                date = DateModel(date_only_ph = "11/30/2023"),
                user = UserModel(id = "3", name = "Pocholo Gopez"),
                amount = "P45,000.00"
            )
        )

        // Add more sample data as needed
        val samplePagingData: PagingData<DonatorData> = PagingData.from(sampleDonatorDataList)
        showList(samplePagingData)

        binding.qrImageView.setImageBitmap(setQR(requireActivity(), "sasacs sas  as "))
        binding.billingNumberTextView.text = viewModel.billingStatementNumber
        binding.billingNumberInQRText.text = viewModel.billingStatementNumber
        binding.amountDueTextView.text = "P100,000.00"
        binding.totalDonatedTextView.text = "P30,000.00"
        binding.billingBadge.text = "Public"
        binding.dateOfBillingStatementTextView.text = "10.12.2023 | 10:00 am"
        binding.dueDateTextView.text = "10.13.2023"

        handleButtonStatus()
    }

    @SuppressLint("SetTextI18n")
    private fun handleButtonStatus(data: String = "ongoing") {
        when (data) {
            "completed" -> {
                binding.donateButton.text = "Completed"
                val color = ContextCompat.getColor(requireContext(), R.color.color_primary)
                val colorStateList = ColorStateList.valueOf(color)
                binding.donateButton.backgroundTintList = colorStateList
                binding.donateButton.isEnabled = false
            }

            "ongoing" -> {
                binding.donateButton.text = "Donate"
                val color = ContextCompat.getColor(requireContext(), R.color.color_primary)
                val colorStateList = ColorStateList.valueOf(color)
                binding.donateButton.backgroundTintList = colorStateList
                binding.donateButton.isEnabled = true
            }

            "cancelled" -> {
                binding.donateButton.text = "Cancelled"
                val color = ContextCompat.getColor(requireContext(), R.color.light_red)
                val colorStateList = ColorStateList.valueOf(color)
                binding.donateButton.backgroundTintList = colorStateList
                binding.donateButton.isEnabled = false
            }
        }
    }

    private fun showList(donator: PagingData<DonatorData>) {
        adapter?.submitData(viewLifecycleOwner.lifecycle, donator)
    }

    private fun clearList() {
        adapter?.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
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
        adapter?.removeLoadStateListener { requireContext() }
        _binding = null
    }

}