package kapwad.reader.app.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
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
import kapwad.reader.app.data.model.ProductListModelData
import kapwad.reader.app.data.model.SyncListModelData
import kapwad.reader.app.databinding.FragmentSyncListBinding
import kapwad.reader.app.databinding.FragmentSyncMenuBinding
import kapwad.reader.app.ui.main.adapter.SyncListAdapter
import kapwad.reader.app.ui.phmarket.adapter.PreOrderListAdapter

@AndroidEntryPoint
class SyncMenuFragment : Fragment(),SyncListAdapter.DateCallback {

    private var _binding: FragmentSyncListBinding? = null
    private val binding get() = _binding!!

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: SyncListAdapter? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private val viewModel: WalletViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSyncListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClickListeners()
        setupList()

    }


    private fun setupList() {
        binding?.apply {
            adapter = SyncListAdapter(requireActivity(), this@SyncMenuFragment)
            linearLayoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter
            val syncListModelData = listOf(
                SyncListModelData(
                    "1",
                    "December 2, 2024",
                    "Not Uploaded",
                    "Not Upload",
                    ),

                SyncListModelData(
                    "2",
                    "November 2, 2024",
                    "Updated",
                    "Uploaded",
                ),

                SyncListModelData(
                    "3",
                    "October 2, 2024",
                    "Not Uploaded",
                    "Not Upload",
                ),

                SyncListModelData(
                    "4",
                    "September 2, 2024",
                    "Updated",
                    "Uploaded",
                ),

            )
            adapter?.appendData(syncListModelData)
        }
    }


    private fun setClickListeners() = binding.run {

    }


    companion object {

    }

    override fun onItemClicked(data: SyncListModelData, position: Int) {

    }

}