package com.hupisan.view.main

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityMainBinding
import com.hupisan.util.AppsConfig
import com.hupisan.view.analistic.StatisticActivity
import com.hupisan.view.arisan.FitureArisanActivity
import com.hupisan.view.bantuan.BantuanActivity
import com.hupisan.view.hutang.AddHutangActivity
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

        appsConfig = AppsConfig()
        appsConfig.AppsConfig(this)
        loadLanguage()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_primary_dark)
        }

        val hutangText = getString(R.string.hutang)
        val piutangText = getString(R.string.piutang)

        setupHeader()
        setupSummaryClickListeners(hutangText, piutangText)
        setupMenuClickListeners(hutangText, piutangText)
        setupSecondaryActions()
        setupFilterTabs()
        setupRecyclerView()
        setupFab(hutangText, piutangText)

        // Delayed initial data fetch to allow smooth transitions
        Handler(Looper.getMainLooper()).postDelayed({
            observeData()
        }, 300)
    }

    private fun setupHeader() {
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
        binding.btnMenuLottery.setOnClickListener {
            startActivity(Intent(this, FitureArisanActivity::class.java))
        }

        binding.btnMenuReseller.setOnClickListener {
            Snackbar.make(binding.constraint, "Fitur Reseller segera hadir", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupFilterTabs() {
        binding.chipFilterAll.setOnClickListener {
            setFilter(FILTER_ALL)
        }
        binding.chipFilterDebt.setOnClickListener {
            setFilter(FILTER_DEBT)
        }
        binding.chipFilterReceivable.setOnClickListener {
            setFilter(FILTER_RECEIVABLE)
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
        val darkBg = ContextCompat.getDrawable(this, R.drawable.bg_pill_dark)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg_pill_inactive)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.hupisan_chip_inactive_text)

        val chips = listOf(
            Pair(binding.chipFilterAll, FILTER_ALL),
            Pair(binding.chipFilterDebt, FILTER_DEBT),
            Pair(binding.chipFilterReceivable, FILTER_RECEIVABLE),
            Pair(binding.chipFilterPaid, FILTER_PAID)
        )

        for ((chip, filterType) in chips) {
            if (filterType == currentFilter) {
                chip.background = darkBg
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
        binding.btnCatatBaru.setOnClickListener {
            showAddChooserDialog(hutangText, piutangText)
        }
    }

    private fun showAddChooserDialog(hutangText: String, piutangText: String) {
        val options = arrayOf(
            "Catat Hutang (Debt)",
            "Catat Piutang (Receivable)"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("Tambah Catatan Baru")
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
            .setNegativeButton("Batal") { dialog, _ ->
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
        binding.tvCountDebt.text = "$countDebt catatan aktif"

        binding.tvTotalReceivable.text = receivableFormatted
        binding.tvCountReceivable.text = "$countReceivable catatan aktif"
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
