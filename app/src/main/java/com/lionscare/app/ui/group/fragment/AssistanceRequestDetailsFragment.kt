package com.lionscare.app.ui.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lionscare.app.R
import com.lionscare.app.databinding.FragmentGroupAssistanceReqDetailsBinding
import com.lionscare.app.ui.group.activity.GroupActivity
import com.lionscare.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssistanceRequestDetailsFragment : Fragment() {
    private var _binding: FragmentGroupAssistanceReqDetailsBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as GroupActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupAssistanceReqDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        setView()
        onResume()
    }

    override fun onResume() {
        super.onResume()
        activity.setTitlee(getString(R.string.lbl_request_details))
    }

    private fun setView() = binding.run {
        when (activity.getAssistanceDetailsType()){
            APPROVED-> {
                requestLinearLayout.visibility = View.GONE
                approvedTextView.visibility = View.VISIBLE
                declinedTextView.visibility = View.GONE
            }
            DECLINED-> {
                requestLinearLayout.visibility = View.GONE
                approvedTextView.visibility = View.GONE
                declinedTextView.visibility = View.VISIBLE
            }
            else->{
                requestLinearLayout.visibility = View.VISIBLE
                approvedTextView.visibility = View.GONE
                declinedTextView.visibility = View.GONE
            }
        }

    }

    private fun setClickListeners() = binding.run {
//        assistanceLinearLayout.setOnSingleClickListener {
//            findNavController().navigate(GroupManageFragmentDirections.actionNavigationGroupAssistance())
//        }
        approveButton.setOnSingleClickListener {
            activity.onBackPressed()
        }
        declineButton.setOnSingleClickListener {
            activity.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val APPROVED = "APPROVED"
        private const val DECLINED = "DECLINED"
    }
}