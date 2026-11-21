package com.hupisan.view.hutang.adapter


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.RowItemBinding
import com.hupisan.view.detail_hutang.DetailHutangActivity


class HutangAdapter(val context: Context, val onButtonClicked: onButtonClicked) : RecyclerView.Adapter<HutangAdapter.ViewHolder>(){
    var list = ArrayList<DataModel>()
    var lastPosition = -1

    var isNotif: Boolean = true

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
                        old.deskripsi == new.deskripsi &&
                        old.notif == new.notif &&
                        old.statusDataSelesai == new.statusDataSelesai &&
                        old.jumlah_dibayar == new.jumlah_dibayar
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list = lists
        diffResult.dispatchUpdatesTo(this)
    }

    fun getData(): ArrayList<DataModel> {
        return list
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }


    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#EFEAE3")), ColorDrawable(Color.parseColor("#E5D7CA")),
        ColorDrawable(Color.parseColor("#EBDCCB")), ColorDrawable(Color.parseColor("#DFD3C3")),
        ColorDrawable(Color.parseColor("#F5EBE1")), ColorDrawable(Color.parseColor("#DEC8B5")),
        ColorDrawable(Color.parseColor("#E8D5C4")), ColorDrawable(Color.parseColor("#EADBC8"))
    )

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }

    inner class ViewHolder(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataModel) {
            val decimalFormat = DecimalFormat("##,###,###")
            with(binding) {
                tvUsername.text = data.username
                
                // Avatar initial letter
                val initial = if (data.username.isNotEmpty()) data.username.first().uppercaseChar().toString() else "U"
                tvAvatarLetter.text = initial

                // Due date
                if (!data.jatuhTempo.isNullOrEmpty()) {
                    tvDibuatPada.text = "Jatuh Tempo: ${data.jatuhTempo} >"
                } else {
                    tvDibuatPada.text = "Dibuat: ${data.diBuat} >"
                }

                // Amount
                tvJumlah.text = "Rp " + decimalFormat.format(data.jumlah)

                // Status Badge & Avatar Circle colors
                if (data.statusDataSelesai) {
                    tvStatusBadge.text = "Lunas"
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_lunas)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.hupisan_paid_text))
                    layoutAvatarCircle.setBackgroundResource(R.drawable.bg_badge_lunas)
                    tvAvatarLetter.setTextColor(ContextCompat.getColor(context, R.color.hupisan_paid_text))
                } else {
                    tvStatusBadge.text = "Belum Lunas"
                    tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_belum_lunas)
                    tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.hupisan_debt_text))
                    layoutAvatarCircle.setBackgroundResource(R.drawable.bg_badge_belum_lunas)
                    tvAvatarLetter.setTextColor(ContextCompat.getColor(context, R.color.hupisan_debt_text))
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(data: DataModel, position: Int) {
        list.add(position, data)
        notifyItemInserted(position)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailHutangActivity::class.java)
            intent.putExtra(DetailHutangActivity.EXTRA_UID, list[position].id)
            intent.putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, list[position].jenisPinjaman)
            context.startActivity(intent)
        }
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                android.R.anim.slide_in_left
            )
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount() = list.size
}

interface onButtonClicked{
    fun onClick(data: DataModel)
}