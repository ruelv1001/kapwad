package kapwad.reader.app.ui.wallet.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentWalletDetailsBinding
import kapwad.reader.app.ui.wallet.activity.WalletActivity
import kapwad.reader.app.utils.copyToClipboard
import kapwad.reader.app.utils.currencyFormat
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.loadGroupAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletDetailsFragment : Fragment() {

    private var _binding: FragmentWalletDetailsBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as WalletActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDetails()
        setupClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun setupDetails() = binding.run {
        activity.onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //DO NOTHING
            }
        })
        when(activity.mode){
            "Send Points" -> {
                titleTextView.text = getString(R.string.wallet_send_points_details_title)
                requestedPointsTextView.text = getString(R.string.wallet_sent_points_text)

                if (activity.isGroupId){
                    recipientGroupLayout.adapterLinearLayout.isVisible = true
                    recipientLayout.membersLinearLayout.isGone = true
                    recipientGroupLayout.titleTextView.text = activity.groupData.name
                    recipientGroupLayout.referenceTextView.text = activity.groupData.code
                    recipientGroupLayout.imageView.loadGroupAvatar(activity.groupData.avatar?.thumb_path)
                    recipientGroupLayout.membersTextView.text = "${activity.groupData.member_count.toString()} members"
                }else{
                    recipientGroupLayout.adapterLinearLayout.isGone = true
                    recipientLayout.membersLinearLayout.isVisible = true
                    recipientLayout.nameTextView.text = activity.qrData.name
                    recipientLayout.profileImageView.loadAvatar(activity.qrData.avatar?.thumb_path, requireActivity())
                    //TODO to be updated when display id ready
                    recipientLayout.idNoTextView.isGone = true
                }

                reasonTextView.text = activity.message
                pointsTextView.text = currencyFormat(activity.amount)
                val valueRefNo = activity.transactionData.remarks?.split(": ")
                refidTextView.text = valueRefNo?.get(1)
                dateTextView.text = activity.transactionData.date_registered?.date_db
            }
        }

    }

    private fun setupClickListener() = binding.run{
        closeImageView.setOnSingleClickListener {
            requireActivity().finish()
        }
        copyImageView.setOnSingleClickListener {
            requireActivity().copyToClipboard(refidTextView.text)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}