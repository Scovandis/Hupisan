package com.hupisan.view.arisan

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.ArisanViewModel
import com.hupisan.viewmodel.ArisanGroupViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.hupisan.R
import com.hupisan.database.entity.ArisanModel
import com.hupisan.databinding.ActivityArisanMemberBinding
import com.hupisan.view.arisan.adapter.ArisanMemberAdapter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArisanMemberActivity : AppCompatActivity() {
    private val TAG = ArisanMemberActivity::class.java.simpleName
    private lateinit var binding: ActivityArisanMemberBinding
    val memberVM: ArisanViewModel by viewModel()
    val groupVM: ArisanGroupViewModel by viewModel()
    lateinit var memberAdapter: ArisanMemberAdapter


    var isRefres: Boolean = true
    private var groupId: Int? = null
    private var groupName: String? = null
    private var groupAnggaran: Int? = null
    private var groupPrizePool: Int? = null
    private var groupDiBuat: String? = null
    private var groupDiUpdate: String? = null
    private var groupTerkumpul: Int? = null

    var timeNow: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArisanMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.animToolbar
        setSupportActionBar(toolbar)
        val collapsingToolbar = binding.collapsingToolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (FitureArisanActivity.statusDimulai){
            binding.optionF.visibility = View.GONE
            binding.fabKocok.visibility = View.VISIBLE
        }else {
            binding.fabKocok.visibility = View.GONE
            binding.optionF.visibility = View.VISIBLE
        }

        memberAdapter = ArisanMemberAdapter(this)

        groupId = intent.getIntExtra(EXTRA_GROUP_ID, 0)
        groupName = intent.getStringExtra(EXTRA_GROUP_NAME) ?: ""
        groupAnggaran = intent.getIntExtra(EXTRA_GROUP_ANGGARAN, 0)
        groupPrizePool = intent.getIntExtra(EXTRA_GROUP_PRIZE_POLL, 0)
        groupDiBuat = intent.getStringExtra(EXTRA_TANGGAL_DIBUAT) ?: ""
        groupDiUpdate = intent.getStringExtra(EXTRA_TANGGAL_DIUPDATE) ?: groupDiBuat
        groupTerkumpul = intent.getIntExtra(EXTRA_GROUP_TERKUMPUL, 0)

        binding.fabTambahkanAnggota.setColorFilter(Color.WHITE)
        binding.fabMulai.setColorFilter(Color.WHITE)
        binding.optionF.setColorFilter(Color.parseColor("#FF6200EE"))

        Log.d(TAG, "EXTRASSS ::: $groupId")
        timeNow = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        collapsingToolbar.title = groupName


        setRecyclerView()
        setActionView()
        observeData()
    }

    private fun observeData(){
        setFirstView()
        val id = groupId ?: 0
        memberVM.getAllMemberFromGroupId(id).observe(this, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            Log.d(TAG, "DATA ALL MEMBER FROM GROUP ID $it")
            if (it.isNullOrEmpty()){
                binding.lottieAnimation.visibility = View.VISIBLE
                binding.tvIsEmpty.visibility = View.VISIBLE
                binding.rvMemberArisan.visibility = View.GONE
                memberAdapter.setData(ArrayList())
            }else {
                binding.lottieAnimation.visibility = View.GONE
                binding.tvIsEmpty.visibility = View.GONE
                binding.rvMemberArisan.visibility = View.VISIBLE
                memberAdapter.setData(ArrayList(it))
                if (it.size >= 50){
                    binding.fabTambahkanAnggota.visibility = View.GONE
                    binding.tvFabTambahkan.visibility = View.GONE
                }
            }
        })
    }
    private fun setRecyclerView(){
        binding.rvMemberArisan.apply {
            layoutManager = GridLayoutManager(this@ArisanMemberActivity, 3, GridLayoutManager.VERTICAL, false)
            adapter = memberAdapter
        }
    }

    private fun setActionView(){
        binding.optionF.setOnClickListener {
            onAddButtonClick()
        }
        binding.fabMulai.setOnClickListener {
            alertMulai()
        }
        binding.fabTambahkanAnggota.setOnClickListener {
            val intent = Intent(this, AddMemberActivity::class.java)
            intent.putExtra(AddMemberActivity.EXTRA_GROUP_ID, groupId)
            intent.putExtra(AddMemberActivity.EXTRA_GROUP_NAME, groupName)
            intent.putExtra(AddMemberActivity.EXTRA_NOMINAL_PEMBAYARAN, groupAnggaran)
            intent.putExtra(AddMemberActivity.EXTRA_GROUP_PRIZE_POLL, groupPrizePool)
            intent.putExtra(AddMemberActivity.EXTRA_TANGGAL_DIBUAT, groupDiBuat)
            intent.putExtra(AddMemberActivity.EXTRA_TANGGAL_DIUPDATE, groupDiUpdate)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun alertMulai(){
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.peringatan))
        alert.setMessage(getString(R.string.dengan_memulai_sebuah_acara_kamu_tidak_akan_bisa_menambahkan_anggota_atau_menghapus_anggota))
        alert.setPositiveButton(getString(R.string.yakin_mulai)){
                dialog, witch ->
            dialog.dismiss()
            val updateTime = timeNow ?: SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            groupVM.updateDataWhereStatus(updateTime, true, groupId ?: 0)
            Toast.makeText(this, getString(R.string.berhasil_memulai_acara), Toast.LENGTH_SHORT).show()
            binding.progressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressbar.visibility = View.GONE
                startActivity(Intent(this, ArisanActivity::class.java))
            }, 800)
        }
        alert.setNegativeButton(getString(R.string.kembali)){
                dialog, witch ->
            dialog.dismiss()
        }
        alert.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, ArisanActivity::class.java))
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)}
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)}
    private var closed = false

    private fun onAddButtonClick(){
        setVisibility(closed)
        setAnimation(closed)
        closed = !closed
    }

    private fun setVisibility(closed: Boolean){
        if (!closed){
            binding.fabMulai.visibility = View.VISIBLE
            binding.fabTambahkanAnggota.visibility = View.VISIBLE
            binding.tvFabMulai.visibility = View.VISIBLE
            binding.tvFabTambahkan.visibility = View.VISIBLE
        }else{
            binding.fabMulai.visibility = View.INVISIBLE
            binding.fabTambahkanAnggota.visibility = View.INVISIBLE
            binding.tvFabMulai.visibility = View.INVISIBLE
            binding.tvFabTambahkan.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(closed: Boolean){
        if (!closed){
            binding.optionF.startAnimation(rotateOpen)
            binding.fabTambahkanAnggota.startAnimation(fromBottom)
            binding.fabMulai.startAnimation(fromBottom)
            binding.tvFabTambahkan.startAnimation(fromBottom)
            binding.tvFabMulai.startAnimation(fromBottom)
        }else{
            binding.optionF.startAnimation(rotateClose)
            binding.fabTambahkanAnggota.startAnimation(toBottom)
            binding.fabMulai.startAnimation(toBottom)
            binding.tvFabMulai.startAnimation(toBottom)
            binding.tvFabTambahkan.startAnimation(toBottom)
        }
    }

    private fun setFirstView(){
        val decimalFormat = DecimalFormat("##,###,###")
        binding.content.tvNamaGroup.text = groupName
        binding.content.tvAnggaran.text = getString(R.string.dolar) + " " + decimalFormat.format(groupAnggaran)
        binding.content.tvDiupdate.text = groupDiUpdate
        binding.content.tvGroupId.text = groupId.toString()
        binding.content.tvPrizePoll.text = getString(R.string.dolar) + " "+decimalFormat.format(groupPrizePool)
        binding.content.tvTanggalDibuat.text = groupDiBuat
        binding.content.tvJumlahTerkumpul.text = "${getString(R.string.dolar)} ${decimalFormat.format(groupTerkumpul)}"

        binding.tvIsEmpty.visibility = View.GONE
        binding.lottieAnimation.visibility = View.GONE
        binding.rvMemberArisan.visibility = View.GONE
    }
    companion object{
        const val EXTRA_GROUP_ID = "extra_group_id"
        const val EXTRA_GROUP_NAME = "extra_group_name"
        const val EXTRA_GROUP_ANGGARAN = "extra_group_anggaran"
        const val EXTRA_GROUP_PRIZE_POLL = "extra_prize_poll"
        const val EXTRA_TANGGAL_DIBUAT = "extra_tanggal_dibuat"
        const val EXTRA_TANGGAL_DIUPDATE = "extra_tanggal_diupdate"
        const val EXTRA_GROUP_TERKUMPUL = "extra_group_terkumpul"
    }
}