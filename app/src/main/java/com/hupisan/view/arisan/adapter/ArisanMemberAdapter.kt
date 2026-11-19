package com.hupisan.view.arisan.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.hupisan.R
import com.hupisan.database.entity.ArisanModel
import com.hupisan.databinding.ItemUserArisanBinding
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ArisanMemberAdapter(val context: Context): RecyclerView.Adapter<ArisanMemberAdapter.ViewHolder>() {
    var list = ArrayList<ArisanModel>()

    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#9ACCCD")), ColorDrawable(Color.parseColor("#8FD8A0")),
        ColorDrawable(Color.parseColor("#CBD890")), ColorDrawable(Color.parseColor("#DACC8F")),
        ColorDrawable(Color.parseColor("#D9A790")), ColorDrawable(Color.parseColor("#D18FD9")),
        ColorDrawable(Color.parseColor("#FF6772")), ColorDrawable(Color.parseColor("#DDFB5C"))
    )

    inner class ViewHolder(val binding: ItemUserArisanBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ArisanModel){
            val decimalFormat = DecimalFormat("##,###,###")
            val option = RequestOptions()
                .centerCrop()

            with(binding){
                tvUsername.text = item.username

                if (item.status_bayar){
                    tvBayar.text = context.getString(R.string.dolar)+ " " +decimalFormat.format(item.jumlah_bayar)
                    option.placeholder(ColorDrawable(Color.parseColor("#42ff00")))
                    ivStatus.background = context.resources.getDrawable(R.drawable.bg_custom_green)
                }else {
                    tvBayar.text = context.getString(R.string.dolar) + "  " +context.getString(R.string._0)
                    option.placeholder(ColorDrawable(Color.parseColor("#ff1106")))
                    ivStatus.background = context.resources.getDrawable(R.drawable.bg_custom_red)
                }

                imvRandomColor.background = getRandomDrawbleColor()
                imgSrcBgGender.setColorFilter(ContextCompat.getColor(context, R.color.white))
                when(item.gender){
                    1 -> {
                        imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_male))
                    }
                    2 -> {
                        imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_female))
                    }
                    3 -> {
                        imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_kosong))
                    }
                    else -> {
                        imgSrcBgGender.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_alert))
                    }
                }
            }
        }
    }

    fun setData(lists: ArrayList<ArisanModel>){
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = list.size
            override fun getNewListSize() = lists.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition].uid == lists[newItemPosition].uid
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val old = list[oldItemPosition]
                val new = lists[newItemPosition]
                return old.username == new.username &&
                        old.jumlah_bayar == new.jumlah_bayar &&
                        old.status_bayar == new.status_bayar &&
                        old.status_menang == new.status_menang &&
                        old.gender == new.gender
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.list = lists
        diffResult.dispatchUpdatesTo(this)
    }

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
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
