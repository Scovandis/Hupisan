package com.hupisan.view.detail_hutang

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.DataViewModel
import com.hupisan.viewmodel.PaymentViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.hupisan.R
import com.google.android.material.snackbar.Snackbar
import com.hupisan.database.entity.DataModel
import com.hupisan.database.entity.PaymentModel
import com.hupisan.databinding.ActivityDetailHutangBinding
import com.hupisan.databinding.AlertDicicilBinding
import com.hupisan.view.detail_hutang.adapter.PaymentAdapter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailHutangActivity : AppCompatActivity() {
    private val TAG = DetailHutangActivity::class.java.simpleName
    private lateinit var binding: ActivityDetailHutangBinding

    private val viewModel: DataViewModel by viewModel()
    private val paymentViewModel: PaymentViewModel by viewModel()

    private var typeJenis: Boolean? = null
    private var jenis: String? = null
    private lateinit var paymentAdapter: PaymentAdapter
    private var currentDataModel: DataModel? = null

    private var activityJumlah = 0
    private var activityDibayar = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHutangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnMenuMore.setOnClickListener {
            showMoreOptionsDialog()
        }

        //get id && typejenis data
        val uid = intent.getIntExtra(EXTRA_UID, intent.getStringExtra(EXTRA_UID)?.toIntOrNull() ?: 0)
        this.jenis = intent.getStringExtra(EXTRA_TYPE_JENIS)
        typeJenis = this.jenis?.toBoolean() ?: false
        setData(uid.toString())

        binding.btnActionEdit.setOnClickListener {
            alertEditNama()
        }

        binding.btnActionAddPayment.setOnClickListener {
            showPaymentOptionsDialog()
        }

        paymentAdapter = PaymentAdapter(this)
        binding.rvDataPembayaranFromId.apply {
            layoutManager = LinearLayoutManager(this@DetailHutangActivity)
            adapter = paymentAdapter
        }

        val uidInt = uid.toString().toIntOrNull() ?: 0
        setListPembayaran(uidInt)

    }

    override fun onPause() {
        super.onPause()
        binding.content.shimmer1.stopShimmer()
    }

    override fun onResume() {
        binding.content.shimmer1.startShimmer()
        super.onResume()
    }

    private fun setListPembayaran(uid: Int) {
        paymentViewModel.getDataPembayaranFromUid(uid).observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                binding.layoutPaymentEmpty.visibility = View.GONE
                binding.rvDataPembayaranFromId.visibility = View.VISIBLE
                paymentAdapter.setData(ArrayList(it))
            } else {
                binding.layoutPaymentEmpty.visibility = View.VISIBLE
                binding.rvDataPembayaranFromId.visibility = View.GONE
            }
        })
    }

    private fun setAlert(statusOption: Int){
        val alertBinding = AlertDicicilBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(alertBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val decimalFormat = DecimalFormat("##,###,###")
        alertBinding.tvTotalHutang.text = getString(R.string.prefix_rp) + decimalFormat.format(activityJumlah)
        alertBinding.tvUsername.text = binding.tvProfileName.text

        val dibuatPada = alertBinding.tvAlertDibuatPada
        dibuatPada.text = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())

        when(statusOption){
            0 -> {
                alertBinding.tvDialogTitle.text = getString(R.string.lunasi_pembayaran)
                alertBinding.btnSimpan.text = getString(R.string.lunas)
                alertBinding.etJumlahAlert.editText!!.isEnabled = false
                alertBinding.etJumlahAlert.editText!!.setText(activityJumlah.toString())
            }
            1 -> {
                alertBinding.tvDialogTitle.text = getString(R.string.bayar_cicilan_title)
                alertBinding.btnSimpan.text = getString(R.string.bayar)
            }
            2 -> {
                alertBinding.tvDialogTitle.text = getString(R.string.tambah_pinjaman)
                alertBinding.btnSimpan.text = getString(R.string.ditambah)
            }
        }
        val type = if (!typeJenis!!) getString(R.string.hutang) else getString(R.string.piutang)
        onRules(alertBinding, statusOption)
        if (statusOption == 1){
            alertBinding.tvStatusType.text = getString(R.string.mengurangi) + " " + type
        } else if(statusOption == 0){
            alertBinding.tvStatusType.text = getString(R.string.melunasi) + " " + type
        } else if(statusOption == 2){
            alertBinding.tvStatusType.text = getString(R.string.menambah) + " " + type
        }

        alertBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertBinding.btnSimpan.setOnClickListener {
            savePaymentData(alertBinding, statusOption)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun savePaymentData(alertBinding: AlertDicicilBinding, statusOption: Int) {
        val fromUid = intent.getIntExtra(EXTRA_UID, intent.getStringExtra(EXTRA_UID)?.toIntOrNull() ?: 0)
        Log.d(TAG, " savePaymentData uid : $fromUid")
        val dibuat = alertBinding.tvAlertDibuatPada.text.toString()
        val keterangan = alertBinding.etKeteranganAlert.editText!!.text.toString()
        val aJumlah = alertBinding.etJumlahAlert.editText?.text?.toString() ?: ""
        val alertJumlah = aJumlah.replace(" ", "").replace(".", "").toIntOrNull() ?: 0
        //activity

        Log.d(TAG , "ACTIVITY JUMLAH ::::::::::::::::: $activityJumlah")
        Log.d(TAG, "ALERT JUMLAH :::::::  $alertJumlah")
        Log.d(TAG, "ALERT SATUS $statusOption")
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm a").format(Date())
        if (aJumlah.length > 9) {
            alertBinding.etJumlahAlert.helperText =
                getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
        } else {
            if (aJumlah.isEmpty()) {
                alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_kosong)
            } else {
                if (keterangan.length > 210) {
                    alertBinding.etKeteranganAlert.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
                } else {
                    if (alertJumlah == 0 || aJumlah.isEmpty()) {
                        alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_kosong)
                    } else {
                        val dateFormat = DecimalFormat("##,###,###")
                        val dataBayar = PaymentModel(
                            0,
                            fromUid,
                            dibuat,
                            alertJumlah,
                            keterangan,
                            statusOption,
                            typeJenis!!
                        )
                        val jenisLabel = if (typeJenis == true) getString(R.string.piutang) else getString(R.string.hutang)
                        when(statusOption){
                            1 -> {
                                var total = activityJumlah - alertJumlah
                                Log.d(TAG, "TOTAL DIKURANGI $total")
                                if(total < 0){
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, "${getString(R.string.nominal_tidak_boleh_melebihi)} sisa $jenisLabel", Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }
                                else if (total == 0){
                                    var bayar = activityDibayar + alertJumlah
                                    Log.d(TAG, "BAYAR $bayar")
                                    paymentViewModel.insertData(dataBayar)
                                    viewModel.updateDataJumlah(total, fromUid, true, simpleDateFormat.toString(), bayar)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, "${getString(R.string.berhasil_dilunasi)} Rp ${dateFormat.format(alertJumlah)}", Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }else if(alertJumlah > activityJumlah){
                                    Toast.makeText(this, getString(R.string.cek_kembali_jumlah_yang_anda_masukkan), Toast.LENGTH_SHORT).show()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, "${getString(R.string.nominal_tidak_boleh_melebihi)} sisa $jenisLabel", Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }else {
                                    var bayar = activityDibayar + alertJumlah
                                    paymentViewModel.insertData(dataBayar)
                                    viewModel.updateDataJumlah(total, fromUid, false, "-", bayar)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(
                                            binding.clRootContent,
                                            "${getString(R.string.berhasil_dicicil)} Rp ${dateFormat.format(alertJumlah)}",
                                            Snackbar.LENGTH_SHORT
                                        ).show() },700)
                                }
                            }
                            2 -> {
                                var total = activityJumlah + alertJumlah
                                Log.d(TAG, "TOTAL DITAMBAH $total")
                                paymentViewModel.insertData(dataBayar)
                                viewModel.updateDataJumlah(total, fromUid, false, "-", activityDibayar)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    Snackbar.make(
                                        binding.clRootContent,
                                        "${getString(R.string.berhasil_ditambah)} Rp ${dateFormat.format(alertJumlah)}",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                },700)
                            }
                            0 -> {
                                var total = activityJumlah - alertJumlah
                                Log.d(TAG, "TOTAL LUNAS $total")
                                if(total == 0){
                                    var bayar = activityDibayar + alertJumlah
                                    paymentViewModel.insertData(dataBayar)
                                    viewModel.updateDataJumlah(total, fromUid, true, simpleDateFormat.toString(), bayar)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(
                                            binding.clRootContent,
                                            "${getString(R.string.berhasil_dilunasi)} Rp ${dateFormat.format(alertJumlah)}",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    },900)

                                }else {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, getString(R.string.nampaknya_mengalami_bug), Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }
                            }
                        }
                    }
            }
            }
        }
    }

    private fun onRules(alertBinding: AlertDicicilBinding, optionsString: Int) {
        alertBinding.etKeteranganAlert.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.length >= 210){
                    alertBinding.etKeteranganAlert.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
                } else if(p0.startsWith(" ")){
                    alertBinding.etKeteranganAlert.helperText = getString(R.string.keterangan_tidak_boleh_diawali_dengan_huruf_kosong)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else {
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(210))
                        alertBinding.etKeteranganAlert.helperText = getString(R.string.bila_ada_keterangan_yang_di_hutang)
                }
            }
        })
        alertBinding.etJumlahAlert.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(optionsString){

                }
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.length >= 9){
                    alertBinding.etJumlahAlert.helperText = getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                }else if (p0.startsWith("-")){
                    alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_diawali_nilai_negative)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith("0")){
                    alertBinding.etJumlahAlert.helperText = getString(R.string.tidak_boleh_diawali_dengan_0)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
                }else if(p0.startsWith(" ")){
                    alertBinding.etJumlahAlert.error = getString(R.string.tidak_boleh_diawali_dengan_nilai_kosong)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(".")){
                    alertBinding.etJumlahAlert.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(",")){
                    alertBinding.etJumlahAlert.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                } else {
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                    alertBinding.etJumlahAlert.helperText = getString(R.string.masukkan_jumlah_nominal)
                }
            }
        })
    }

    private fun setData(
        uid: String
    ) {
        Log.d(TAG, "UID FROM ADAPTER $uid")
        val id = uid.toIntOrNull() ?: return
        viewModel.getDataWhereId(id).observe(this, Observer {
            with(binding) {
                if (it != null) {
                    currentDataModel = it
                    val jenisPinjaman = if (!it.jenisPinjaman) getString(R.string.hutang) else getString(R.string.piutang)
                    jenis = jenisPinjaman
                    activityJumlah = it.jumlah
                    activityDibayar = it.jumlah_dibayar

                    val decimalFormat = DecimalFormat("##,###,###")

                    // Top Toolbar & Profile
                    tvToolbarTitle.text = getString(R.string.detail_format, jenisPinjaman)
                    tvProfileName.text = it.username
                    val initial = if (it.username.isNotEmpty()) it.username.first().uppercaseChar().toString() else "U"
                    tvAvatarLetter.text = initial
                    tvProfileRole.text = if (it.jenisPinjaman) getString(R.string.kreditur) else getString(R.string.debitur)

                    // Big Summary Card
                    val totalOriginal = it.jumlah + it.jumlah_dibayar
                    tvMainTotalLabel.text = getString(R.string.total_format, jenisPinjaman)
                    tvMainTotalAmount.text = getString(R.string.prefix_rp) + decimalFormat.format(totalOriginal)
                    tvAmountPaid.text = getString(R.string.prefix_rp) + decimalFormat.format(it.jumlah_dibayar)
                    tvAmountRemaining.text = getString(R.string.prefix_rp) + decimalFormat.format(it.jumlah)

                    if (it.statusDataSelesai || it.jumlah == 0) {
                        tvStatusBadgeCard.text = getString(R.string.lunas)
                        tvStatusBadgeCard.setBackgroundResource(R.drawable.bg_badge_lunas)
                        tvStatusBadgeCard.setTextColor(ContextCompat.getColor(this@DetailHutangActivity, R.color.hupisan_paid_text))
                        btnActionAddPayment.visibility = View.GONE
                    } else {
                        tvStatusBadgeCard.text = getString(R.string.belum_lunas)
                        tvStatusBadgeCard.setBackgroundResource(R.drawable.bg_badge_belum_lunas)
                        tvStatusBadgeCard.setTextColor(ContextCompat.getColor(this@DetailHutangActivity, R.color.hupisan_debt_text))
                        btnActionAddPayment.visibility = View.VISIBLE
                    }

                    // Info Rows
                    val jatuhTempoStr = if (it.jatuhTempo.isNotEmpty()) {
                        if (it.jam_jatuh_tempo.isNotEmpty() && it.jam_jatuh_tempo != "-") {
                            "${it.jatuhTempo} - ${it.jam_jatuh_tempo}"
                        } else it.jatuhTempo
                    } else it.diBuat
                    tvInfoJatuhTempo.text = jatuhTempoStr
                    tvInfoCatatan.text = if (it.deskripsi.isNotEmpty()) it.deskripsi else getString(R.string.pinjaman_untuk_keperluan)

                    // Legacy content binding
                    content.tvNama.text = it.username
                    content.tvTanggalDibuat.text = it.diBuat
                    content.tvDescription.text = it.deskripsi
                    content.tvJumlah.text = getString(R.string.prefix_rp) + decimalFormat.format(it.jumlah)
                    content.tvJumlahStatus.text = getString(R.string.label_jumlah_jenis, jenisPinjaman)
                }
            }
        })
    }
    private fun showPaymentOptionsDialog() {
        val dialogBinding = com.hupisan.databinding.DialogPaymentOptionsBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnOptionCicil.setOnClickListener {
            alertDialog.dismiss()
            setAlert(1) // cicil
        }
        dialogBinding.btnOptionLunas.setOnClickListener {
            alertDialog.dismiss()
            setAlert(0) // lunas
        }
        dialogBinding.btnOptionTambah.setOnClickListener {
            alertDialog.dismiss()
            setAlert(2) // tambah
        }
        dialogBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun alertEditNama() {
        val dialogBinding = com.hupisan.databinding.DialogEditNoteBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.etCatatan.setText(binding.tvInfoCatatan.text)
        dialogBinding.etCatatan.setSelection(dialogBinding.etCatatan.text?.length ?: 0)

        dialogBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnSimpan.setOnClickListener {
            val newDesc = dialogBinding.etCatatan.text.toString().trim()
            if (newDesc.isNotEmpty()) {
                binding.tvInfoCatatan.text = newDesc
                Snackbar.make(binding.clRootContent, getString(R.string.catatan_berhasil_diperbarui), Snackbar.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showMoreOptionsDialog() {
        val dialogBinding = com.hupisan.databinding.DialogMoreOptionsBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnOptionWa.setOnClickListener {
            alertDialog.dismiss()
            sendWhatsAppReminder()
        }
        dialogBinding.btnOptionDelete.setOnClickListener {
            alertDialog.dismiss()
            alertDelete()
        }
        dialogBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun sendWhatsAppReminder() {
        val model = currentDataModel ?: return
        val jenisLabel = if (model.jenisPinjaman) "piutang" else "hutang"
        val tempoText = if (model.jatuhTempo.isNotEmpty()) "jatuh tempo pada ${model.jatuhTempo}" else ""
        val message = "Halo ${model.username}, sekadar mengingatkan catatan $jenisLabel sebesar Rp ${decimalFormat.format(model.jumlah)} $tempoText. Catatan ini dicatat melalui aplikasi Hupisan. Terima kasih banyak ya 🙏"
        val waUrl = "https://api.whatsapp.com/send?text=" + java.net.URLEncoder.encode(message, "UTF-8")
        try {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(waUrl))
            startActivity(intent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.bagikan_catatan_via)))
        }
    }

    private fun alertDelete() {
        val uid = intent.getIntExtra(EXTRA_UID, intent.getStringExtra(EXTRA_UID)?.toIntOrNull() ?: 0)
        if (uid == 0 && currentDataModel == null) return
        val dialogBinding = com.hupisan.databinding.DialogConfirmDeleteBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvMessage.text = getString(R.string.konfirmasi_hapus_catatan_format, binding.tvProfileName.text)

        dialogBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnHapus.setOnClickListener {
            currentDataModel?.let { model -> viewModel.deleteData(model) }
            paymentViewModel.deleteDataFormUid(uid)
            Snackbar.make(binding.clRootContent, getString(R.string.catatan_berhasil_dihapus), Snackbar.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 600)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    companion object {
        const val EXTRA_UID = "extra_uid"
        const val EXTRA_TYPE_JENIS = "extra_type_jenis"

        val decimalFormat = DecimalFormat("##,###,###")
    }
}