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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dswd.ziacare.app.R
import dswd.ziacare.app.databinding.FragmentDeactivateOrDeleteFormBinding
import dswd.ziacare.app.ui.accountcontrol.activity.AccountControlActivity
import dswd.ziacare.app.utils.setOnSingleClickListener

class DeactivateOrDeleteFormFragment : Fragment()  {
    private var _binding : FragmentDeactivateOrDeleteFormBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy { requireActivity() as AccountControlActivity }

    val hidingAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, -1f
    ).apply {
        duration = 400
    }

    val showingAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, 0f,
        Animation.RELATIVE_TO_SELF, -1f,
        Animation.RELATIVE_TO_SELF, 0f
    ).apply {
        duration = 400
    }
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
        leavingTemporarilyCardView.setOnSingleClickListener {
            resetRadioButtonState(leavingTemporarilyRadio)
            leavingTemporarilyRadio.isChecked =  !leavingTemporarilyRadio.isChecked
        }
        safetyPrivacyConcernsCardView.setOnSingleClickListener {
            resetRadioButtonState(safetyPrivacyConcernsRadio)
            safetyPrivacyConcernsRadio.isChecked =  !safetyPrivacyConcernsRadio.isChecked
        }
        troubleGettingStartedCardView.setOnSingleClickListener {
            resetRadioButtonState(troubleGettingStartedRadio)
            troubleGettingStartedRadio.isChecked =  !troubleGettingStartedRadio.isChecked
        }
        multipleAccountsCardView.setOnSingleClickListener {
            resetRadioButtonState(multipleAccountsRadio)
            multipleAccountsRadio.isChecked =  !multipleAccountsRadio.isChecked
        }
        unableToTransactCardView.setOnSingleClickListener {
            resetRadioButtonState(unableToTransactRadio)
            unableToTransactRadio.isChecked =  !unableToTransactRadio.isChecked
        }
        anotherReasonCardView.setOnSingleClickListener {
            resetRadioButtonState(anotherReasonRadio)
            anotherReasonRadio.isChecked =  !anotherReasonRadio.isChecked

            if (anotherReasonRadio.isChecked){
                reasonTextInputLayout.animation = showingAnimation
                reasonTextInputLayout.isVisible = true
                reasonTextInputLayout.startAnimation(showingAnimation)
            }else{
                hidingAnimation.setAnimationListener(createAnimationListener())
                reasonTextInputLayout.animation = hidingAnimation
                reasonTextInputLayout.startAnimation(hidingAnimation)
            }
        }

        continueButton.setOnSingleClickListener {
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

    private fun createAnimationListener(): Animation.AnimationListener {
        return object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.reasonTextInputLayout.isVisible = false
                hideSoftKeyboard()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        }
    }
    private fun hideSoftKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun resetRadioButtonState(excludeCheckBox : CheckBox) = binding.run{
        if(excludeCheckBox != anotherReasonRadio){
            //reset the text layout
            hidingAnimation.setAnimationListener(createAnimationListener())
            reasonTextInputLayout.animation = hidingAnimation
            reasonTextInputLayout.startAnimation(hidingAnimation)
        }

        val checkBoxes = listOf(
            leavingTemporarilyRadio,
            safetyPrivacyConcernsRadio,
            troubleGettingStartedRadio,
            multipleAccountsRadio,
            unableToTransactRadio,
            anotherReasonRadio,
        )

        checkBoxes.forEach { checkBox ->
            if (checkBox != excludeCheckBox) {
                checkBox.isChecked = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}