package com.hupisan.view.main

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityMainBinding
import com.hupisan.util.AppsConfig
import com.hupisan.view.statistic.StatisticActivity
import com.hupisan.view.arisan.FitureArisanActivity
import com.hupisan.view.bantuan.BantuanActivity
import com.hupisan.view.add_hutang.AddHutangActivity
import com.hupisan.view.detail_hutang.DetailHutangActivity
import com.hupisan.view.hutang.HutangActivity
import com.hupisan.view.lunas.LunasActivity
import com.hupisan.view.main.adapter.NewAdapter
import com.hupisan.view.pengaturan.SettingsActivity
import com.hupisan.viewmodel.DataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DataViewModel by viewModel()
    lateinit var newAdapter: NewAdapter
    lateinit var appsConfig: AppsConfig

    private var allRecords = ArrayList<DataModel>()
    private var currentFilter = FILTER_ALL

    companion object {
        var ISNULL = 0
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"

        const val FILTER_ALL = 0
        const val FILTER_DEBT = 1
        const val FILTER_RECEIVABLE = 2
        const val FILTER_PAID = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appsConfig = AppsConfig(this)
        loadLanguage()

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val hutangText = getString(R.string.hutang)
        val piutangText = getString(R.string.piutang)

        setupHeader()
        setupSummaryClickListeners(hutangText, piutangText)
        setupMenuClickListeners(hutangText, piutangText)
        setupSecondaryActions()
        setupBottomNav()
        setupFilterTabs()
        setupRecyclerView()
        setupFab(hutangText, piutangText)

        // Delayed initial data fetch to allow smooth transitions
        Handler(Looper.getMainLooper()).postDelayed({
            observeData()
        }, 300)
    }

    private fun setupHeader() {
        binding.btnHeaderNotifications.setOnClickListener {
            startActivity(Intent(this, BantuanActivity::class.java))
        }

        binding.btnHeaderHelp.setOnClickListener {
            startActivity(Intent(this, BantuanActivity::class.java))
        }

        binding.btnHeaderSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupSummaryClickListeners(hutangText: String, piutangText: String) {
        binding.colSummaryDebt.setOnClickListener {
            val intent = Intent(this, HutangActivity::class.java)
            intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, hutangText)
            startActivity(intent)
        }

        binding.colSummaryReceivable.setOnClickListener {
            val intent = Intent(this, HutangActivity::class.java)
            intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, piutangText)
            startActivity(intent)
        }
    }

    private fun setupMenuClickListeners(hutangText: String, piutangText: String) {
        binding.cardMenuDebt.setOnClickListener {
            val intent = Intent(this, HutangActivity::class.java)
            intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, hutangText)
            startActivity(intent)
        }

        binding.cardMenuReceivable.setOnClickListener {
            val intent = Intent(this, HutangActivity::class.java)
            intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, piutangText)
            startActivity(intent)
        }

        binding.cardMenuPaidOff.setOnClickListener {
            startActivity(Intent(this, LunasActivity::class.java))
        }

        binding.cardMenuStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticActivity::class.java))
        }
    }

    private fun setupSecondaryActions() {
        binding.cardBannerArisan.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_arisan_title))
                .setMessage(getString(R.string.dialog_arisan_message))
                .setPositiveButton(getString(R.string.dialog_arisan_positive)) { dialog, _ -> dialog.dismiss() }
                .setNeutralButton(getString(R.string.dialog_arisan_neutral)) { _, _ ->
                    startActivity(Intent(this, FitureArisanActivity::class.java))
                }
                .show()
        }

        binding.cardMenuArisan.setOnClickListener {
            binding.cardBannerArisan.performClick()
        }

        binding.btnMenuLottery.setOnClickListener {
            startActivity(Intent(this, FitureArisanActivity::class.java))
        }

        binding.btnMenuReseller.setOnClickListener {
            Snackbar.make(binding.constraint, getString(R.string.fitur_reseller_segera_hadir), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            // Already at home
        }

        binding.navHistory.setOnClickListener {
            startActivity(Intent(this, LunasActivity::class.java))
        }

        binding.navReminder.setOnClickListener {
            startActivity(Intent(this, BantuanActivity::class.java))
        }

        binding.navMore.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupFilterTabs() {
        binding.chipFilterAll.setOnClickListener {
            setFilter(FILTER_ALL)
        }
        binding.chipFilterReceivable.setOnClickListener {
            setFilter(FILTER_RECEIVABLE)
        }
        binding.chipFilterDebt.setOnClickListener {
            setFilter(FILTER_DEBT)
        }
        binding.chipFilterPaid.setOnClickListener {
            setFilter(FILTER_PAID)
        }
    }

    private fun setFilter(filter: Int) {
        currentFilter = filter
        updateFilterTabsUI()
        applyFilter()
    }

    private fun updateFilterTabsUI() {
        val activeBg = ContextCompat.getDrawable(this, R.drawable.bg_filter_pill_active)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg_filter_pill_inactive)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.hupisan_text_secondary)

        val chips = listOf(
            Pair(binding.chipFilterAll, FILTER_ALL),
            Pair(binding.chipFilterReceivable, FILTER_RECEIVABLE),
            Pair(binding.chipFilterDebt, FILTER_DEBT),
            Pair(binding.chipFilterPaid, FILTER_PAID)
        )

        for ((chip, filterType) in chips) {
            if (filterType == currentFilter) {
                chip.background = activeBg
                chip.setTextColor(whiteColor)
            } else {
                chip.background = inactiveBg
                chip.setTextColor(inactiveTextColor)
            }
        }
    }

    private fun applyFilter() {
        val filteredList = when (currentFilter) {
            FILTER_DEBT -> allRecords.filter { !it.jenisPinjaman && !it.statusDataSelesai }
            FILTER_RECEIVABLE -> allRecords.filter { it.jenisPinjaman && !it.statusDataSelesai }
            FILTER_PAID -> allRecords.filter { it.statusDataSelesai }
            else -> allRecords
        }

        binding.tvActivityLogBadge.text = filteredList.size.toString()

        if (filteredList.isEmpty()) {
            binding.rvActivityLog.visibility = View.GONE
            binding.containerEmptyState.visibility = View.VISIBLE
        } else {
            binding.containerEmptyState.visibility = View.GONE
            binding.rvActivityLog.visibility = View.VISIBLE
            newAdapter.setData(ArrayList(filteredList))
        }
    }

    private fun setupRecyclerView() {
        newAdapter = NewAdapter(this@MainActivity)
        binding.rvActivityLog.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupFab(hutangText: String, piutangText: String) {
        val onAddClick = View.OnClickListener {
            showAddChooserDialog(hutangText, piutangText)
        }
        binding.btnCatatBaru.setOnClickListener(onAddClick)
        binding.btnEmptyCreateFirst.setOnClickListener(onAddClick)
    }

    private fun showAddChooserDialog(hutangText: String, piutangText: String) {
        val options = arrayOf(
            getString(R.string.dialog_add_option_debt),
            getString(R.string.dialog_add_option_receivable)
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_add_title))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(this, AddHutangActivity::class.java)
                        intent.putExtra(AddHutangActivity.EXTRA_STRING_JENIS, hutangText)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(this, AddHutangActivity::class.java)
                        intent.putExtra(AddHutangActivity.EXTRA_STRING_JENIS, piutangText)
                        startActivity(intent)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.batal)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun observeData() {
        viewModel.allData.observe(this, Observer { list ->
            binding.shimmer1.stopShimmer()
            binding.shimmer1.visibility = View.GONE

            allRecords = ArrayList(list ?: emptyList())
            calculateSummary(allRecords)
            populatePerluPerhatian(allRecords)
            applyFilter()
        })
    }

    private fun calculateSummary(records: List<DataModel>) {
        var totalDebt = 0L
        var countDebt = 0
        var totalReceivable = 0L
        var countReceivable = 0

        for (item in records) {
            if (!item.statusDataSelesai) {
                if (!item.jenisPinjaman) {
                    // Hutang (Debt)
                    totalDebt += item.jumlah
                    countDebt++
                } else {
                    // Piutang (Receivable)
                    totalReceivable += item.jumlah
                    countReceivable++
                }
            }
        }

        val decimalFormat = DecimalFormat("##,###,###")

        val debtFormatted = if (totalDebt > 0) {
            "Rp " + decimalFormat.format(totalDebt)
        } else {
            "Rp 0"
        }

        val receivableFormatted = if (totalReceivable > 0) {
            "Rp " + decimalFormat.format(totalReceivable)
        } else {
            "Rp 0"
        }

        binding.tvTotalDebt.text = debtFormatted
        binding.tvCountDebt.text = getString(R.string.catatan_aktif_format, countDebt)

        binding.tvTotalReceivable.text = receivableFormatted
        binding.tvCountReceivable.text = getString(R.string.catatan_aktif_format, countReceivable)

        // Calculate Net Balance (Selisih Bersih) with Financial Health Badge
        val netBalance = totalReceivable - totalDebt
        when {
            netBalance > 0 -> {
                binding.containerNetBalance.setBackgroundResource(R.drawable.bg_badge_lunas)
                binding.tvNetBalanceStatus.text = getString(R.string.status_surplus)
                binding.tvNetBalanceStatus.setTextColor(ContextCompat.getColor(this, R.color.hupisan_paid_text))
                binding.tvNetBalance.text = "+ Rp " + decimalFormat.format(netBalance)
                binding.tvNetBalance.setTextColor(ContextCompat.getColor(this, R.color.hupisan_paid_text))
            }
            netBalance < 0 -> {
                binding.containerNetBalance.setBackgroundResource(R.drawable.bg_badge_belum_lunas)
                binding.tvNetBalanceStatus.text = getString(R.string.status_defisit)
                binding.tvNetBalanceStatus.setTextColor(ContextCompat.getColor(this, R.color.hupisan_debt_text))
                binding.tvNetBalance.text = "- Rp " + decimalFormat.format(-netBalance)
                binding.tvNetBalance.setTextColor(ContextCompat.getColor(this, R.color.hupisan_debt_text))
            }
            else -> {
                binding.containerNetBalance.setBackgroundResource(R.drawable.bg_badge_neutral)
                binding.tvNetBalanceStatus.text = getString(R.string.status_kas_seimbang)
                binding.tvNetBalanceStatus.setTextColor(ContextCompat.getColor(this, R.color.hupisan_text_secondary))
                binding.tvNetBalance.text = "Rp 0"
                binding.tvNetBalance.setTextColor(ContextCompat.getColor(this, R.color.hupisan_text_primary))
            }
        }
    }

    private fun getDaysRemaining(dueDateStr: String?): Long? {
        if (dueDateStr.isNullOrEmpty()) return null
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dueDate = sdf.parse(dueDateStr) ?: return null
            val calendarNow = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calendarDue = Calendar.getInstance().apply {
                time = dueDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffMillis = calendarDue.timeInMillis - calendarNow.timeInMillis
            diffMillis / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            null
        }
    }

    private fun populatePerluPerhatian(records: List<DataModel>) {
        binding.containerPerluPerhatianItems.removeAllViews()

        val attentionList = records
            .filter { !it.statusDataSelesai && getDaysRemaining(it.jatuhTempo) != null && (getDaysRemaining(it.jatuhTempo) ?: 99L) <= 7L }
            .sortedBy { getDaysRemaining(it.jatuhTempo) ?: 99L }

        binding.tvTempoBadge.text = getString(R.string.attention_count_format, attentionList.size)

        if (attentionList.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = getString(R.string.attention_all_safe)
                textSize = 13f
                setTextColor(ContextCompat.getColor(context, R.color.hupisan_text_secondary))
                setPadding(8, 8, 8, 8)
            }
            binding.containerPerluPerhatianItems.addView(emptyTv)
        } else {
            val decimalFormat = DecimalFormat("##,###,###")
            val inflater = LayoutInflater.from(this)

            attentionList.forEachIndexed { index, item ->
                val itemBinding = com.hupisan.databinding.RowPerluPerhatianBinding.inflate(inflater, binding.containerPerluPerhatianItems, false)

                val days = getDaysRemaining(item.jatuhTempo) ?: 0L
                val (tagText, tagColor) = when {
                    days < 0 -> Pair(getString(R.string.tag_lewat_tempo), ContextCompat.getColor(this, R.color.hupisan_debt_red))
                    days == 0L -> Pair(getString(R.string.tag_hari_ini), ContextCompat.getColor(this, R.color.hupisan_debt_red))
                    days == 1L -> Pair(getString(R.string.tag_besok), ContextCompat.getColor(this, R.color.hupisan_partial_text))
                    else -> Pair(getString(R.string.tag_hari_lagi_format, days), ContextCompat.getColor(this, R.color.hupisan_partial_text))
                }

                val typeLabel = if (item.jenisPinjaman) getString(R.string.piutang) else getString(R.string.hutang)
                itemBinding.tvAttentionTitle.text = "${item.username} ($typeLabel)"
                itemBinding.tvAttentionAmount.text = "Rp " + decimalFormat.format(item.jumlah)
                itemBinding.tvAttentionTag.text = tagText
                itemBinding.tvAttentionTag.setTextColor(tagColor)

                if (item.jenisPinjaman) {
                    itemBinding.dotIndicator.setBackgroundResource(R.drawable.bg_circle_green_badge)
                } else {
                    itemBinding.dotIndicator.setBackgroundResource(R.drawable.bg_circle_red_badge)
                }

                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, DetailHutangActivity::class.java).apply {
                        putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, item.jenisPinjaman)
                        putExtra(DetailHutangActivity.EXTRA_UID, item.id)
                    }
                    startActivity(intent)
                }

                binding.containerPerluPerhatianItems.addView(itemBinding.root)

                if (index < attentionList.size - 1) {
                    val divider = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                        ).apply {
                            setMargins(0, 4, 0, 4)
                        }
                        setBackgroundColor(ContextCompat.getColor(context, R.color.hupisan_card_stroke))
                    }
                    binding.containerPerluPerhatianItems.addView(divider)
                }
            }
        }
    }

    private fun loadLanguage() {
        var lang = appsConfig.getRegionLanguage()

        if (lang.equals(getString(R.string.english))) {
            lang = "en"
        } else if (lang.equals(getString(R.string.indonesia))) {
            lang = "in"
        } else if (lang.equals(getString(R.string.thailand))) {
            lang = "tpi"
        } else if (lang.equals(getString(R.string.vietnamese))) {
            lang = "vun"
        } else if (lang.equals(getString(R.string.india))) {
            lang = "kok"
        }

        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()

        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
    }

    private fun intentViewPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.hupisan")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Refresh filter and summary on return
        applyFilter()
    }
}
