package com.hupisan.view.hutang.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.R
import com.hupisan.database.entity.PaymentModel
import com.hupisan.databinding.RowPembayaranBinding
import com.hupisan.view.hutang.DetailHutangActivity
import java.util.Random
import kotlin.collections.ArrayList

class PaymentAdapter(val context: Context) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    var list = ArrayList<PaymentModel>()

    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#9ACCCD")), ColorDrawable(Color.parseColor("#8FD8A0")),
        ColorDrawable(Color.parseColor("#CBD890")), ColorDrawable(Color.parseColor("#DACC8F")),
        ColorDrawable(Color.parseColor("#D9A790")), ColorDrawable(Color.parseColor("#D18FD9")),
        ColorDrawable(Color.parseColor("#FF6772")), ColorDrawable(Color.parseColor("#DDFB5C"))
    )

    inner class ViewHolder(val binding: RowPembayaranBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaymentModel) {
            binding.apply {
                tvCreateAt.text = item.diUpdate
                tvBankRiwayatPembayaran.text = context.getString(R.string.dolar) + ". " + DetailHutangActivity.decimalFormat.format(item.jumlahPembayaran)

                when (item.diTambahAtaudiKurangiAtauLunas) {
                    1 -> {
                        when (item.typeJenis) {
                            false -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.RED)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_keluar))
                                ivStatus.setColorFilter(Color.RED)
                            }
                            true -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.GREEN)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrouw_masuk))
                                ivStatus.setColorFilter(Color.GREEN)
                            }
                            else -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.BLACK)
                                ivStatus.setColorFilter(Color.BLACK)
                            }
                        }
                    }
                    2 -> {
                        when (item.typeJenis) {
                            false -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.GREEN)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrouw_masuk))
                                ivStatus.setColorFilter(Color.GREEN)
                            }
                            true -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.RED)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_keluar))
                                ivStatus.setColorFilter(Color.RED)
                            }
                            else -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.BLACK)
                                ivStatus.setColorFilter(Color.BLACK)
                            }
                        }
                    }
                    0 -> {
                        ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24))
                        tvBankRiwayatPembayaran.setTextColor(Color.BLACK)
                        ivStatus.setColorFilter(Color.BLACK)
                    }
                    else -> {
                        ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24))
                        tvBankRiwayatPembayaran.setTextColor(Color.BLACK)
                        ivStatus.setColorFilter(Color.BLACK)
                    }
                }
            }
        }
    }

    fun setData(lists: ArrayList<PaymentModel>) {
        this.list = lists
        notifyDataSetChanged()
    }

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowPembayaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}
