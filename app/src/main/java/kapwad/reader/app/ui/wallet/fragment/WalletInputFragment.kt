package kapwad.reader.app.ui.wallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentWalletInputBinding
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.utils.removeCommas
import kapwad.reader.app.utils.setAmountFormat
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WalletInputFragment : Fragment() {

    private var _binding: FragmentWalletInputBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletInputBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        setupDetails()
    }

    private fun setupDetails() = binding.run {
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_title)

                recipientEditText.setText(
                    if (activity.isGroupId){
                        activity.groupData.name
                    }else{
                        activity.qrData.name
                    }
                )
                amountEditText.isEnabled = activity.qrData.amount == null
                if(!amountEditText.isEnabled)
                    amountEditText.setText(activity.qrData.amount)
            }

            else -> Unit
        }

    }

    private fun setupClickListener() = binding.run{
        amountEditText.setAmountFormat()

        backImageView.setOnSingleClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        continueButton.setOnSingleClickListener {
            if(amountEditText.text.toString().isNotEmpty() && amountEditText.text.toString().replace(",","").toDouble() != 0.0){
                activity.amount = amountEditText.text.toString().removeCommas()
                activity.message = messageEditText.text.toString()
                findNavController().navigate(WalletInputFragmentDirections.actionNavigationWalletInputToNavigationWalletSummary())
            }else{
             amountEditText.error = "Enter amount"
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}