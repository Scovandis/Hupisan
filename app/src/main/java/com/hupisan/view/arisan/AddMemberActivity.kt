package com.hupisan.view.arisan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.ArisanViewModel
import com.hupisan.viewmodel.ArisanGroupViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hupisan.R
import com.hupisan.database.entity.ArisanModel
import com.hupisan.databinding.ActivityAddMemberBinding
import com.hupisan.models.ListUserModels
import com.hupisan.view.arisan.adapter.ListUser
import java.text.SimpleDateFormat
import java.util.*

import kotlin.collections.ArrayList

class AddMemberActivity : AppCompatActivity() {
    private val TAG = AddMemberActivity::class.java.simpleName
    lateinit var binding: ActivityAddMemberBinding
    val memberVM: ArisanViewModel by viewModel()
    val groupArisanVM: ArisanGroupViewModel by viewModel()
    lateinit var userAdapter: ListUser

    var groupId: Int? = null
    var groupName: String? = null
    var groupPembayaran: Int? =null
    var groupDiupdate : String? = null
    var groupDibuat: String? = null
    var groupPrizePoll: Int? = null

    var timeNow: String? = null
    var isBayar1: SwitchMaterial? = null
    var gender: Int = 1
    var count: Int = 0

    lateinit var listUsername: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.animToolbar
        setSupportActionBar(toolbar)
        val collapsingToolbar = binding.collapsingToolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listUsername = ArrayList<String>()
        firstData()
        userAdapter = ListUser()

        collapsingToolbar.title = "$groupName (${getString(R.string.dolar)} $groupPembayaran)"

