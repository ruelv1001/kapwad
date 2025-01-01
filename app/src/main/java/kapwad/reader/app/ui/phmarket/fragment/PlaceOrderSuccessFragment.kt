    package kapwad.reader.app.ui.phmarket.fragment

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import kapwad.reader.app.databinding.FragmentPlaceOrderSuccessBinding
    import kapwad.reader.app.ui.crops.activity.CropsActivity
    import kapwad.reader.app.ui.crops.adapter.DateListAdapter
    import kapwad.reader.app.ui.main.activity.MainActivity
    import dagger.hilt.android.AndroidEntryPoint

    @AndroidEntryPoint
    class PlaceOrderSuccessFragment : Fragment(){

        private var _binding: FragmentPlaceOrderSuccessBinding? = null
        private val binding get() = _binding!!
        private val activity by lazy { requireActivity() as CropsActivity }
        private var adapter: DateListAdapter? = null
        private var layoutManager: LinearLayoutManager? = null
    
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View {
            _binding = FragmentPlaceOrderSuccessBinding.inflate(
                inflater,
                container,
                false
            )
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setClickListeners()
        }

        private fun setClickListeners() = binding.run {
            doneTextView.setOnClickListener {
                val intent = MainActivity.getIntent(requireActivity())
                startActivity(intent)
            }


        }



    }