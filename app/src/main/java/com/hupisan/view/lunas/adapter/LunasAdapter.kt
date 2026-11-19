package com.hupisan.view.lunas.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.RowLunasBinding
import com.hupisan.view.hutang.DetailHutangActivity
import com.hupisan.view.lunas.WindowFragmentLunas
import java.util.*

class LunasAdapter(val context: Context): RecyclerView.Adapter<LunasAdapter.ViewHolder>() {
    var list = ArrayList<DataModel>()

    fun setData(lists: ArrayList<DataModel>){
        this.list = lists
        notifyDataSetChanged()
    }
    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#EFEAE3")), ColorDrawable(Color.parseColor("#E5D7CA")),
        ColorDrawable(Color.parseColor("#EBDCCB")), ColorDrawable(Color.parseColor("#DFD3C3")),
        ColorDrawable(Color.parseColor("#D9C3B0")), ColorDrawable(Color.parseColor("#CDB59F")),
        ColorDrawable(Color.parseColor("#DFCCB7")), ColorDrawable(Color.parseColor("#E8DDD1"))
    )

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }
    inner class ViewHolder(val binding: RowLunasBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: DataModel){
            val jenis = if (!data.jenisPinjaman) context.getString(R.string.hutang) else context.getString(R.string.piutang)
            val status = if(data.statusDataSelesai) context.getString(R.string.lunas) else context.getString(R.string.belum_lunas)
            with(binding){
                tvStatus.text = "  :  $status"
                tvTanggalDibuat.text = "  :  " +data.diBuat
                tvUsername.text = data.username
                tvTanggalSelesai.text = "  :  " +data.tanggal_lunas
                tvJenis.text = "  :  $jenis"

                imvRandomColor.background = getRandomDrawbleColor()

                imgSrcBgGender.setColorFilter(ContextCompat.getColor(context, R.color.white))
                    when(data.genderUser){
                        1 -> {
                            imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_male))
                        }
                        2 -> {
                            imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_female))
                        }
                        3 -> {
                            imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_kosong))
                        }else -> {
                        imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_alert))
                    }
                }
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
            WindowFragmentLunas.STAY_LUNAS = 1
            val intent = Intent(context, DetailHutangActivity::class.java)
            intent.putExtra(DetailHutangActivity.EXTRA_UID, list[position].id)
            intent.putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, list[position].jenisPinjaman)
            context.startActivity(intent)
        }
    }

    override fun getItemCount()=list.size

}