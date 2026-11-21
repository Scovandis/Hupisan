package com.hupisan.view.lunas.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.RowLunasBinding
import com.hupisan.view.detail_hutang.DetailHutangActivity
import java.text.DecimalFormat
import java.util.ArrayList

class LunasAdapter(val context: Context): RecyclerView.Adapter<LunasAdapter.ViewHolder>() {
    var list = ArrayList<DataModel>()
    private val decimalFormat = DecimalFormat("##,###,###")

    fun setData(lists: ArrayList<DataModel>){
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
                        old.jumlah_dibayar == new.jumlah_dibayar &&
                        old.statusDataSelesai == new.statusDataSelesai &&
                        old.jenisPinjaman == new.jenisPinjaman &&
                        old.tanggal_lunas == new.tanggal_lunas
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list = lists
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(val binding: RowLunasBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: DataModel){
            val jenis = if (!data.jenisPinjaman) context.getString(R.string.hutang) else context.getString(R.string.piutang)
            val nominal = if (data.jumlah_dibayar > 0) data.jumlah_dibayar else data.jumlah
            val amountFormatted = "Rp " + decimalFormat.format(nominal)

            with(binding){
                tvUsername.text = data.username
                tvJenis.text = jenis
                tvTanggalSelesai.text = if (!data.tanggal_lunas.isNullOrBlank() && data.tanggal_lunas != "-") {
                    "Lunas: ${data.tanggal_lunas}"
                } else {
                    "Dibuat: ${data.diBuat}"
                }
                tvNominalLunas.text = amountFormatted
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLunasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailHutangActivity::class.java)
            intent.putExtra(DetailHutangActivity.EXTRA_UID, list[position].id)
            intent.putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, list[position].jenisPinjaman)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
