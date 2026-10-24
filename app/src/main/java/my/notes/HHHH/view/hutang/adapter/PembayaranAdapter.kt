package my.notes.HHHH.view.hutang.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.notes.HHHH.R
import my.notes.HHHH.database.entity.DataPembayaranModel
import my.notes.HHHH.databinding.RowPembayaranBinding
import my.notes.HHHH.view.hutang.DetailHutangActivity
import java.util.*
import kotlin.collections.ArrayList

class PembayaranAdapter(val context: Context) : RecyclerView.Adapter<PembayaranAdapter.ViewHolder>() {
    var list = ArrayList<DataPembayaranModel>()

    fun setData(lists: ArrayList<DataPembayaranModel>) {
        this.list = lists
        notifyDataSetChanged()
    }

    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#9ACCCD")), ColorDrawable(Color.parseColor("#8FD8A0")),
        ColorDrawable(Color.parseColor("#CBD890")), ColorDrawable(Color.parseColor("#DACC8F")),
        ColorDrawable(Color.parseColor("#D9A790")), ColorDrawable(Color.parseColor("#D18FD9")),
        ColorDrawable(Color.parseColor("#FF6772")), ColorDrawable(Color.parseColor("#DDFB5C"))
    )

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }

    inner class ViewHolder(val binding: RowPembayaranBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: DataPembayaranModel){
            binding.apply {
                tvCreateAt.text = item.diUpdate
//                tvDescription.text = item.keterangan
                tvBankRiwayatPembayaran.text = context.getString(R.string.dolar) + ". " + DetailHutangActivity.decimalFormat.format(item.jumlahPembayaran)

                when(item.diTambahAtaudiKurangiAtauLunas){
                    1 -> {
                        when(item.typeJenis){
                            false -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.RED)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_keluar))
                                ivStatus.setColorFilter(Color.RED)
                            }
                            true -> {
                                //piutang
                                tvBankRiwayatPembayaran.setTextColor(Color.GREEN)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrouw_masuk))
                                ivStatus.setColorFilter(Color.GREEN)
                            } else -> {
                                //lunas
                                tvBankRiwayatPembayaran.setTextColor(Color.BLACK)
                                ivStatus.setColorFilter(Color.BLACK)
                            }
                        }
                    }
                    2 -> {
                        when(item.typeJenis){
                            false -> {
                                tvBankRiwayatPembayaran.setTextColor(Color.GREEN)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrouw_masuk))
                                ivStatus.setColorFilter(Color.GREEN)
                            }
                            true -> {
                                //piutang
                                tvBankRiwayatPembayaran.setTextColor(Color.RED)
                                ivStatus.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_keluar))
                                ivStatus.setColorFilter(Color.RED)
                            }
                            else -> {
                                //lunas
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowPembayaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
    override fun getItemCount()= list.size
}