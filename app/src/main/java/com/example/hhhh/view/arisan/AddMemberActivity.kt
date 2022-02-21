package com.example.hhhh.view.arisan

import android.content.Intent
import android.icu.text.LocaleDisplayNames
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hhhh.R
import com.example.hhhh.database.ArisanGroupViewModel
import com.example.hhhh.database.ArisanViewModel
import com.example.hhhh.database.entity.ArisanModel
import com.example.hhhh.database.entity.GrupArisanModel
import com.example.hhhh.databinding.ActivityAddMemberBinding
import com.example.hhhh.models.ListUserModels
import com.example.hhhh.view.arisan.adapter.ListUser
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddMemberActivity : AppCompatActivity() {
    private val TAG = AddMemberActivity::class.java.simpleName
    lateinit var binding: ActivityAddMemberBinding
    lateinit var memberVM: ArisanViewModel
    lateinit var groupArisanVM: ArisanGroupViewModel
    lateinit var userAdapter: ListUser

    var groupId: Int? = null
    var groupName: String? = null
    var groupPembayaran: Int? =null
    var timeNow: String? = null
    var isBayar1: SwitchMaterial? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firstData()
        memberVM = ViewModelProvider(this).get(ArisanViewModel::class.java)
        groupArisanVM = ViewModelProvider(this).get(ArisanGroupViewModel::class.java)
        userAdapter = ListUser()
        setHide()
        setView()
        setRecyclerView()
    }
    private fun firstData(){
        val time = SimpleDateFormat("dd/MM/yyyy").format(Date())
        val Gid = intent.extras!!.get(EXTRA_GROUP_ID)
        val Gname = intent.extras!!.get(EXTRA_GROUP_NAME)
        val Gpembayaran = intent.extras!!.get(EXTRA_NOMINAL_PEMBAYARAN)
        groupId = Gid.toString().toInt()
        groupName = Gname.toString()
        groupPembayaran = Gpembayaran.toString().toIntOrNull()
        timeNow = time.toString()
    }
    private fun setHide(){
        isBayar1 = binding.switchOnPay
        binding.btnSimpanMember.visibility = View.GONE
        binding.rvUsername.visibility = View.GONE
        if (userAdapter != null){
            binding.rvUsername.visibility = View.VISIBLE
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, ArisanMemberActivity::class.java))
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
        binding.etMemeberName1.editText!!.addTextChangedListener{object: TextWatcher {
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
        }}
        val member1 = binding.etMemeberName1.editText!!.text
        var count=0
        var prefixText = 1
        binding.btnAddMember.setOnClickListener {
            if(member1.isNotEmpty()){
                count+=1
                prefixText+=1
                binding.etMemeberName1.prefixText = "${prefixText}"
                groupId?.let { itId -> saveMemberRules(itId) }
                if (count in 3..10) {
                    binding.btnSimpanMember.visibility = View.VISIBLE
                    binding.btnAddMember.visibility = View.GONE
                }
                Log.d(TAG, "COUNT $count")
            }else {
              binding.etMemeberName1.error = getString(R.string.nama_tidak_boleh_kosong)
            }
        }
        binding.btnSimpanMember.setOnClickListener {
            groupId?.let { itId -> updateGroupData(itId) }
        }
    }
    private fun updateGroupData(groupId: Int){
        var totalMember = 0
        var anggaranTerkumpul = 0
        memberVM.getAllMemberFromGroupId(groupId).observe(this, androidx.lifecycle.Observer {
            Log.d(TAG, "GROUP ID $groupId")
            if (it.isNullOrEmpty()){
                Log.d(TAG, "OPPPSSS DATA KOSONG!! $it")
            }else {
                it.forEach {
                    totalMember+=1
                    if (it.status_bayar){
                        anggaranTerkumpul += it.jumlah_bayar
                    }
                }
                groupArisanVM.updateDataWhereGroupNeeded(timeNow!!, anggaranTerkumpul, totalMember, groupId)
            }
        })
        Log.d(TAG, "TOTAL MEMEBER $totalMember")
        Log.d(TAG, "Anggaran TERKUMPUL $anggaranTerkumpul")

        binding.progressbar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            Snackbar.make(binding.constraint, getString(R.string.data_berhasil_di_dimpan), Snackbar.LENGTH_SHORT).show()
            binding.progressbar.visibility =View.GONE
        },1000)
        startActivity(Intent(this, ArisanActivity::class.java))
    }

    private fun saveMemberRules(groupId: Int){
        val member1 = binding.etMemeberName1.editText!!.text
        val statusB1 = if (isBayar1!!.isChecked) groupPembayaran else 0

        if (member1!!.isEmpty()){
            binding.etMemeberName1.error = getString(R.string.masukkan_nama_memeber)
        }
        else {
            if (member1.isNotEmpty()){
                val data = ArisanModel(
                    0,
                    groupId,
                    member1.toString(),
                    statusB1!!,
                    "",
                    isBayar1!!.isChecked,
                    status_menang = false
                )
                val listUser = ListUserModels(member1.toString(), +1)
                memberVM.insertUser(data)
                userAdapter.addUser(listUser)
            }
            binding.etMemeberName1.editText!!.text.clear()
            binding.switchOnPay.isChecked = false
            Log.d(TAG, "MEMBER $member1")
            binding.progressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressbar.visibility = View.GONE
                Snackbar.make(binding.constraint, getString(R.string.anggota_berhasil_di_tambahkan), Snackbar.LENGTH_SHORT).show()
            }, 700)
        }
    }
    companion object{
        var EXTRA_GROUP_NAME = "extra_group_name"
        var EXTRA_GROUP_ID = "extra_group_id"
        var EXTRA_NOMINAL_PEMBAYARAN = "extra_nominal_pembayaran"
    }
}