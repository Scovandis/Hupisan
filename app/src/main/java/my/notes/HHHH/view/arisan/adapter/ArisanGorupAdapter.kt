package my.notes.HHHH.view.arisan.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import my.notes.HHHH.R
import my.notes.HHHH.database.entity.GrupArisanModel
import my.notes.HHHH.databinding.ItemGroupArisanBinding
import my.notes.HHHH.view.arisan.ArisanMemberActivity
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ArisanGorupAdapter(val context: Context): RecyclerView.Adapter<ArisanGorupAdapter.ViewHolder>() {
    var list= ArrayList<GrupArisanModel>()

    fun setData(lists: ArrayList<GrupArisanModel>){
        this.list = lists
        notifyDataSetChanged()
    }
    fun getData(): ArrayList<GrupArisanModel>{
        return list
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

    inner class ViewHolder(val binding: ItemGroupArisanBinding): RecyclerView.ViewHolder(binding.root){
        val decimalFormat = DecimalFormat("##,###,###")
        fun bind(data: GrupArisanModel){
            with(binding){
                nameGroup.text = data.grup_name
                tvJumlahHadiah.text = " : "  + context.getString(R.string.dolar)+ " "+ decimalFormat.format(data.total_prize_poll)
                tvJumlahMember.text = data.jumlah_member.toString()
                tvJumlahNominal.text = " : " + context.getString(R.string.dolar)+ " " + decimalFormat.format(data.anggaran_kesepakatan_peruser)
                tvJumlahTerkumpul.text = " : " + context.getString(R.string.dolar) + "  " + decimalFormat.format(data.anggaran_terkumpul)

                imvRandomColor.background = getRandomDrawbleColor()

                imgSrcBgGroup.setColorFilter(ContextCompat.getColor(context, R.color.white))
                if (data.jumlah_member <= 6){
                    imgSrcBgGroup.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_female))
                }else if(data.jumlah_member in 7..15){
                    imgSrcBgGroup.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_male))
                }else {
                    imgSrcBgGroup.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_kosong))
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = ItemGroupArisanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ArisanMemberActivity::class.java)
            intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_ID, list[position].uid)
            intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_NAME, list[position].grup_name)
            intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_ANGGARAN, list[position].anggaran_kesepakatan_peruser)
            intent.putExtra(ArisanMemberActivity.EXTRA_TANGGAL_DIUPDATE, list[position].diupdate)
            intent.putExtra(ArisanMemberActivity.EXTRA_TANGGAL_DIBUAT, list[position].dibuat)
            intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_PRIZE_POLL, list[position].total_prize_poll)
            intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_TERKUMPUL, list[position].anggaran_terkumpul)
            context.startActivity(intent)
        }
    }

    override fun getItemCount()= list.size
}