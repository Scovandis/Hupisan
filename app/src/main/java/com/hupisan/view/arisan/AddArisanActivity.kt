package com.hupisan.view.arisan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.hupisan.R
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.ArisanViewModel
import com.hupisan.viewmodel.ArisanGroupViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial

import androidx.appcompat.app.AlertDialog
import com.hupisan.database.entity.ArisanModel
import com.hupisan.database.entity.GrupArisanModel
import com.hupisan.databinding.ActivityAddArisanBinding


class AddArisanActivity : AppCompatActivity() {
    private val TAG = AddArisanActivity::class.java.simpleName
    lateinit var binding : ActivityAddArisanBinding
    private var dateTimeNow: String? = null

    val groupArisanVM: ArisanGroupViewModel by viewModel()
    val userArisanVM: ArisanViewModel by viewModel()

    private var isBayar1: SwitchMaterial? = null
    var groupId : Int? = null
    private var gender: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArisanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dateTimeNow = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        binding.progressbar.progressDrawable = resources.getDrawable(R.drawable.custom_progressbar)

        setSwitch()
        setOnActionAddView()
    }
    private fun setSwitch(){
        isBayar1 = binding.switchOnPay1
        binding.tvMemberIsFull.visibility = View.GONE
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                if (groupId != null){
                    alertDialogWhenOption()
                }else {
                    startActivity(Intent(this, ArisanActivity::class.java))
                }
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun alertDialogWhenOption(){
        val alert = AlertDialog.Builder(this)
        alert.setTitle(getString(R.string.peringatan))
        alert.setMessage(getString(R.string.kembali_akan_menghapus_semua_data_yang_telah_kamu_buat_sebelumnya))
        alert.setPositiveButton(getString(R.string.iya)){
            dialog, witch ->
            dialog.dismiss()
            deleteData()
            startActivity(Intent(this, ArisanActivity::class.java))
        }
        alert.setNegativeButton(getString(R.string.kembali)){
            dialog, witch ->
            dialog.dismiss()
        }
        alert.show()
    }

    private fun deleteData(){
        groupArisanVM.deleteGroupWhereUid(groupId!!.toInt())
    }

    private fun setOnActionAddView(){
        binding.tvCreateAt.text = dateTimeNow

        binding.etNamaGroup.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                rulesGroupName(p0.toString())
            }
        })
        binding.etNominalPembayaran.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.length >= 9){
                    binding.etNominalPembayaran.helperText = getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                }else if (p0.startsWith("-")){
                    binding.etNominalPembayaran.error = getString(R.string.nominal_tidak_boleh_diawali_nilai_negative)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith("0")){
                    binding.etNominalPembayaran.helperText = getString(R.string.tidak_boleh_diawali_dengan_0)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
                }else if(p0.startsWith(" ")){
                    binding.etNominalPembayaran.error = getString(R.string.tidak_boleh_diawali_dengan_nilai_kosong)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(".")){
                    binding.etNominalPembayaran.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(",")){
                    binding.etNominalPembayaran.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                } else {
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                    binding.etNominalPembayaran.helperText = getString(R.string.masukkan_jumlah_nominal)
                }
            }
        })
        binding.etPrizePoll.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.length >= 9){
                    binding.etPrizePoll.helperText = getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                }else if (p0.startsWith("-")){
                    binding.etPrizePoll.error = getString(R.string.nominal_tidak_boleh_diawali_nilai_negative)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith("0")){
                    binding.etPrizePoll.helperText = getString(R.string.tidak_boleh_diawali_dengan_0)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
                }else if(p0.startsWith(" ")){
                    binding.etPrizePoll.error = getString(R.string.tidak_boleh_diawali_dengan_nilai_kosong)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(".")){
                    binding.etPrizePoll.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(",")){
                    binding.etPrizePoll.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                } else {
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                    binding.etPrizePoll.helperText = getString(R.string.masukkan_jumlah_nominal)
                }
            }
        })

        binding.etMemeberName1.editText!!.addTextChangedListener(object: TextWatcher{
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
        binding.radioGroup1.setOnCheckedChangeListener { _, checkedId ->
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
        binding.btnSimpanGroup.setOnClickListener {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            binding.progressbar.visibility = View.VISIBLE
            savedGroupData()
        }
        var member1 = binding.etMemeberName1.editText!!.text
        var count=0
        var prefixText = 1
        binding.btnTambahkanAnggota.setOnClickListener {
            if(member1.isNotEmpty()){
                count+=1
                prefixText+=1
                binding.etMemeberName1.prefixText = "${prefixText}"
                groupId?.let { itId -> saveMemberRules(itId) }
                if (count in 3..4) {
                    binding.btnTambahkanAnggota.visibility = View.VISIBLE
                    binding.btnSimpanData.visibility = View.VISIBLE
                    binding.tvMemberIsFull.text = getString(R.string.kamu_sudah_bisa_memulainya)
                    binding.tvMemberIsFull.visibility = View.VISIBLE
                }else if(count == 5){
                    binding.btnTambahkanAnggota.visibility = View.GONE
                    binding.btnSimpanData.visibility = View.VISIBLE
                    binding.tvMemberIsFull.text = getString(R.string.kamu_suadah_memasukkan_banyak_user_silahkan_simpan_terlebih_dahulu)
                    binding.tvMemberIsFull.visibility = View.VISIBLE
                }
                Log.d(TAG, "COUNT $count")
            }else{
                binding.etMemeberName1.error = getString(R.string.nama_tidak_boleh_kosong)
            }
        }

        binding.btnSimpanData.setOnClickListener {
            groupId?.let { itId -> updateGroupData(itId) }
        }


    }
    private fun savedGroupData(){
        val groupName = binding.etNamaGroup.editText!!.text.toString()
        val dibuat = dateTimeNow ?: SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val diupdate = dibuat
        val anggaran_kesepakatan_peruser = binding.etNominalPembayaran.editText!!.text
        val anggaran_terkumpul = 0
        val prize_poll = binding.etPrizePoll.editText!!.text

        if (groupName.length <=2){
            binding.etNamaGroup.error = getString(R.string.nama_tidak_boleh_kurang_dari_2_huruf)
        }else {
            if (binding.etNominalPembayaran.editText!!.text.isNullOrEmpty() || anggaran_kesepakatan_peruser.isEmpty() || anggaran_kesepakatan_peruser.toString().toInt() == 0){
                binding.etNominalPembayaran.error = getString(R.string.nominal_tidak_boleh_kosong)
            }else {
                if (binding.etPrizePoll.editText!!.text.isNullOrEmpty() || prize_poll.isEmpty() || prize_poll.toString().toInt() == 0){
                    binding.etPrizePoll.error = getString(R.string.nominal_tidak_boleh_kosong)
                }else {
                    val dataGroup = GrupArisanModel(0, groupName, dibuat, diupdate, anggaran_kesepakatan_peruser.toString().toInt(), anggaran_terkumpul, prize_poll.toString().toInt(), 0, false)
                    groupArisanVM.insertGroup(dataGroup)

                    groupArisanVM.getDataWhereGroupName(groupName.toString()).observe(this, androidx.lifecycle.Observer {
                        if(it != null){
                            groupId = it.uid
                        }
                    })

                    binding.btnSimpanGroup.visibility = View.GONE
                    binding.btnTambahkanAnggota.visibility = View.VISIBLE
                    binding.etNamaGroup.editText!!.isEnabled = false
                    binding.etPrizePoll.editText!!.isEnabled = false
                    binding.etNominalPembayaran.editText!!.isEnabled = false

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.progressbar.visibility = View.GONE
                    },700)

                    Snackbar.make(binding.constraint, getString(R.string.group_berhasil_dibuat), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveMemberRules(groupId: Int){
        val member1 = binding.etMemeberName1.editText!!.text
        val statusB1 = if (isBayar1!!.isChecked) binding.etNominalPembayaran.editText!!.text.toString().toInt() else 0

        if (member1!!.isEmpty()){
            binding.etMemeberName1.error = getString(R.string.masukkan_nama_memeber)
        }
        else {
            if (member1.isNotEmpty()){
                val data = ArisanModel(
                    0,
                    groupId,
                    member1.toString(),
                    statusB1,
                    "",
                    isBayar1!!.isChecked,
                    status_menang = false,
                    gender
                )
                userArisanVM.insertUser(data)
            }
            binding.etMemeberName1.editText!!.text.clear()
            binding.switchOnPay1.isChecked = false
            Log.d(TAG, "MEMBER $member1")
            binding.progressbar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.progressbar.visibility = View.GONE
                Snackbar.make(binding.constraint, getString(R.string.anggota_berhasil_di_tambahkan), Snackbar.LENGTH_SHORT).show()
            }, 700)
        }
    }

    private fun updateGroupData(groupId: Int){
        var totalMember = 0
        var anggaranTerkumpul = 0
        userArisanVM.getAllMemberFromGroupId(groupId).observe(this, androidx.lifecycle.Observer {
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
                val now = dateTimeNow ?: SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                groupArisanVM.updateDataWhereGroupNeeded(now, anggaranTerkumpul, totalMember, groupId ?: 0)
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
    private fun rulesGroupName(p0 :String){
        if (p0.length <= 0){
            binding.etNamaGroup.error = getString(R.string.nama_tidak_boleh_kosong)
        }else if(p0.length >= 20){
            binding.etNamaGroup.error = getString(R.string.tidak_boleh_lebih_dari_20_huruf)
        }else{
            binding.etNamaGroup.error = ""
        }
    }
    companion object{
        var EXTRA_VALUE = "extra_value" //1 maka ganti tambahkan member 0 maka ganti tambahkan group
    }
}