        setHide()
        setView()
        setRecyclerView()
    }
    private fun firstData(){
        val time = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        groupId = intent.getIntExtra(EXTRA_GROUP_ID, 0)
        groupName = intent.getStringExtra(EXTRA_GROUP_NAME) ?: ""
        groupPembayaran = intent.getIntExtra(EXTRA_NOMINAL_PEMBAYARAN, 0)
        groupPrizePoll = intent.getIntExtra(EXTRA_GROUP_PRIZE_POLL, 0)
        groupDibuat = intent.getStringExtra(EXTRA_TANGGAL_DIBUAT) ?: ""
        groupDiupdate = intent.getStringExtra(EXTRA_TANGGAL_DIUPDATE) ?: groupDibuat
        timeNow = time
    }
    private fun setHide(){
        isBayar1 = binding.switchOnPay
        binding.btnSimpanMember.visibility = View.GONE
        binding.rvUsername.visibility = View.VISIBLE
    }
    private fun deleteData() {
        val id = groupId ?: return
        memberVM.getAllMemberFromGroupId(id).observe(this, androidx.lifecycle.Observer {
            for (i in it) {
                if (listUsername.contains(i.username)) {
                    memberVM.deleteMemberWhereUID(i.uid)
                }
            }
        })
    }
    private fun alertDialogWhenOption(){
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.peringatan))
        alert.setMessage(getString(R.string.kembali_akan_menghapus_semua_data_yang_telah_kamu_buat_sebelumnya))
        alert.setPositiveButton(getString(R.string.iya)){
                dialog, witch ->
            dialog.dismiss()
            deleteData()
            binding.progressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressbar.visibility = View.GONE
                val intent = Intent(this, ArisanMemberActivity::class.java)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_ID, groupId)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_NAME, groupName)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_PRIZE_POLL, groupPrizePoll)
                intent.putExtra(ArisanMemberActivity.EXTRA_TANGGAL_DIBUAT, groupDibuat)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_ANGGARAN, groupPembayaran)
                intent.putExtra(ArisanMemberActivity.EXTRA_TANGGAL_DIUPDATE, groupDiupdate)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_TERKUMPUL, 0)
                startActivity(intent)
            },700)
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
                val intent = Intent(this, ArisanMemberActivity::class.java)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_ID, groupId)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_NAME, groupName)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_PRIZE_POLL, groupPrizePoll)
                intent.putExtra(ArisanMemberActivity.EXTRA_TANGGAL_DIBUAT, groupDibuat)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_ANGGARAN, groupPembayaran)
                intent.putExtra(ArisanMemberActivity.EXTRA_TANGGAL_DIUPDATE, groupDiupdate)
                intent.putExtra(ArisanMemberActivity.EXTRA_GROUP_TERKUMPUL, 0)
                startActivity(intent)
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun setRecyclerView(){
        binding.rvUsername.apply {
            layoutManager = LinearLayoutManager(this@AddMemberActivity, LinearLayoutManager.VERTICAL, false)
            adapter = userAdapter
        }
    }

    private fun setView(){
        binding.radioGroup1.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            when (radio.id) {
                R.id.rb_gender_laki -> {
                    gender = 1
                }
                R.id.rb_gender_perempuan -> {
                    gender = 2
                }
                R.id.rb_gender_kosong -> {
                    gender = 3
                }
            }
        }
        binding.etMemeberName1.editText!!.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length <= 0){
                    binding.etMemeberName1.error = getString(R.string.nama_tidak_boleh_kosong)
                }
                else if(p0.toString().length >= 20){
                    binding.etMemeberName1.error = getString(R.string.tidak_boleh_lebih_dari_20_huruf)
                }else{
                    binding.etMemeberName1.error = ""
                }
            }
        })
        count = 0
        var prefixText = 1
        binding.btnAddMember.setOnClickListener {
            val memberText = binding.etMemeberName1.editText?.text?.toString()?.trim() ?: ""
            if (memberText.isNotEmpty()) {
                count += 1
                prefixText += 1
                binding.etMemeberName1.prefixText = "$prefixText"
                groupId?.let { itId -> saveMemberRules(itId) }
                if (count in 1..7) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.btnSimpanMember.visibility = View.VISIBLE
                    binding.tvAlert.text = getString(R.string.kamu_bisa_menambahkan_maksimal_9_anggota)
                } else if (count == 9) {
                    binding.btnAddMember.visibility = View.GONE
                    binding.btnSimpanMember.visibility = View.VISIBLE
                }
                Log.d(TAG, "COUNT $count")
            } else {
                binding.etMemeberName1.error = getString(R.string.nama_tidak_boleh_kosong)
            }
        }
        binding.btnSimpanMember.setOnClickListener {
            groupId?.let { itId -> updateGroupData(itId) }
        }
    }

    private fun updateGroupData(groupId: Int) {
        var totalMember = 0
        var anggaranTerkumpul = 0
        memberVM.getAllMemberFromGroupId(groupId).observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "GROUP ID $groupId")
            if (it.isNullOrEmpty()) {
                Log.d(TAG, "OPPPSSS DATA KOSONG!! $it")
            } else {
                it.forEach { member ->
                    totalMember += 1
                    if (member.status_bayar) {
                        anggaranTerkumpul += member.jumlah_bayar
                    }
                }
                groupArisanVM.updateDataWhereGroupNeeded(timeNow ?: SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()), anggaranTerkumpul, totalMember, groupId)
            }
        })
        Log.d(TAG, "TOTAL MEMEBER $totalMember")
        Log.d(TAG, "Anggaran TERKUMPUL $anggaranTerkumpul")

        binding.progressbar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            Snackbar.make(binding.swipeRefreshLayout, getString(R.string.data_berhasil_di_dimpan), Snackbar.LENGTH_SHORT).show()
            binding.progressbar.visibility = View.GONE
        }, 1000)
        startActivity(Intent(this, ArisanActivity::class.java))
    }

    private fun saveMemberRules(groupId: Int) {
        val member1 = binding.etMemeberName1.editText?.text?.toString()?.trim() ?: ""
        val nominal = groupPembayaran ?: 0
        val statusB1 = if (isBayar1?.isChecked == true) nominal else 0

        if (member1.isEmpty()) {
            binding.etMemeberName1.error = getString(R.string.masukkan_nama_memeber)
        } else {
            val data = ArisanModel(
                0,
                groupId,
                member1,
                statusB1,
                "",
                isBayar1?.isChecked ?: false,
                status_menang = false,
                gender
            )

            val listUser = ListUserModels(member1, count)
            memberVM.insertUser(data)
            userAdapter.addUser(listUser)
            listUsername.add(member1)

            binding.etMemeberName1.editText?.text?.clear()
            binding.switchOnPay.isChecked = false
            Log.d(TAG, "MEMBER $member1")
            binding.progressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressbar.visibility = View.GONE
                Snackbar.make(binding.swipeRefreshLayout, getString(R.string.anggota_berhasil_di_tambahkan), Snackbar.LENGTH_SHORT).show()
            }, 700)
        }
    }

    companion object{
        const val EXTRA_GROUP_NAME = "extra_group_name"
        const val EXTRA_GROUP_ID = "extra_group_id"
        const val EXTRA_NOMINAL_PEMBAYARAN = "extra_nominal_pembayaran"
        const val EXTRA_GROUP_PRIZE_POLL = "extra_prize_poll"
        const val EXTRA_TANGGAL_DIBUAT = "extra_tanggal_dibuat"
        const val EXTRA_TANGGAL_DIUPDATE = "extra_tanggal_diupdate"
    }
}