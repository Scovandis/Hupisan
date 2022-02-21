package com.example.hhhh.view.arisan.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.example.hhhh.R
import com.example.hhhh.database.entity.ArisanModel
import com.example.hhhh.databinding.ItemUserArisanBinding
import java.text.DecimalFormat

class ArisanMemeberAdapter(val context: Context): RecyclerView.Adapter<ArisanMemeberAdapter.ViewHolder>() {
    var list = ArrayList<ArisanModel>()

    fun setData(lists: ArrayList<ArisanModel>){
        this.list = lists
    }
    inner class ViewHolder(val binding: ItemUserArisanBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ArisanModel){
            val decimalFormat = DecimalFormat("##,###,###")
            val option = RequestOptions()
                .centerCrop()

            with(binding){
                tvUsername.text = item.username
                if (item.status_bayar){
                    tvBayar.text = " : " + context.getString(R.string.dolar)+ " " +decimalFormat.format(item.jumlah_bayar)
                    option.placeholder(ColorDrawable(Color.parseColor("#42ff00")))
                }else {
                    tvBayar.text = " : " +context.getString(R.string.dolar) + "  " +context.getString(R.string._0)
                    option.placeholder(ColorDrawable(Color.parseColor("#ff1106")))

                }

            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserArisanBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()= list.size
}