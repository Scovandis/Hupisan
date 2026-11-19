package com.hupisan.view.statistic

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityStatisticBinding
import com.hupisan.viewmodel.DataViewModel
import org.eazegraph.lib.models.PieModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticBinding
    private val viewModel: DataViewModel by viewModel()

    private var currentFilterMode = 0 // 0: Bulan Ini, 1: 5 Bulan, 2: 6 Bulan
    private var allDataList: List<DataModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_card_bg)

        setupToolbar()
        setupFilterPills()
        observeData()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCalendar.setOnClickListener {
            val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            Toast.makeText(this, "Periode aktif: ${dateFormat.format(Date())}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFilterPills() {
        binding.btnFilterMonth.setOnClickListener {
            setFilterMode(0)
        }

        binding.btnFilter5Month.setOnClickListener {
            setFilterMode(1)
        }

        binding.btnFilter6Month.setOnClickListener {
            setFilterMode(2)
        }

        updateFilterPillsUI()
    }

    private fun setFilterMode(mode: Int) {
        currentFilterMode = mode
        updateFilterPillsUI()
        computeAndRenderStatistics()
    }

    private fun updateFilterPillsUI() {
        val activeBg = ContextCompat.getDrawable(this, R.drawable.bg_filter_pill_active)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg_filter_pill_inactive)
        val activeTextColor = ContextCompat.getColor(this, R.color.white)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.hupisan_text_secondary)

        binding.btnFilterMonth.background = if (currentFilterMode == 0) activeBg else inactiveBg
        binding.btnFilterMonth.setTextColor(if (currentFilterMode == 0) activeTextColor else inactiveTextColor)

        binding.btnFilter5Month.background = if (currentFilterMode == 1) activeBg else inactiveBg
        binding.btnFilter5Month.setTextColor(if (currentFilterMode == 1) activeTextColor else inactiveTextColor)

        binding.btnFilter6Month.background = if (currentFilterMode == 2) activeBg else inactiveBg
        binding.btnFilter6Month.setTextColor(if (currentFilterMode == 2) activeTextColor else inactiveTextColor)
    }

    private fun observeData() {
        viewModel.allData.observe(this) { list ->
            allDataList = list ?: emptyList()
            computeAndRenderStatistics()
        }
    }

    private fun computeAndRenderStatistics() {
        val parser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calNow = Calendar.getInstance()
        val currentMonth = calNow.get(Calendar.MONTH)
        val currentYear = calNow.get(Calendar.YEAR)

        // Filter data based on currentFilterMode
        val filteredList = allDataList.filter { item ->
            val date = try {
                parser.parse(item.diBuat)
            } catch (e: Exception) {
                null
            }

            if (date == null) {
                true
            } else {
                val itemCal = Calendar.getInstance().apply { time = date }
                when (currentFilterMode) {
                    0 -> { // Bulan Ini
                        itemCal.get(Calendar.MONTH) == currentMonth && itemCal.get(Calendar.YEAR) == currentYear
                    }
                    1 -> { // 5 Bulan
                        val diffMonths = (currentYear - itemCal.get(Calendar.YEAR)) * 12 + (currentMonth - itemCal.get(Calendar.MONTH))
                        diffMonths in 0..4
                    }
                    2 -> { // 6 Bulan
                        val diffMonths = (currentYear - itemCal.get(Calendar.YEAR)) * 12 + (currentMonth - itemCal.get(Calendar.MONTH))
                        diffMonths in 0..5
                    }
                    else -> true
                }
            }
        }

        // 1. Summary Cards
        val totalHutang = filteredList.filter { !it.jenisPinjaman }.sumOf { it.jumlah }
        val totalPiutang = filteredList.filter { it.jenisPinjaman }.sumOf { it.jumlah }

        val decimalFormat = DecimalFormat("##,###,###")
        binding.tvStatTotalHutang.text = "Rp " + decimalFormat.format(totalHutang)
        binding.tvStatTotalPiutang.text = "Rp " + decimalFormat.format(totalPiutang)

        // 2. Donut Chart
        val total = totalHutang + totalPiutang
        val hutangPercent = if (total > 0) ((totalHutang.toDouble() / total) * 100).toInt() else 0
        val piutangPercent = if (total > 0) (100 - hutangPercent) else 0

        binding.tvDonutPercentage.text = "$hutangPercent%"
        binding.tvDonutLabel.text = "Hutang"
        binding.tvLegendHutangPercent.text = "$hutangPercent%"
        binding.tvLegendPiutangPercent.text = "$piutangPercent%"

        binding.piechart.clearChart()
        if (totalHutang > 0 || totalPiutang > 0) {
            if (totalHutang > 0) {
                binding.piechart.addPieSlice(
                    PieModel("Hutang", totalHutang.toFloat(), Color.parseColor("#22A872"))
                )
            }
            if (totalPiutang > 0) {
                binding.piechart.addPieSlice(
                    PieModel("Piutang", totalPiutang.toFloat(), Color.parseColor("#A3E8D0"))
                )
            }
        } else {
            binding.piechart.addPieSlice(
                PieModel("Kosong", 1f, Color.parseColor("#E2E8F0"))
            )
        }
        binding.piechart.startAnimation()

        // 3. Trend Transaksi (Bars for last 3 months)
        renderTrendBars()
    }

    private fun renderTrendBars() {
        val parser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val monthLabelFormat = SimpleDateFormat("MMM", Locale("id", "ID"))

        val months = ArrayList<Calendar>()
        for (i in 2 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.MONTH, -i)
            months.add(c)
        }

        val m1 = months[0]
        val m2 = months[1]
        val m3 = months[2]

        binding.tvLabelM1.text = monthLabelFormat.format(m1.time)
        binding.tvLabelM2.text = monthLabelFormat.format(m2.time)
        binding.tvLabelM3.text = monthLabelFormat.format(m3.time)

        var hutangM1 = 0
        var piutangM1 = 0
        var hutangM2 = 0
        var piutangM2 = 0
        var hutangM3 = 0
        var piutangM3 = 0

        for (item in allDataList) {
            val date = try {
                parser.parse(item.diBuat)
            } catch (e: Exception) {
                null
            } ?: continue

            val c = Calendar.getInstance().apply { time = date }
            val itemMonth = c.get(Calendar.MONTH)
            val itemYear = c.get(Calendar.YEAR)

            if (itemMonth == m1.get(Calendar.MONTH) && itemYear == m1.get(Calendar.YEAR)) {
                if (item.jenisPinjaman) piutangM1 += item.jumlah else hutangM1 += item.jumlah
            } else if (itemMonth == m2.get(Calendar.MONTH) && itemYear == m2.get(Calendar.YEAR)) {
                if (item.jenisPinjaman) piutangM2 += item.jumlah else hutangM2 += item.jumlah
            } else if (itemMonth == m3.get(Calendar.MONTH) && itemYear == m3.get(Calendar.YEAR)) {
                if (item.jenisPinjaman) piutangM3 += item.jumlah else hutangM3 += item.jumlah
            }
        }

        val hasData = (hutangM1 + piutangM1 + hutangM2 + piutangM2 + hutangM3 + piutangM3) > 0
        if (!hasData) {
            setBarHeightDp(binding.barHutangM1, 35)
            setBarHeightDp(binding.barPiutangM1, 55)
            setBarHeightDp(binding.barHutangM2, 50)
            setBarHeightDp(binding.barPiutangM2, 30)
            setBarHeightDp(binding.barHutangM3, 70)
            setBarHeightDp(binding.barPiutangM3, 45)
        } else {
            val maxAmount = maxOf(hutangM1, piutangM1, hutangM2, piutangM2, hutangM3, piutangM3)
            setBarHeight(binding.barHutangM1, hutangM1, maxAmount)
            setBarHeight(binding.barPiutangM1, piutangM1, maxAmount)
            setBarHeight(binding.barHutangM2, hutangM2, maxAmount)
            setBarHeight(binding.barPiutangM2, piutangM2, maxAmount)
            setBarHeight(binding.barHutangM3, hutangM3, maxAmount)
            setBarHeight(binding.barPiutangM3, piutangM3, maxAmount)
        }
    }

    private fun setBarHeightDp(barView: View, heightDp: Int) {
        val density = resources.displayMetrics.density
        val params = barView.layoutParams
        params.height = (heightDp * density).toInt()
        barView.layoutParams = params
    }

    private fun setBarHeight(barView: View, amount: Int, maxAmount: Int) {
        val density = resources.displayMetrics.density
        val minHeightPx = (14 * density).toInt()
        val maxHeightPx = (95 * density).toInt()

        val heightPx = if (maxAmount > 0 && amount > 0) {
            val ratio = amount.toFloat() / maxAmount.toFloat()
            (minHeightPx + (maxHeightPx - minHeightPx) * ratio).toInt()
        } else {
            minHeightPx
        }

        val params = barView.layoutParams
        params.height = heightPx
        barView.layoutParams = params
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
