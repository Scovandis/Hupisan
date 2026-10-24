package my.notes.HHHH.view.hutang.adapter


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import my.notes.HHHH.R
import my.notes.HHHH.database.entity.DataModel
import my.notes.HHHH.databinding.RowItemBinding
import my.notes.HHHH.view.hutang.DetailHutangActivity


class HutangAdapter(val context: Context, val onButtonClicked: onButtonClicked) : RecyclerView.Adapter<HutangAdapter.ViewHolder>(){
    var list = ArrayList<DataModel>()
    var lastPosition = -1

    var isNotif: Boolean = true

    fun setData(lists: ArrayList<DataModel>) {
        this.list = lists
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<DataModel> {
        return list
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
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

    inner class ViewHolder(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataModel) {
            val decimalFormat = DecimalFormat("##,###,###")
            with(binding) {
                tvUsername.text = data.username
                tvDeskription.text = data.deskripsi
                tvDibuatPada.text = data.diBuat
                tvJumlah.text = context.getString(R.string.dolar)+" " + decimalFormat.format(data.jumlah)

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
                btnNotif.setOnClickListener {
                    val alertDialog: AlertDialog = AlertDialog.Builder(binding.root.context).create()
                    alertDialog.setTitle(context.getString(R.string.jatuh_tempo))
                    if (!data.notif){
                        alertDialog.setMessage(context.getString(R.string.tanggal_tidak_di_atur))
                    }else{
                        alertDialog.setMessage(data.jatuhTempo + " " + data.jam_jatuh_tempo)
                    }
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.iya)) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                }
                isNotif = data.notif

                if (isNotif){
                    btnNotif.icon = context.resources.getDrawable(R.drawable.ic_notifications_active_black_24dp)
                }else {
                    btnNotif.icon = context.resources.getDrawable(R.drawable.ic_notifications_off_black_24dp)
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
        val animation = AnimationUtils.loadAnimation(
            holder.itemView.context,
            android.R.anim.slide_in_left
        )
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount() = list.size
}

interface onButtonClicked{
    fun onClick(data: DataModel)
}