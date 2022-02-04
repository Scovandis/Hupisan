package com.example.hhhh.view.lunas

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hhhh.R
import com.example.hhhh.database.DataViewModel
import com.example.hhhh.database.entity.DataModel
import com.example.hhhh.databinding.FragmentWindowLunasBinding
import com.example.hhhh.listener.OnBottomSheetCallback
import com.example.hhhh.view.lunas.adapter.LunasAdapter
import com.example.hhhh.view.main.adapter.AutoSliderAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WindowFragmentLunas : BottomSheetDialogFragment(), OnBottomSheetCallback {
    lateinit var binding: FragmentWindowLunasBinding
    private var currentState: Int = BottomSheetBehavior.STATE_EXPANDED
    private lateinit var viewModel: DataViewModel
    private lateinit var lunasAdapter: LunasAdapter
    lateinit var listData: ArrayList<DataModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWindowLunasBinding.inflate(inflater, container, false)
        (activity as LunasActivity).setOnBottomSheetCallbacks(this)
        binding.filterImage.setColorFilter(Color.parseColor("#FF6200EE"))
        viewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)
        lunasAdapter = LunasAdapter(binding.root.context)
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
        viewModel.getAllDataWhereStatusLunas(getString(R.string.lunas)).observe(viewLifecycleOwner, Observer {
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
                it.forEach {
                    jumlah++
                }
                binding.textResult.text = jumlah.toString() + " " + getString(R.string.hasil)
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
    private fun setDataFilter(typeJenis: String){
        binding.rvLunas.visibility = View.GONE
        binding.lottieAnimation.visibility = View.GONE
        binding.shimmer1.visibility = View.VISIBLE
        binding.shimmer1.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getAllDataWhereStatusData(getString(R.string.lunas), typeJenis).observe(viewLifecycleOwner, Observer {
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

                    it.forEach {
                        jumlah++
                    }
                    binding.textResult.text = jumlah.toString() + " " + getString(R.string.hasil)
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
            setDataFilter(getString(R.string.hutang))
        }else if (LunasActivity.STATUS_TYPE == getString(R.string.piutang)){
            setDataFilter(getString(R.string.piutang))
        }else{
            LunasActivity.STATUS_TYPE == null
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        currentState = newState
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                filterData()
                binding.textResult.text = jumlah.toString() +" "+ getString(R.string.hasil)
                binding.filterImage.setImageResource(R.drawable.ic_baseline_filter_list_white_24)
                binding.filterImage.setColorFilter(Color.parseColor("#FF6200EE"))
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                jumlah = 0
                binding.textResult.text = getString(R.string.lihat_hasilnya)
                binding.filterImage.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                binding.filterImage.setColorFilter(Color.parseColor("#FF6200EE"))
            }
            //todo: when the bottom sheet is moving update data
        }
    }

    companion object{
        var STAY_LUNAS = 0
    }

}