package com.example.hhhh.view.main.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hhhh.R
import com.example.hhhh.database.entity.DataModel
import com.example.hhhh.databinding.RowNewDataBinding
import com.example.hhhh.view.hutang.DetailHutangActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewAdapter(val context: Context): RecyclerView.Adapter<NewAdapter.ViewHolder>() {
    var list = ArrayList<DataModel>()

    fun setData(lists: ArrayList<DataModel>){
        this.list = lists
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

    inner class ViewHolder(val binding: RowNewDataBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(dataModel: DataModel){

            val decimalFormat = DecimalFormat("##,###,###")

            val dateFormat= SimpleDateFormat("dd/MM/yyyy")
            val dateString: String = dataModel.diBuat
            val myDate: Date = dateFormat.parse(dateString)
            val calendar1 = Calendar.getInstance()
            calendar1.time = myDate
            calendar1.add(Calendar.DAY_OF_YEAR, -1)
            val newDate = calendar1.time
            Log.d("NEW ADAPTER", "NEW DATE $newDate")

            val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("yyyy/MM/dd")
            val formatDate = formatter.format(parser.parse("2018-12-14T09:55:00"))
            Log.d("NEW ADAPTER ,", "FORMATDATE $formatDate")

            with(binding){
                val sdf = SimpleDateFormat("dd/MM/yyyy").format(Date())
                Log.d("NEW ADAPTER ", "DATA MODEL ${dataModel.diBuat}")
                Log.d("ADAPTER ", "dataDibuat :${dataModel.diBuat} \n newData : ${sdf.toString()}")

                if (dataModel.diBuat == sdf.toString()){
                    Log.d("NEW ADAPTER ", "DATA MODEL2 ${dataModel.diBuat}")
                    tvLabelNew.visibility = View.VISIBLE
                }else{
                    Log.d("NEW ADAPTER ", "DATA MODEL3 ${dataModel.diBuat}")
                    tvLabelNew.visibility = View.GONE
                }

                imvRandomColor.background = getRandomDrawbleColor()
//                tvStatusType.text = dataModel.jenis
                tvJumlahNominal.text = context.getString(R.string.dolar) + ". " + decimalFormat.format(dataModel.jumlah)
                tvUsername.text = dataModel.username
                tvCreateAt.text = dataModel.diBuat
//                if ()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowNewDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            DetailHutangActivity.STAY_MAIN_MENU = 101
            val intent = Intent(context, DetailHutangActivity::class.java)
            intent.putExtra(DetailHutangActivity.EXTRA_TYPE_JENIS, list[position].jenisPinjaman)
            intent.putExtra(DetailHutangActivity.EXTRA_UID, list[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount()= list.size
}


