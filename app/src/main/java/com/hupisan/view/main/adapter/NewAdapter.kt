package com.hupisan.view.main.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.RowNewDataBinding
import com.hupisan.view.hutang.DetailHutangActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewAdapter(val context: Context) : RecyclerView.Adapter<NewAdapter.ViewHolder>() {
    var list = ArrayList<DataModel>()

    fun setData(lists: ArrayList<DataModel>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = list.size
            override fun getNewListSize() = lists.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition].id == lists[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = list[oldItemPosition]
                val new = lists[newItemPosition]
                return old.username == new.username &&
                        old.jumlah == new.jumlah &&
                        old.statusDataSelesai == new.statusDataSelesai &&
                        old.jenisPinjaman == new.jenisPinjaman &&
                        old.diBuat == new.diBuat &&
                        old.tanggal_lunas == new.tanggal_lunas
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list = lists
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(val binding: RowNewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataModel: DataModel) {
            val decimalFormat = DecimalFormat("##,###,###")
            val todayString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            with(binding) {
                tvUsername.text = dataModel.username
                tvJumlahNominal.text = "Rp " + decimalFormat.format(dataModel.jumlah)

                val timeLabel = if (dataModel.diBuat == todayString) "Baru saja" else dataModel.diBuat

                if (dataModel.statusDataSelesai) {
                    val settleDate = if (!dataModel.tanggal_lunas.isNullOrBlank()) dataModel.tanggal_lunas else timeLabel
                    tvStatusSubtitle.text = "Paid Off • $settleDate"
                    tvJumlahNominal.setTextColor(ContextCompat.getColor(context, R.color.hupisan_paid_orange))
                    boxStatusIcon.setBackgroundResource(R.drawable.bg_activity_icon_paid)
                    ivStatusIcon.setImageResource(R.drawable.ic_hupisan_check_circle)
                    ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.hupisan_paid_orange))
                    ivActionCheck.setColorFilter(ContextCompat.getColor(context, R.color.hupisan_receivable_green))
                } else if (dataModel.jenisPinjaman) {
                    tvStatusSubtitle.text = "Receivable • $timeLabel"
                    tvJumlahNominal.setTextColor(ContextCompat.getColor(context, R.color.hupisan_receivable_green))
                    boxStatusIcon.setBackgroundResource(R.drawable.bg_activity_icon_receivable)
                    ivStatusIcon.setImageResource(R.drawable.ic_hupisan_receivable_arrow)
                    ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.hupisan_receivable_green))
                    ivActionCheck.setColorFilter(ContextCompat.getColor(context, R.color.hupisan_text_secondary))
                } else {
                    tvStatusSubtitle.text = "Debt • $timeLabel"
                    tvJumlahNominal.setTextColor(ContextCompat.getColor(context, R.color.hupisan_debt_red))
                    boxStatusIcon.setBackgroundResource(R.drawable.bg_activity_icon_debt)
                    ivStatusIcon.setImageResource(R.drawable.ic_hupisan_debt_arrow)
                    ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.hupisan_debt_red))
                    ivActionCheck.setColorFilter(ContextCompat.getColor(context, R.color.hupisan_text_secondary))
                }

                btnActionCheck.setOnClickListener {
                    openDetail(dataModel)
                }
            }
        }

        private fun openDetail(dataModel: DataModel) {
            val intent = Intent(context, DetailHutangActivity::class.java).apply {
                putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, dataModel.jenisPinjaman)
                putExtra(DetailHutangActivity.EXTRA_UID, dataModel.id)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowNewDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailHutangActivity::class.java).apply {
                putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, list[position].jenisPinjaman)
                putExtra(DetailHutangActivity.EXTRA_UID, list[position].id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = list.size
}
