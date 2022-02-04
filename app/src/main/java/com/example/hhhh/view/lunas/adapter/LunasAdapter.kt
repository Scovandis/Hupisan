package com.example.hhhh.view.lunas.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hhhh.R
import com.example.hhhh.database.entity.DataModel
import com.example.hhhh.databinding.RowLunasBinding
import com.example.hhhh.view.hutang.DetailHutangActivity
import com.example.hhhh.view.lunas.WindowFragmentLunas
import java.util.*

class LunasAdapter(val context: Context): RecyclerView.Adapter<LunasAdapter.ViewHolder>() {
    var list = ArrayList<DataModel>()

    fun setData(lists: ArrayList<DataModel>){
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
    inner class ViewHolder(val binding: RowLunasBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: DataModel){
            with(binding){
                tvStatus.text = "  :  " +data.statusData
                tvTanggalDibuat.text = "  :  " +data.diBuat
                tvUsername.text = data.username
                tvTanggalSelesai.text = "  :  " +data.tanggal_lunas
                tvJenis.text = "  :  " + data.jenis

                imvRandomColor.background = getRandomDrawbleColor()

                imgSrcBgGender.setColorFilter(ContextCompat.getColor(context, R.color.white))
                    when(data.gender){
                        context.getString(R.string.laki_laki) -> {
                            imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_male))
                        }
                        context.getString(R.string.perempuan) -> {
                            imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_female))
                        }
                        context.getString(R.string.kosongkan) -> {
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
            intent.putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, list[position].jenis)
            context.startActivity(intent)
        }
    }

    override fun getItemCount()=list.size

}