package my.notes.HHHH.view.arisan.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import my.notes.HHHH.R
import my.notes.HHHH.database.entity.ArisanModel
import my.notes.HHHH.databinding.ItemUserArisanBinding
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserArisanBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()= list.size
}