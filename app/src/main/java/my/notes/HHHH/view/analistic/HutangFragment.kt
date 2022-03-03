package my.notes.HHHH.view.analistic

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import my.notes.HHHH.databinding.FragmentHutangBinding
import my.notes.HHHH.R
import org.eazegraph.lib.models.PieModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class HutangFragment : Fragment() {
    private val TAG = HutangFragment::class.java.simpleName
    lateinit var viewModel: my.notes.HHHH.database.DataViewModel
    lateinit var binding: FragmentHutangBinding

    var jumlah_hari_dipinjam = 0
    var jumlah_minggu_dipinjam = 0
    var jumlah_bulan_dipinjam = 0
    var jumlah_tahun_dipinjam = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHutangBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(my.notes.HHHH.database.DataViewModel::class.java)
        setUpData()
        return binding.root
    }

    private fun setUpData(){
            //time now
            val formatter = SimpleDateFormat("yyyy/MM/dd")
            val timeNow = SimpleDateFormat("yyyy/MM/dd").format(Date()).replace("/", "").trim().toInt()
            Log.d(TAG, "TIMENOW $timeNow")

            //minggu
            val calendar1 = Calendar.getInstance()
            calendar1.add(Calendar.DATE, -7)
            val afterOneWeek = calendar1.time

            Log.d(TAG, "NEW DATE BERFOR 7 Days : $afterOneWeek")

            //bulan
            val month = Calendar.getInstance()
            month.add(Calendar.MONTH, -1) // Added one month
            val oneMonthBefor = month.time
            Log.d(TAG, "ONEMONTHFROM NOW $oneMonthBefor")

            //tahun
            val year = Calendar.getInstance()
            year.add(Calendar.YEAR, -1)
            val oneYearAgo = year.time
            Log.d(TAG, "ONE YEAR AGO $oneYearAgo")

            viewModel.getAllDataWhereStatusData(statusData = false, jenis = false).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                for (i in it){
                    val perserI = SimpleDateFormat("dd/MM/yyyy")
                    val tgl_dibuat = formatter.format(perserI.parse("${i.diBuat}")).replace("/", "").trim().toInt()

                    val parser =  SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)

                    val formatDateOneWeek = formatter.format(parser.parse("$afterOneWeek")).replace("/", "").trim().toInt()

                    val formatDateOneMonth = formatter.format(parser.parse("$oneMonthBefor")).replace("/", "").trim().toInt()

                    val formatOneYearAgo = formatter.format(parser.parse("$oneYearAgo")).replace("/", "").trim().toInt()

                    if (tgl_dibuat == timeNow){
                        jumlah_hari_dipinjam += i.jumlah
//                        jumlah_hari_dibayar += i.jumlah_dibayar
                    }
                    if (tgl_dibuat > formatDateOneWeek && tgl_dibuat <= timeNow){
                        jumlah_minggu_dipinjam += i.jumlah
//                        jumlah_minggu_dibayar += i.jumlah_dibayar
                    }
                    if (tgl_dibuat > formatDateOneMonth && tgl_dibuat <= timeNow){
                        jumlah_bulan_dipinjam += i.jumlah
//                        jumlah_bulan_dibayar += i.jumlah_dibayar
                    }
                    if (tgl_dibuat > formatOneYearAgo && tgl_dibuat <= timeNow){
                        jumlah_tahun_dipinjam += i.jumlah
//                        jumlah_tahun_dibayar += i.jumlah_dibayar
                    }
                }
                Log.d(TAG, "HARI $jumlah_hari_dipinjam")
                Log.d(TAG, "MINGGU $jumlah_minggu_dipinjam")
                Log.d(TAG, "BULAN $jumlah_bulan_dipinjam")
                Log.d(TAG, "TAHUN $jumlah_tahun_dipinjam")
//                Log.d(TAG, "HARI $jumlah_hari_dibayar")
//                Log.d(TAG, "MINGGU $jumlah_minggu_dibayar")
//                Log.d(TAG, "BULAN $jumlah_bulan_dibayar")
//                Log.d(TAG, "TAHUN $jumlah_tahun_dibayar")

                bindView()
                addPieChart()
            })
    }
    private fun bindView(){
        val decimalFormat = DecimalFormat("##,###,###")
        with(binding){
            tvHariDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlah_hari_dipinjam)
            tvMingguDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlah_minggu_dipinjam)
            tvBulanDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlah_bulan_dipinjam)
            tvTahunDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlah_tahun_dipinjam)

//            tvHariDibayar.text = getString(com.example.hhhh.R.string.dolar) + " " + decimalFormat.format(jumlah_hari_dibayar)
//            tvMingguDibayar.text = getString(com.example.hhhh.R.string.dolar) + " " + decimalFormat.format(jumlah_minggu_dibayar)
//            tvBulanDibayar.text = getString(com.example.hhhh.R.string.dolar) + " " + decimalFormat.format(jumlah_bulan_dibayar)
//            tvTahunDibayar.text = getString(com.example.hhhh.R.string.dolar) + " " + decimalFormat.format(jumlah_tahun_dibayar)
        }
    }
    private fun addPieChart(){
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.hari_dibayar), jumlah_hari_dipinjam.toFloat(),
                Color.parseColor("#FFA726")
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.minggu_dibayar), jumlah_minggu_dipinjam.toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.bulan_dibayar), jumlah_bulan_dipinjam.toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.tahun_dibayar), jumlah_tahun_dipinjam.toFloat(),
                Color.parseColor("#29B6F6")
            )
        )
        binding.piechart.startAnimation()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HutangFragment().apply {
                arguments = Bundle()
            }
    }
}