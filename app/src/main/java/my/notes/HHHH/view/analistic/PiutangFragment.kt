package my.notes.HHHH.view.analistic

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import my.notes.HHHH.R
import my.notes.HHHH.databinding.FragmentPiutangBinding
import my.notes.HHHH.viewmodel.DataViewModel
import org.eazegraph.lib.models.PieModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PiutangFragment : Fragment() {

    val viewModel: DataViewModel by activityViewModel()

    private var _binding: FragmentPiutangBinding? = null
    private val binding get() = _binding!!

    private var jumlahHariDipinjam = 0
    private var jumlahMingguDipinjam = 0
    private var jumlahBulanDipinjam = 0
    private var jumlahTahunDipinjam = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPiutangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpData() {
        val outputFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val inputParser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val now = Date()
        val todayInt = outputFormatter.format(now).toIntOrNull() ?: 0

        val cal = Calendar.getInstance()
        cal.time = now
        cal.add(Calendar.DATE, -7)
        val oneWeekAgoInt = outputFormatter.format(cal.time).toIntOrNull() ?: 0

        cal.time = now
        cal.add(Calendar.MONTH, -1)
        val oneMonthAgoInt = outputFormatter.format(cal.time).toIntOrNull() ?: 0

        cal.time = now
        cal.add(Calendar.YEAR, -1)
        val oneYearAgoInt = outputFormatter.format(cal.time).toIntOrNull() ?: 0

        viewModel.getAllDataWhereStatusData(statusData = false, jenis = true).observe(viewLifecycleOwner) { dataList ->
            jumlahHariDipinjam = 0
            jumlahMingguDipinjam = 0
            jumlahBulanDipinjam = 0
            jumlahTahunDipinjam = 0

            for (item in dataList) {
                val parsedDate = try {
                    inputParser.parse(item.diBuat)
                } catch (e: Exception) {
                    null
                }
                val tglDibuat = parsedDate?.let { outputFormatter.format(it).toIntOrNull() } ?: 0

                if (tglDibuat == todayInt) {
                    jumlahHariDipinjam += item.jumlah
                }
                if (tglDibuat in (oneWeekAgoInt + 1)..todayInt) {
                    jumlahMingguDipinjam += item.jumlah
                }
                if (tglDibuat in (oneMonthAgoInt + 1)..todayInt) {
                    jumlahBulanDipinjam += item.jumlah
                }
                if (tglDibuat in (oneYearAgoInt + 1)..todayInt) {
                    jumlahTahunDipinjam += item.jumlah
                }
            }

            bindView()
            addPieChart()
        }
    }

    private fun bindView() {
        val decimalFormat = DecimalFormat("##,###,###")
        binding.tvHariDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlahHariDipinjam)
        binding.tvMingguDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlahMingguDipinjam)
        binding.tvBulanDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlahBulanDipinjam)
        binding.tvTahunDipinjam.text = getString(R.string.dolar) + " " + decimalFormat.format(jumlahTahunDipinjam)
    }

    private fun addPieChart() {
        binding.piechart.clearChart()
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.hari_dibayar),
                jumlahHariDipinjam.toFloat(),
                Color.parseColor("#FFA726")
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.minggu_dibayar),
                jumlahMingguDipinjam.toFloat(),
                Color.parseColor("#66BB6A")
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.bulan_dibayar),
                jumlahBulanDipinjam.toFloat(),
                Color.parseColor("#EF5350")
            )
        )
        binding.piechart.addPieSlice(
            PieModel(
                getString(R.string.tahun_dibayar),
                jumlahTahunDipinjam.toFloat(),
                Color.parseColor("#29B6F6")
            )
        )
        binding.piechart.startAnimation()
    }

    companion object {
        @JvmStatic
        fun newInstance() = PiutangFragment().apply {
            arguments = Bundle()
        }
    }
}
