package com.hupisan.view.pengingat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.RowPengingatItemBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PengingatAdapter(
    private val context: Context,
    private val onItemClick: (DataModel) -> Unit,
    private val onToggleAlarm: (DataModel, Boolean) -> Unit,
    private val onSnoozeClick: (DataModel) -> Unit
) : RecyclerView.Adapter<PengingatAdapter.ViewHolder>() {

    private var items = listOf<DataModel>()
    private val decimalFormat = DecimalFormat("##,###,###")

    fun submitList(newItems: List<DataModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: RowPengingatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataModel) {
            with(binding) {
                tvReminderName.text = item.username
                tvReminderAmount.text =
                    context.getString(R.string.prefix_rp) + decimalFormat.format(item.jumlah)

                if (item.jenisPinjaman) {
                    tvReminderType.text = context.getString(R.string.piutang)
                    tvReminderAmount.setTextColor(
                        ContextCompat.getColor(context, R.color.hupisan_receivable_text)
                    )
                } else {
                    tvReminderType.text = context.getString(R.string.hutang)
                    tvReminderAmount.setTextColor(
                        ContextCompat.getColor(context, R.color.hupisan_debt_text)
                    )
                }

                val timeStr = item.jam_jatuh_tempo.takeIf { it.isNotBlank() && it != "-" } ?: "09:00"
                val dueLabel = if (item.jatuhTempo.isNotBlank()) {
                    "${context.getString(R.string.pengingat_due_format, item.jatuhTempo)} • $timeStr"
                } else {
                    item.diBuat
                }
                tvReminderSchedule.text = dueLabel

                val cleanDesc = item.deskripsi.trim().takeIf { it.isNotEmpty() && it != "-" }
                if (cleanDesc != null) {
                    tvReminderDesc.text = cleanDesc
                    tvReminderDesc.visibility = View.VISIBLE
                } else {
                    tvReminderDesc.visibility = View.GONE
                }

                val days = getDaysRemaining(item.jatuhTempo)
                when {
                    days != null && days < 0 -> {
                        tvReminderDueBadge.text = context.getString(R.string.pengingat_summary_overdue)
                        tvReminderDueBadge.setBackgroundResource(R.drawable.bg_badge_belum_lunas)
                        tvReminderDueBadge.setTextColor(
                            ContextCompat.getColor(context, R.color.hupisan_debt_text)
                        )
                    }
                    days == 0L -> {
                        tvReminderDueBadge.text = context.getString(R.string.tag_hari_ini)
                        tvReminderDueBadge.setBackgroundResource(R.drawable.bg_badge_belum_lunas)
                        tvReminderDueBadge.setTextColor(
                            ContextCompat.getColor(context, R.color.hupisan_debt_text)
                        )
                    }
                    days == 1L -> {
                        tvReminderDueBadge.text = context.getString(R.string.tag_besok)
                        tvReminderDueBadge.setBackgroundResource(R.drawable.bg_badge_attention)
                        tvReminderDueBadge.setTextColor(
                            ContextCompat.getColor(context, R.color.hupisan_partial_text)
                        )
                    }
                    days != null -> {
                        tvReminderDueBadge.text =
                            context.getString(R.string.tag_hari_lagi_format, days)
                        tvReminderDueBadge.setBackgroundResource(R.drawable.bg_badge_lunas)
                        tvReminderDueBadge.setTextColor(
                            ContextCompat.getColor(context, R.color.hupisan_paid_text)
                        )
                    }
                    else -> {
                        tvReminderDueBadge.text = context.getString(R.string.status_belum_lunas)
                        tvReminderDueBadge.setBackgroundResource(R.drawable.bg_badge_neutral)
                        tvReminderDueBadge.setTextColor(
                            ContextCompat.getColor(context, R.color.hupisan_text_secondary)
                        )
                    }
                }

                tvAlarmStatus.text = if (item.notif) {
                    context.getString(R.string.pengingat_status_aktif_format, timeStr)
                } else {
                    context.getString(R.string.pengingat_status_nonaktif)
                }

                btnSnooze1h.visibility = if (item.notif) View.VISIBLE else View.GONE

                switchItemAlarm.setOnCheckedChangeListener(null)
                switchItemAlarm.isChecked = item.notif
                switchItemAlarm.setOnCheckedChangeListener { _, isChecked ->
                    onToggleAlarm(item, isChecked)
                }

                btnSnooze1h.setOnClickListener {
                    onSnoozeClick(item)
                }

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowPengingatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    companion object {
        fun getDaysRemaining(dueDateStr: String?): Long? {
            if (dueDateStr.isNullOrBlank()) return null
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dueDate = sdf.parse(dueDateStr) ?: return null
                val now = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val due = Calendar.getInstance().apply {
                    time = dueDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                (due.timeInMillis - now.timeInMillis) / (1000L * 60L * 60L * 24L)
            } catch (_: Exception) {
                null
            }
        }
    }
}
