package com.hupisan.view.add_hutang

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.DataViewModel
import com.hupisan.R
import com.hupisan.R.id
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityAddHutangBinding
import com.hupisan.notification.ReminderScheduler
import com.hupisan.view.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class AddHutangActivity : AppCompatActivity(){
    private val TAG = AddHutangActivity::class.java.simpleName

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                binding.switchReminder.isChecked = false
                Snackbar.make(binding.constraint, getString(R.string.izin_notif_dibutuhkan), Snackbar.LENGTH_LONG).show()
            }
        }

    var day : Int= 0
    var month : Int = 0
    var year : Int= 0
    var hours : Int = 0
    var minute : Int= 0
    var myDay : Int = 0
    var myMonth : Int = 0
    var myYears : Int = 0
    var myHours : Int = 0
    var myMinutes : Int= 0

    var nGender: Int = 1
    var nUname = "-"
    var nDeskripsi = "-"
    var nJumlah: Int = 0
    var nDibayar: Int = 0
    var nDiBuat: String? = null
    var nJatuhTempo: String? = null
    var nNotif: SwitchMaterial? = null
    var nJenis: String? = null
    var nTime: String? = null

    val dbViewModel: DataViewModel by viewModel()

    private lateinit var binding: ActivityAddHutangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHutangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.tvSelectedDate.text = todayDate
        nJatuhTempo = todayDate
        nTime = "09:00"
        binding.tvReminderTime.text = nTime

        nJenis = intent.getStringExtra(EXTRA_STRING_JENIS) ?: getString(R.string.hutang)
        val isPiutang = nJenis.equals(getString(R.string.piutang), ignoreCase = true)
        updateTypeSelection(isPiutang)

        binding.tabToggleHutang.setOnClickListener {
            updateTypeSelection(false)
        }
        binding.tabTogglePiutang.setOnClickListener {
            updateTypeSelection(true)
        }

        val openDatePicker = {
            val calendar = Calendar.getInstance()
            val dpd = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    val cal = Calendar.getInstance()
                    cal.set(year, monthOfYear, dayOfMonth)
                    val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
                    binding.tvSelectedDate.text = formatted
                    nJatuhTempo = formatted
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        binding.cardJatuhTempo.setOnClickListener { openDatePicker() }
        binding.btnPickDate.setOnClickListener { openDatePicker() }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutReminderTime.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                if (nTime == null) {
                    nTime = "09:00"
                    binding.tvReminderTime.text = "09:00"
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }

        binding.tvReminderTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.btnSimpan.setOnClickListener {
            onSavedData()
        }
    }

    private fun updateTypeSelection(isPiutang: Boolean) {
        if (isPiutang) {
            nJenis = getString(R.string.piutang)
            binding.tabTogglePiutang.setBackgroundResource(R.drawable.bg_green_pill)
            binding.tabTogglePiutang.setTextColor(Color.WHITE)
            binding.tabToggleHutang.background = null
            binding.tabToggleHutang.setTextColor(ContextCompat.getColor(this, R.color.hupisan_text_secondary))

            binding.tvToolbarTitle.text = getString(R.string.tambah_piutang)
            binding.tvLabelNama.text = getString(R.string.nama_kreditur)
            binding.etNamaInput.hint = getString(R.string.masukkan_nama_kreditur)
            binding.tvLabelJumlah.text = getString(R.string.total_piutang)
        } else {
            nJenis = getString(R.string.hutang)
            binding.tabToggleHutang.setBackgroundResource(R.drawable.bg_green_pill)
            binding.tabToggleHutang.setTextColor(Color.WHITE)
            binding.tabTogglePiutang.background = null
            binding.tabTogglePiutang.setTextColor(ContextCompat.getColor(this, R.color.hupisan_text_secondary))

            binding.tvToolbarTitle.text = getString(R.string.tambah_hutang)
            binding.tvLabelNama.text = getString(R.string.nama_debitur)
            binding.etNamaInput.hint = getString(R.string.masukkan_nama_debitur)
            binding.tvLabelJumlah.text = getString(R.string.jumlah_utang)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun onSavedData(){
        nDiBuat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        nUname = binding.etNamaInput.text?.toString()?.trim() ?: ""
        nDeskripsi = binding.etCatatanInput.text?.toString()?.trim() ?: ""
        val rawJumlah = binding.etJumlahInput.text?.toString()?.replace(" ", "")?.replace(".", "")?.replace("Rp", "")?.trim() ?: ""
        nJatuhTempo = binding.tvSelectedDate.text?.toString() ?: nDiBuat
        val selectedTime = binding.tvReminderTime.text?.toString()?.trim()?.takeIf { it.isNotEmpty() } ?: nTime ?: "09:00"
        nTime = selectedTime

        val jenis: Boolean = nJenis.equals(getString(R.string.piutang), ignoreCase = true)
        Log.d(TAG, "JENIS $jenis ($nJenis)")

        try {
            nJumlah = rawJumlah.toInt()
        } catch (e: Exception) {
            nJumlah = 0
        }

        if (nUname.length < 2) {
            binding.etNamaInput.error = getString(R.string.nama_tidak_boleh_kurang_dari_2_huruf)
            binding.etNamaInput.requestFocus()
            return
        }

        if (nJumlah <= 0) {
            binding.etJumlahInput.error = getString(R.string.nominal_tidak_boleh_kosong)
            binding.etJumlahInput.requestFocus()
            return
        }

        val hasNotification = binding.switchReminder.isChecked
        val savedReminderTime = if (hasNotification) (nTime ?: "09:00") else "-"
        val data = DataModel(0, nUname, nDeskripsi, nJumlah, nDiBuat ?: "", nJatuhTempo ?: "", hasNotification, 1, jenis, false, savedReminderTime, "-", nDibayar)
        Log.d(TAG, "dataInsert $data")
        val appContext = applicationContext
        val dueDateForAlarm = nJatuhTempo ?: ""
        val timeForAlarm = savedReminderTime
        dbViewModel.insertData(data) { insertedId ->
            if (hasNotification) {
                ReminderScheduler.schedule(appContext, insertedId, dueDateForAlarm, timeForAlarm)
            }
        }
        Snackbar.make(binding.constraint, getString(R.string.data_berhasil_di_dimpan), Snackbar.LENGTH_SHORT).show()
        
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 800)
    }

    private fun onActionView(){
        // Handled directly in onCreate
    }
    fun checkDigit(number: Int): String? {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun showTimePickerDialog(){
        val parts = (nTime ?: "09:00").split(":")
        val initHour = parts.getOrNull(0)?.toIntOrNull() ?: 9
        val initMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val mTimePicker = TimePickerDialog(this, R.style.DialogTheme,
            { _, selectedHour, selectedMinute ->
                val formattedTime = "${checkDigit(selectedHour)}:${checkDigit(selectedMinute)}"
                binding.tvReminderTime.text = formattedTime
                nTime = formattedTime
            },
            initHour,
            initMinute,
            true
        )
        mTimePicker.setOnShowListener {
            val primaryColor = ContextCompat.getColor(this, R.color.hupisan_primary_dark)
            mTimePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor)
            mTimePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor)
        }
        mTimePicker.show()
    }

    companion object{
        private val _filterCharacter = object: InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence {
                for (i in start until end) {
                    if (!Character.isLetterOrDigit(source!!.get(i))) {
                        return "0"
                    }
                }
                return "0"
            }
        }

        var EXTRA_STRING_JENIS = "extra_string_jenis"
        private val default_notification_channel_id = "default"
    }
}
