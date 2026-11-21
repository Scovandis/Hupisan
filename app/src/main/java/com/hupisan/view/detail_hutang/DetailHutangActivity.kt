package com.hupisan.view.detail_hutang

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.hupisan.notification.ReminderScheduler
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
            if (savePaymentData(alertBinding, statusOption)) {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    private fun savePaymentData(alertBinding: AlertDicicilBinding, statusOption: Int): Boolean {
        val fromUid = intent.getIntExtra(EXTRA_UID, intent.getStringExtra(EXTRA_UID)?.toIntOrNull() ?: 0)
        Log.d(TAG, " savePaymentData uid : $fromUid")
        val dibuat = alertBinding.tvAlertDibuatPada.text.toString()
        val keterangan = alertBinding.etKeteranganAlert.editText!!.text.toString()
        val aJumlah = if (statusOption == 0) {
            activityJumlah.toString()
        } else {
            alertBinding.etJumlahAlert.editText?.text?.toString() ?: ""
        }
        val alertJumlah = if (statusOption == 0) {
            activityJumlah
        } else {
            aJumlah.replace(" ", "").replace(".", "").replace(",", "").toIntOrNull() ?: 0
        }

        Log.d(TAG , "ACTIVITY JUMLAH ::::::::::::::::: $activityJumlah")
        Log.d(TAG, "ALERT JUMLAH :::::::  $alertJumlah")
        Log.d(TAG, "ALERT SATUS $statusOption")
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        if (statusOption != 0 && aJumlah.length > 9) {
            alertBinding.etJumlahAlert.helperText =
                getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
            return false
        }
        if (statusOption != 0 && aJumlah.isEmpty()) {
            alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_kosong)
            return false
        }
        if (keterangan.length > 210) {
            alertBinding.etKeteranganAlert.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
            return false
        }
        if (alertJumlah <= 0) {
            alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_kosong)
            return false
        }

        val dateFormat = DecimalFormat("##,###,###")
        val dataBayar = PaymentModel(
            0,
            fromUid,
            dibuat,
            alertJumlah,
            keterangan,
            statusOption,
            typeJenis ?: false
        )
        val jenisLabel = if (typeJenis == true) getString(R.string.piutang) else getString(R.string.hutang)
        when (statusOption) {
            1 -> {
                val total = activityJumlah - alertJumlah
                Log.d(TAG, "TOTAL DIKURANGI $total")
                if (total < 0) {
                    alertBinding.etJumlahAlert.error = "${getString(R.string.nominal_tidak_boleh_melebihi)} ${getString(R.string.sisa_tagihan)} $jenisLabel"
                    return false
                } else if (total == 0) {
                    val bayar = activityDibayar + alertJumlah
                    paymentViewModel.insertData(dataBayar)
                    viewModel.updateDataJumlah(total, fromUid, true, simpleDateFormat, bayar)
                    ReminderScheduler.cancel(this, fromUid)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Snackbar.make(binding.clRootContent, "${getString(R.string.berhasil_dilunasi)} ${getString(R.string.prefix_rp)}${dateFormat.format(alertJumlah)}", Snackbar.LENGTH_SHORT).show()
                    }, 300)
                } else {
                    val bayar = activityDibayar + alertJumlah
                    paymentViewModel.insertData(dataBayar)
                    viewModel.updateDataJumlah(total, fromUid, false, "-", bayar)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Snackbar.make(
                            binding.clRootContent,
                            "${getString(R.string.berhasil_dicicil)} ${getString(R.string.prefix_rp)}${dateFormat.format(alertJumlah)}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }, 300)
                }
            }
            2 -> {
                val total = activityJumlah + alertJumlah
                Log.d(TAG, "TOTAL DITAMBAH $total")
                paymentViewModel.insertData(dataBayar)
                viewModel.updateDataJumlah(total, fromUid, false, "-", activityDibayar)
                Handler(Looper.getMainLooper()).postDelayed({
                    Snackbar.make(
                        binding.clRootContent,
                        "${getString(R.string.berhasil_ditambah)} ${getString(R.string.prefix_rp)}${dateFormat.format(alertJumlah)}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }, 300)
            }
            0 -> {
                val bayar = activityDibayar + alertJumlah
                paymentViewModel.insertData(dataBayar)
                viewModel.updateDataJumlah(0, fromUid, true, simpleDateFormat, bayar)
                ReminderScheduler.cancel(this, fromUid)
                Handler(Looper.getMainLooper()).postDelayed({
                    Snackbar.make(
                        binding.clRootContent,
                        "${getString(R.string.berhasil_dilunasi)} ${getString(R.string.prefix_rp)}${dateFormat.format(alertJumlah)}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }, 300)
            }
        }
        return true
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
                    tvProfileRole.text = if (it.jenisPinjaman) getString(R.string.debitur) else getString(R.string.kreditur)

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
        val current = currentDataModel ?: return
        val dialogBinding = com.hupisan.databinding.DialogEditNoteBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.etEditNama.setText(current.username)
        dialogBinding.etEditNama.setSelection(dialogBinding.etEditNama.text?.length ?: 0)
        dialogBinding.etEditNominal.setText(current.jumlah.toString())
        dialogBinding.etEditNominal.isEnabled = !current.statusDataSelesai
        dialogBinding.etCatatan.setText(if (current.deskripsi == "-") "" else current.deskripsi)

        var selectedDate = current.jatuhTempo.ifBlank {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
        var selectedTime = current.jam_jatuh_tempo.takeIf { it.isNotBlank() && it != "-" } ?: "09:00"

        dialogBinding.tvEditDueDate.text = selectedDate
        dialogBinding.tvEditReminderTime.text = selectedTime
        dialogBinding.switchEditReminder.isChecked = current.notif
        dialogBinding.rowEditTime.visibility = if (current.notif) View.VISIBLE else View.GONE

        dialogBinding.switchEditReminder.setOnCheckedChangeListener { _, isChecked ->
            dialogBinding.rowEditTime.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        val openDatePicker = {
            val cal = Calendar.getInstance()
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedDate)?.let { cal.time = it }
            } catch (_: Exception) {}
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val picked = Calendar.getInstance().apply { set(year, month, day) }
                    selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(picked.time)
                    dialogBinding.tvEditDueDate.text = selectedDate
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dialogBinding.rowEditDate.setOnClickListener { openDatePicker() }
        dialogBinding.tvEditDueDate.setOnClickListener { openDatePicker() }

        val openTimePicker = {
            val parts = selectedTime.split(":")
            val initH = parts.getOrNull(0)?.toIntOrNull() ?: 9
            val initM = parts.getOrNull(1)?.toIntOrNull() ?: 0
            TimePickerDialog(
                this,
                R.style.DialogTheme,
                { _, h, m ->
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                    dialogBinding.tvEditReminderTime.text = selectedTime
                },
                initH,
                initM,
                true
            ).show()
        }

        dialogBinding.rowEditTime.setOnClickListener { openTimePicker() }
        dialogBinding.tvEditReminderTime.setOnClickListener { openTimePicker() }

        dialogBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnSimpan.setOnClickListener {
            val newName = dialogBinding.etEditNama.text?.toString()?.trim() ?: ""
            val newNominalStr = dialogBinding.etEditNominal.text?.toString()?.replace(".", "")?.replace(",", "")?.trim() ?: ""
            val newNominal = if (current.statusDataSelesai) current.jumlah else (newNominalStr.toIntOrNull() ?: -1)
            val newDesc = dialogBinding.etCatatan.text?.toString()?.trim() ?: ""
            val newNotif = dialogBinding.switchEditReminder.isChecked

            if (newName.length < 2) {
                dialogBinding.tilEditNama.error = getString(R.string.nama_tidak_boleh_kurang_dari_2_huruf)
                return@setOnClickListener
            } else {
                dialogBinding.tilEditNama.error = null
            }

            if (!current.statusDataSelesai && newNominal <= 0) {
                dialogBinding.tilEditNominal.error = getString(R.string.nominal_tidak_boleh_kosong)
                return@setOnClickListener
            } else {
                dialogBinding.tilEditNominal.error = null
            }

            val updatedModel = DataModel(
                id = current.id,
                username = newName,
                deskripsi = newDesc,
                jumlah = newNominal,
                diBuat = current.diBuat,
                jatuhTempo = selectedDate,
                notif = newNotif,
                genderUser = current.genderUser,
                jenisPinjaman = current.jenisPinjaman,
                statusDataSelesai = current.statusDataSelesai,
                jam_jatuh_tempo = if (newNotif) selectedTime else "-",
                tanggal_lunas = current.tanggal_lunas,
                jumlah_dibayar = current.jumlah_dibayar
            )

            viewModel.updateData(updatedModel)
            if (newNotif && !current.statusDataSelesai) {
                ReminderScheduler.schedule(this, updatedModel.id, updatedModel.jatuhTempo, updatedModel.jam_jatuh_tempo)
            } else {
                ReminderScheduler.cancel(this, updatedModel.id)
            }

            Snackbar.make(binding.clRootContent, getString(R.string.catatan_berhasil_diperbarui), Snackbar.LENGTH_SHORT).show()
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
        val amountText = getString(R.string.prefix_rp) + decimalFormat.format(model.jumlah)
        val cleanDesc = model.deskripsi.trim().takeIf { it.isNotEmpty() && it != "-" }
        val descSuffix = if (cleanDesc != null) " ($cleanDesc)" else ""
        val message = if (model.jenisPinjaman) {
            getString(R.string.wa_reminder_piutang_format, model.username, amountText, model.jatuhTempo, descSuffix)
        } else {
            getString(R.string.wa_reminder_hutang_format, model.username, amountText, model.jatuhTempo, descSuffix)
        }
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
            currentDataModel?.let { model ->
                ReminderScheduler.cancel(this, model.id)
                viewModel.deleteData(model)
            }
            ReminderScheduler.cancel(this, uid)
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