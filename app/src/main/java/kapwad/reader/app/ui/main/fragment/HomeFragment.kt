package kapwad.reader.app.ui.main.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kapwad.reader.app.R
import kapwad.reader.app.data.repositories.baseresponse.UserModel
import kapwad.reader.app.data.repositories.group.response.GroupListData
import kapwad.reader.app.databinding.FragmentHomeBinding
import kapwad.reader.app.ui.geotagging.activity.GeoTaggingActivity
import kapwad.reader.app.ui.group.activity.GroupActivity
import kapwad.reader.app.ui.group.activity.GroupDetailsActivity
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.main.adapter.GroupsYourGroupAdapter
import kapwad.reader.app.ui.main.viewmodel.ImmediateFamilyViewModel
import kapwad.reader.app.ui.main.viewmodel.ImmediateFamilyViewState
import kapwad.reader.app.ui.main.viewmodel.SettingsViewModel
import kapwad.reader.app.ui.main.viewmodel.SettingsViewState
import kapwad.reader.app.ui.phmarket.activity.PHMarketActivity
import kapwad.reader.app.utils.copyToClipboard
import kapwad.reader.app.utils.loadAvatar
import kapwad.reader.app.utils.loadGroupAvatar
import kapwad.reader.app.utils.setOnSingleClickListener
import kapwad.reader.app.utils.setQRv2
import kapwad.reader.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kapwad.reader.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), GroupsYourGroupAdapter.GroupCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var frontAnim: AnimatorSet? = null
    var backAnim: AnimatorSet? = null

    private var immediateFamilyId = ""

    private val iFViewModel: ImmediateFamilyViewModel by viewModels()
    private val viewModel: SettingsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()

        binding.swipeRefreshLayout.setOnRefreshListener(this@HomeFragment)

        //immediately make this view gone
        //not done in xml as this is a reused layout
        //Reason: so it wont show for a second after finishin api call


    }

    override fun onResume() {
        super.onResume()
        hideLoadingDialog()
        binding.mainLayout.idNoTextView.setText(viewModel.user.mrid.toString())
        val firstName = viewModel.user.firstname.orEmpty()
        val middleName = viewModel.user.middlename.orEmpty()
        val lastName = viewModel.user.lastname.orEmpty()

        binding.mainLayout.nameTextView.text = "$firstName $middleName $lastName".trim()

    }






    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity).hideLoadingDialog()
    }



    private fun setClickListeners() = binding.run {
        allConsumerHButton.setOnSingleClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationAllConsumer())
        }

    }


    companion object {
        private const val START_CREATE_FAMILY = "START_CREATE_FAMILY"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: GroupListData) {
        val intent = GroupDetailsActivity.getIntent(requireActivity())
        startActivity(intent)
    }

    override fun onRefresh() {
        viewModel.getProfileDetails()
    }
}