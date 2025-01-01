package kapwad.reader.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kapwad.reader.app.R
import kapwad.reader.app.databinding.FragmentWalletMenuBinding
import kapwad.reader.app.ui.main.activity.MainActivity
import kapwad.reader.app.ui.wallet.adapter.InboundOutboundAdapter
import kapwad.reader.app.ui.wallet.viewmodel.WalletViewModel
import kapwad.reader.app.utils.adapter.CustomViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletMenuFragment : Fragment(){

    private var _binding: FragmentWalletMenuBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: InboundOutboundAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: WalletViewModel by viewModels()
    private var pager2Adapter: CustomViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWalletMenuBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClickListeners()
        setUpTabs()

    }

    private fun setUpTabs() = binding.run {
        pager2Adapter = CustomViewPagerAdapter(childFragmentManager,lifecycle)
        pager2Adapter?.apply {
            addFragment(WalletDAFragment.newInstance())
            addFragment(WalletDBPFragment.newInstance())
            addFragment(WalletLoyaltyPointsFragment.newInstance())
        }

        optionViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        optionViewPager.offscreenPageLimit = 2
        optionViewPager.adapter = pager2Adapter
        val statusList = arrayListOf(
            getString(R.string.da),
            getString(R.string.dbp),
            getString(R.string.loyalty_points),
        )


        TabLayoutMediator(walletTabLayout, optionViewPager) { tab, position ->
            tab.text = statusList[position]
        }.attach()


        optionViewPager.registerOnPageChangeCallback(viewPager2PageCallback())


    }
    private fun viewPager2PageCallback() : ViewPager2.OnPageChangeCallback{
        return object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentFragment = getCurrentFragment(position)

            }
        }
    }

    private fun getCurrentFragment(position: Int): Fragment?{
        return childFragmentManager.findFragmentByTag("f$position")
    }




    private fun setClickListeners() = binding.run {

    }





    companion object{

    }

}