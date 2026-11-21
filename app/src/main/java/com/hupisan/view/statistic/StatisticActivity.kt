package com.hupisan.view.statistic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityStatisticBinding
import com.hupisan.databinding.RowTopContactBinding
import com.hupisan.viewmodel.DataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.URLEncoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticBinding
    private val viewModel: DataViewModel by viewModel()

    private var currentFilterMode = 0 // 0: Bulan Ini, 1: 3 Bulan, 2: Semua
    private var allDataList: List<DataModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_card_bg)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setupToolbar()
        setupFilterPills()
        setupShareButton()
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
                    1 -> { // 3 Bulan
                        val diffMonths = (currentYear - itemCal.get(Calendar.YEAR)) * 12 + (currentMonth - itemCal.get(Calendar.MONTH))
                        diffMonths in 0..2
                    }
                    else -> true // Semua
                }
            }
        }

        binding.tvScorecardPeriod.text = when (currentFilterMode) {
            0 -> "Periode: Bulan Ini"
            1 -> "Periode: 3 Bulan Terakhir"
            else -> "Periode: Seluruh Data Tercatat"
        }

        // 1. Rekap Performa Bulan Ini (Monthly Scorecard)
        val totalDipinjamkan = filteredList.filter { it.jenisPinjaman }.sumOf { it.jumlah }
        val totalDitagih = filteredList.filter { it.jenisPinjaman && it.statusDataSelesai }.sumOf { it.jumlah }
        val totalTertunggak = filteredList.filter { it.jenisPinjaman && !it.statusDataSelesai }.sumOf { it.jumlah }

        val decimalFormat = DecimalFormat("##,###,###")
        binding.tvTotalDipinjamkan.text = "Rp " + decimalFormat.format(totalDipinjamkan)
        binding.tvTotalDitagih.text = "Rp " + decimalFormat.format(totalDitagih)
        binding.tvTotalTertunggak.text = "Rp " + decimalFormat.format(totalTertunggak)

        // Ratio Bar (Bar Chart sederhana perbandingan Hijau vs Merah)
        val ratioPaid = if (totalDipinjamkan > 0) ((totalDitagih.toDouble() / totalDipinjamkan) * 100).toInt() else 0
        val ratioUnpaid = (100 - ratioPaid).coerceAtLeast(0)

        binding.tvRatioLabel.text = "$ratioPaid% Tertagih"

        val paramPaid = binding.barRatioPaid.layoutParams as LinearLayout.LayoutParams
        paramPaid.weight = ratioPaid.toFloat()
        binding.barRatioPaid.layoutParams = paramPaid

        val paramUnpaid = binding.barRatioUnpaid.layoutParams as LinearLayout.LayoutParams
        paramUnpaid.weight = ratioUnpaid.toFloat()
        binding.barRatioUnpaid.layoutParams = paramUnpaid

        // 2. Daftar "Orang Teratas" (Top Contact Summary: 3 Debitur Piutang Terbesar)
        renderTopContacts()
    }

    private fun renderTopContacts() {
        binding.containerTopContacts.removeAllViews()

        val topContacts = allDataList
            .filter { it.jenisPinjaman && !it.statusDataSelesai }
            .sortedByDescending { it.jumlah }
            .take(3)

        if (topContacts.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "🎉 Tidak ada piutang yang tertunggak. Semua catatan lunas!"
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.hupisan_text_secondary))
                setPadding(8, 12, 8, 12)
            }
            binding.containerTopContacts.addView(emptyTv)
        } else {
            val decimalFormat = DecimalFormat("##,###,###")
            val inflater = LayoutInflater.from(this)

            topContacts.forEachIndexed { index, item ->
                val rowBinding = RowTopContactBinding.inflate(inflater, binding.containerTopContacts, false)

                rowBinding.tvRankNumber.text = (index + 1).toString()
                rowBinding.tvContactName.text = item.username
                rowBinding.tvContactDue.text = if (!item.jatuhTempo.isNullOrBlank()) "Tempo: ${item.jatuhTempo}" else "Dibuat: ${item.diBuat}"
                rowBinding.tvContactAmount.text = "Rp " + decimalFormat.format(item.jumlah)

                val onWaClick = View.OnClickListener {
                    val message = "Halo ${item.username}, sekadar mengingatkan catatan piutang sebesar Rp ${decimalFormat.format(item.jumlah)} jatuh tempo pada ${item.jatuhTempo ?: "-"}. Terima kasih banyak 🙏"
                    try {
                        val waUrl = "https://api.whatsapp.com/send?text=" + URLEncoder.encode(message, "UTF-8")
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)))
                    } catch (e: Exception) {
                        Toast.makeText(this, "Tidak dapat membuka WhatsApp", Toast.LENGTH_SHORT).show()
                    }
                }

                rowBinding.btnContactWa.setOnClickListener(onWaClick)
                rowBinding.root.setOnClickListener(onWaClick)

                binding.containerTopContacts.addView(rowBinding.root)

                if (index < topContacts.size - 1) {
                    val divider = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                        ).apply {
                            setMargins(0, 4, 0, 4)
                        }
                        setBackgroundColor(ContextCompat.getColor(context, R.color.hupisan_card_stroke))
                    }
                    binding.containerTopContacts.addView(divider)
                }
            }
        }
    }

    private fun setupShareButton() {
        binding.btnShareReport.setOnClickListener {
            shareFinancialSummary()
        }
    }

    private fun shareFinancialSummary() {
        val decimalFormat = DecimalFormat("##,###,###")
        val periodFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        val currentPeriod = periodFormat.format(Date())

        val totalDipinjamkan = allDataList.filter { it.jenisPinjaman }.sumOf { it.jumlah }
        val totalDitagih = allDataList.filter { it.jenisPinjaman && it.statusDataSelesai }.sumOf { it.jumlah }
        val totalTertunggak = allDataList.filter { it.jenisPinjaman && !it.statusDataSelesai }.sumOf { it.jumlah }

        val ratioPaid = if (totalDipinjamkan > 0) ((totalDitagih.toDouble() / totalDipinjamkan) * 100).toInt() else 0

        val topContacts = allDataList
            .filter { it.jenisPinjaman && !it.statusDataSelesai }
            .sortedByDescending { it.jumlah }
            .take(3)

        val sb = StringBuilder()
        sb.append("📊 *Laporan Keuangan Personal - Hupisan*\n")
        sb.append("Periode: $currentPeriod\n\n")

        sb.append("💰 *Rekap Piutang & Tagihan:*\n")
        sb.append("• Total Uang Dipinjamkan : Rp ${decimalFormat.format(totalDipinjamkan)}\n")
        sb.append("• Berhasil Ditagih (Lunas) : Rp ${decimalFormat.format(totalDitagih)} ($ratioPaid%)\n")
        sb.append("• Masih Tertunggak        : Rp ${decimalFormat.format(totalTertunggak)}\n\n")

        if (topContacts.isNotEmpty()) {
            sb.append("👥 *Prioritas Piutang Tertinggi:*\n")
            topContacts.forEachIndexed { idx, item ->
                val tempoText = if (!item.jatuhTempo.isNullOrBlank()) " (Tempo: ${item.jatuhTempo})" else ""
                sb.append("${idx + 1}. ${item.username}: Rp ${decimalFormat.format(item.jumlah)}$tempoText\n")
            }
            sb.append("\n")
        }

        sb.append("Catatan keuangan dicatat rapi & aman menggunakan aplikasi Hupisan.")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan Keuangan")
        startActivity(shareIntent)
    }
}
