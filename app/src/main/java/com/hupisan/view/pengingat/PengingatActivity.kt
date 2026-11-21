package com.hupisan.view.pengingat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityPengingatBinding
import com.hupisan.notification.ReminderScheduler
import com.hupisan.view.detail_hutang.DetailHutangActivity
import com.hupisan.viewmodel.DataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PengingatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengingatBinding
    private val viewModel: DataViewModel by viewModel()
    private lateinit var adapter: PengingatAdapter

    private var allUnsettled = listOf<DataModel>()
    private var currentFilter = FILTER_ALL

    companion object {
        private const val FILTER_ALL = 0
        private const val FILTER_OVERDUE = 1
        private const val FILTER_UPCOMING = 2
        private const val FILTER_OFF = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityPengingatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSyncAlarms.setOnClickListener {
            ReminderScheduler.rescheduleAll(applicationContext)
            Snackbar.make(
                binding.rootPengingat,
                getString(R.string.pengingat_reschedule_done),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        adapter = PengingatAdapter(
            context = this,
            onItemClick = { item ->
                val intent = Intent(this, DetailHutangActivity::class.java).apply {
                    putExtra(DetailHutangActivity.EXTRA_UID, item.id)
                    putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, item.jenisPinjaman)
                }
                startActivity(intent)
            },
            onToggleAlarm = { item, enabled ->
                val reminderTime = item.jam_jatuh_tempo.takeIf { it.isNotBlank() && it != "-" } ?: "09:00"
                val updated = DataModel(
                    id = item.id,
                    username = item.username,
                    deskripsi = item.deskripsi,
                    jumlah = item.jumlah,
                    diBuat = item.diBuat,
                    jatuhTempo = item.jatuhTempo,
                    notif = enabled,
                    genderUser = item.genderUser,
                    jenisPinjaman = item.jenisPinjaman,
                    statusDataSelesai = item.statusDataSelesai,
                    jam_jatuh_tempo = if (enabled) reminderTime else "-",
                    tanggal_lunas = item.tanggal_lunas,
                    jumlah_dibayar = item.jumlah_dibayar
                )
                viewModel.updateData(updated)
                if (enabled) {
                    ReminderScheduler.schedule(applicationContext, updated.id, updated.jatuhTempo, reminderTime)
                } else {
                    ReminderScheduler.cancel(applicationContext, updated.id)
                }
            },
            onSnoozeClick = { item ->
                ReminderScheduler.scheduleSnooze(applicationContext, item.id)
                Snackbar.make(
                    binding.rootPengingat,
                    getString(R.string.notif_snoozed_toast),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        )

        binding.rvPengingat.apply {
            layoutManager = LinearLayoutManager(this@PengingatActivity)
            adapter = this@PengingatActivity.adapter
        }

        setupFilters()
        observeData()
    }

    private fun setupFilters() {
        binding.chipReminderAll.setOnClickListener { setFilter(FILTER_ALL) }
        binding.chipReminderOverdue.setOnClickListener { setFilter(FILTER_OVERDUE) }
        binding.chipReminderUpcoming.setOnClickListener { setFilter(FILTER_UPCOMING) }
        binding.chipReminderOff.setOnClickListener { setFilter(FILTER_OFF) }
    }

    private fun setFilter(filter: Int) {
        currentFilter = filter
        updateFilterUI()
        applyFilter()
    }

    private fun updateFilterUI() {
        val chips = listOf(
            Pair(FILTER_ALL, binding.chipReminderAll),
            Pair(FILTER_OVERDUE, binding.chipReminderOverdue),
            Pair(FILTER_UPCOMING, binding.chipReminderUpcoming),
            Pair(FILTER_OFF, binding.chipReminderOff)
        )
        for ((id, view) in chips) {
            styleChip(view, id == currentFilter)
        }
    }

    private fun styleChip(chip: TextView, active: Boolean) {
        if (active) {
            chip.setBackgroundResource(R.drawable.bg_filter_pill_active)
            chip.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            chip.setBackgroundResource(R.drawable.bg_filter_pill_inactive)
            chip.setTextColor(ContextCompat.getColor(this, R.color.hupisan_text_secondary))
        }
    }

    private fun observeData() {
        viewModel.allData.observe(this) { list ->
            val unsettled = (list ?: emptyList())
                .filter { !it.statusDataSelesai }
                .sortedBy { PengingatAdapter.getDaysRemaining(it.jatuhTempo) ?: Long.MAX_VALUE }

            allUnsettled = unsettled

            val overdueCount = unsettled.count {
                val d = PengingatAdapter.getDaysRemaining(it.jatuhTempo)
                d != null && d < 0
            }
            val activeCount = unsettled.count { it.notif }

            binding.tvOverdueCount.text = overdueCount.toString()
            binding.tvActiveCount.text = activeCount.toString()

            applyFilter()
        }
    }

    private fun applyFilter() {
        val filtered = when (currentFilter) {
            FILTER_OVERDUE -> allUnsettled.filter {
                val d = PengingatAdapter.getDaysRemaining(it.jatuhTempo)
                d != null && d < 0
            }
            FILTER_UPCOMING -> allUnsettled.filter {
                val d = PengingatAdapter.getDaysRemaining(it.jatuhTempo)
                it.notif && (d == null || d >= 0)
            }
            FILTER_OFF -> allUnsettled.filter { !it.notif }
            else -> allUnsettled
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            binding.rvPengingat.visibility = View.GONE
            binding.cardEmptyPengingat.visibility = View.VISIBLE
        } else {
            binding.rvPengingat.visibility = View.VISIBLE
            binding.cardEmptyPengingat.visibility = View.GONE
        }
    }
}
