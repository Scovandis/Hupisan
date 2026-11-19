package com.hupisan.view.lunas

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import com.hupisan.viewmodel.DataViewModel
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.FragmentWindowLunasBinding
import com.hupisan.listener.OnBottomSheetCallback
import com.hupisan.view.lunas.adapter.LunasAdapter

class WindowFragmentLunas : BottomSheetDialogFragment(), OnBottomSheetCallback {
    private var _binding: FragmentWindowLunasBinding? = null
    private val binding get() = _binding!!

    private var currentState: Int = BottomSheetBehavior.STATE_EXPANDED
    private val viewModel: DataViewModel by activityViewModel()
    private lateinit var lunasAdapter: LunasAdapter
    private var listData = ArrayList<DataModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWindowLunasBinding.inflate(inflater, container, false)
        (activity as? LunasActivity)?.setOnBottomSheetCallbacks(this)
        binding.filterImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.hupisan_primary_dark))
        lunasAdapter = LunasAdapter(requireContext())
        binding.rvLunas.visibility = View.GONE
        binding.lottieAnimation.visibility = View.GONE
        binding.tvIsEmpty.visibility = View.GONE
        binding.shimmer1.startShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            setDataFirstOpen()
            initRecyclerview()
        }, 800)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initRecyclerview(){
        binding.rvLunas.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = lunasAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        binding.shimmer1.startShimmer()
    }

    override fun onPause() {
        binding.shimmer1.stopShimmer()
        super.onPause()
    }
    private fun setDataFirstOpen(){
        viewModel.getAllDataWhereStatusLunas(true).observe(viewLifecycleOwner, Observer {
            binding.shimmer1.stopShimmer()
            binding.shimmer1.visibility = View.GONE
            if (it.isEmpty()){
                binding.lottieAnimation.visibility = View.VISIBLE
                binding.tvIsEmpty.visibility = View.VISIBLE
                binding.rvLunas.visibility = View.GONE
            }else{
                binding.lottieAnimation.visibility = View.GONE
                binding.tvIsEmpty.visibility = View.GONE
                binding.rvLunas.visibility = View.VISIBLE
                jumlah = it.size
                binding.textResult.text = "$jumlah ${getString(R.string.hasil)}"
                listData = it as ArrayList<DataModel>
                lunasAdapter.setData(listData)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textResult.setOnClickListener {
            (activity as LunasActivity).openBottomSheet()
        }

        binding.filterImage.setOnClickListener {
            if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                (activity as LunasActivity).closeBottomSheet()
            } else  {
                (activity as LunasActivity).openBottomSheet()
            }
        }
    }
    var jumlah: Int = 0
    private fun setDataFilter(typeJenis: Boolean){
        binding.rvLunas.visibility = View.GONE
        binding.lottieAnimation.visibility = View.GONE
        binding.shimmer1.visibility = View.VISIBLE
        binding.shimmer1.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getAllDataWhereStatusData(true, typeJenis).observe(viewLifecycleOwner, Observer {
                binding.shimmer1.stopShimmer()
                binding.shimmer1.visibility = View.GONE
                if (it.isEmpty()){
                    binding.lottieAnimation.visibility = View.VISIBLE
                    binding.tvIsEmpty.visibility = View.VISIBLE
                    binding.rvLunas.visibility = View.GONE
                }else{
                    binding.lottieAnimation.visibility =View.GONE
                    binding.tvIsEmpty.visibility = View.GONE
                    binding.rvLunas.visibility = View.VISIBLE

                    jumlah = it.size
                    binding.textResult.text = "$jumlah ${getString(R.string.hasil)}"
                    listData = it as ArrayList<DataModel>
                    lunasAdapter.setData(listData)
                }
            })
        },800)
        initRecyclerview()
    }
    private fun filterData(){
        if (LunasActivity.STATUS_TYPE == null){
            binding.shimmer1.visibility = View.VISIBLE
            binding.shimmer1.startShimmer()
            binding.rvLunas.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                setDataFirstOpen()
            }, 800)
        }else if (LunasActivity.STATUS_TYPE == getString(R.string.hutang)){
            setDataFilter(false)
        }else if (LunasActivity.STATUS_TYPE == getString(R.string.piutang)){
            setDataFilter(true)
        }else{
            LunasActivity.STATUS_TYPE = null
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        currentState = newState
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                filterData()
                binding.textResult.text = jumlah.toString() +" "+ getString(R.string.hasil)
                binding.filterImage.setImageResource(R.drawable.ic_baseline_filter_list_white_24)
                binding.filterImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.hupisan_primary_dark))
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                jumlah = 0
                binding.textResult.text = getString(R.string.lihat_hasilnya)
                binding.filterImage.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                binding.filterImage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.hupisan_primary_dark))
            }
        }
    }

    companion object{
        var STAY_LUNAS = 0
    }

}