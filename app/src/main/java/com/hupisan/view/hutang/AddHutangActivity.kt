package com.hupisan.view.hutang

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.DataViewModel
import com.hupisan.R
import com.hupisan.R.id
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityAddHutangBinding
import com.hupisan.notification.service.NotificationService
import com.hupisan.view.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class AddHutangActivity : AppCompatActivity(){
    private val TAG = AddHutangActivity::class.java.simpleName

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

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_primary_dark)

        try {
            binding.tvDibuatPada.text = SimpleDateFormat("EEEE, d MMM yyyy", Locale("id", "ID")).format(Date())
        } catch (e: Exception) {
            binding.tvDibuatPada.text = SimpleDateFormat("dd/MM/yyyy").format(Date())
        }
        binding.tvDateTime.text = SimpleDateFormat("dd/MM/yyyy").format(Date())
        binding.tvTime.text = SimpleDateFormat("HH:mm").format(Date())

        nNotif = binding.switchOnNotif

        nJenis = intent.getStringExtra(EXTRA_STRING_JENIS)
        val isPiutang = nJenis.equals(getString(R.string.piutang), ignoreCase = true)
        if (isPiutang) {
            binding.tvLabelNama.text = "Nama Kreditur"
            binding.etNama.editText?.hint = "Masukkan nama kreditur"
            binding.tvLabelJumlah.text = "Jumlah Piutang"
        } else {
            binding.tvLabelNama.text = "Nama Debitur"
            binding.etNama.editText?.hint = "Masukkan nama debitur"
            binding.tvLabelJumlah.text = "Jumlah Hutang"
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val screenTitle = if (nJenis?.contains("Catat", ignoreCase = true) == true) nJenis else "Catat ${nJenis ?: getString(R.string.hutang)}"
        supportActionBar!!.title = screenTitle

        Log.d(TAG, "GET STRINGEXTRA $nJenis")
        onActionView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                val intent = Intent(this, HutangActivity::class.java)
                intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, nJenis)
                startActivity(intent)
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun onSavedData(){
        val uid = UUID.randomUUID()
        nDiBuat = SimpleDateFormat("dd/MM/yyyy").format(Date())
        nUname = binding.etNama.editText?.text.toString()
        nDeskripsi = binding.etKeterangan.editText?.text.toString()
        var jumlah = binding.etJumlah.editText?.text.toString().replace(" ", "").replace(".", "").trim()
        nJatuhTempo = binding.tvDateTime.text.toString()
        nTime = binding.tvTime.text.toString()
        //flase hutang true piutang
        val jenis: Boolean = !nJenis.equals(getString(R.string.hutang))
        Log.d(TAG, "JENIS $jenis")
        try {
            nJumlah= jumlah.toInt()
        } catch (e: NumberFormatException) {
            Log.e("exptn", e.toString())
        }
        val notif = nNotif!!.isChecked
        Log.d(TAG, "dibuat : $nDiBuat\nusername : $nUname\njumlah nominal : $nJumlah\nketerangan : $nDeskripsi\njatuh tempo : $nJatuhTempo\ngender : $nGender\njenis : $nJenis\nnotif : $notif \nwaktu : $nTime")

        if (nUname.length <= 2){
            binding.etNama.error = getString(R.string.nama_tidak_boleh_kurang_dari_2_huruf)
        }else {
            if (binding.etJumlah.editText!!.text.isNullOrEmpty() || jumlah.isEmpty() || nJumlah == 0){
                binding.etJumlah.error = getString(R.string.nominal_tidak_boleh_kosong)
            }else {
                if (nDeskripsi.length > 210){
                    binding.etKeterangan.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
                } else {
                    val data = DataModel(0, nUname, nDeskripsi, nJumlah, nDiBuat ?: "", nJatuhTempo ?: "", notif, nGender, jenis, false, nTime ?: "", "-", nDibayar)
                    Log.d(TAG, "dataInsert $data")
                    dbViewModel.insertData(data)
                    Snackbar.make(binding.constraint, getString(R.string.data_berhasil_di_dimpan), Snackbar.LENGTH_SHORT).show()
                    isNotification()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this, HutangActivity::class.java)
                        intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, nJenis)
                        startActivity(intent)
                    }, 1200)
                }
            }
        }
    }
    private fun isNotification(){
        if (nNotif!!.isChecked){
            val myDate = "$nJatuhTempo $nTime"
            val sdf1 = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val jtem = sdf1.parse(myDate)
            val mil = jtem?.time ?: System.currentTimeMillis()
            val millis = System.currentTimeMillis()
            Log.d(TAG, "MILLLISEEECOND $millis")
            Log.d(TAG, "TOTAL ${mil - millis}")
            val postDelay = mil - millis

            getNotification("${getString(R.string.ada)} $nJenis ${getString(R.string.yang_harus_dibayar)} ${getString(R.string.dari)} $nUname")?.let { scheduleNotification(it, postDelay.toInt()) }
        }
    }

    private fun scheduleNotification(notification: Notification, delay: Int) {
        val notificationIntent = Intent(this, NotificationService::class.java)
        notificationIntent.putExtra(NotificationService.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(NotificationService.NOTIFICATION, notification)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            flags
        )
        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    private fun getNotification(content: String): Notification? {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, AddHutangActivity.default_notification_channel_id)
        builder.setContentTitle(getString(R.string.jatuh_tempo))
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_foreground_apps)
        builder.setAutoCancel(true)
        builder.setChannelId(MainActivity.NOTIFICATION_CHANNEL_ID)
        return builder.build()
    }

    private fun onActionView(){
        binding.cardViewTanggal.setOnClickListener {
            val calender: Calendar = Calendar.getInstance()
            day = calender.get(Calendar.DAY_OF_MONTH)
            month = calender.get(Calendar.MONTH)
            year = calender.get(Calendar.YEAR)

            val datePicker = DatePickerDialog(this@AddHutangActivity, R.style.DialogTheme,
                { view, myYears, monthOfYear, dayOfMonth ->
                    val mont = monthOfYear +1
                    binding.tvDateTime.text = "${dayOfMonth}/${mont}/$myYears"
                }, year, month, day
            )
            val now = System.currentTimeMillis() - 1000
            datePicker.datePicker.minDate = now

            datePicker.setOnShowListener {
                val primaryColor = ContextCompat.getColor(this, R.color.hupisan_primary_dark)
                datePicker.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor)
                datePicker.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor)
            }
            datePicker.show()
        }
        binding.cardViewTime.setOnClickListener { showTimePickerDialog() }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            when (radio.id) {
                id.rb_gender_laki -> {
                    nGender = 1
                }
                id.rb_gender_perempuan -> {
                    nGender = 2
                }
                id.rb_gender_kosong -> {
                    nGender = 3
                }
                else -> {
                    Toast.makeText(this, getString(R.string.pilih_salah_satu), Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.etKeterangan.editText!!.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length >= 210){
                    binding.etKeterangan.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
                }else if(s.startsWith(" ")){
                    binding.etKeterangan.error = getString(R.string.keterangan_tidak_boleh_diawali_dengan_huruf_kosong)
                    binding.etKeterangan.editText!!.text.trim()
                    s.filters = arrayOf<InputFilter>(LengthFilter(0))
                }else {
                    s.filters = arrayOf<InputFilter>(LengthFilter(210))
                    binding.etKeterangan.helperText = getString(R.string.bila_ada_keterangan_yang_di_hutang)
                }
            }
        })

        binding.etNama.editText!!.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length <= 0){
                    binding.etNama.error = getString(R.string.nama_tidak_boleh_kosong)
                }else if(p0.toString().length >= 20){
                    binding.etNama.error = getString(R.string.tidak_boleh_lebih_dari_20_huruf)
                }else{
                    binding.etNama.error = ""
                }
            }
        })

        binding.etJumlah.editText!!.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.length >= 9){
                    binding.etJumlah.helperText = getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
                    p0.filters = arrayOf<InputFilter>(LengthFilter(9))
                }else if (p0.startsWith("-")){
                    binding.etJumlah.error = getString(R.string.nominal_tidak_boleh_diawali_nilai_negative)
                    p0.filters = arrayOf<InputFilter>(LengthFilter(0))
                }else if(p0.startsWith("0")){
                    binding.etJumlah.helperText = getString(R.string.tidak_boleh_diawali_dengan_0)
                    p0.filters = arrayOf<InputFilter>(LengthFilter(1))
                }else if(p0.startsWith(" ")){
                    binding.etJumlah.error = getString(R.string.tidak_boleh_diawali_dengan_nilai_kosong)
                    p0.filters = arrayOf(LengthFilter(0))
                }else if(p0.startsWith(".")){
                    binding.etJumlah.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(LengthFilter(0))
                }else if(p0.startsWith(",")){
                    binding.etJumlah.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(LengthFilter(0))
                } else {
                    p0.filters = arrayOf<InputFilter>(LengthFilter(9))
                    binding.etJumlah.helperText = getString(R.string.masukkan_jumlah_nominal)
                }
            }
        })

        binding.btnSimpan.setOnClickListener {
            onSavedData()
        }

    }
    fun checkDigit(number: Int): String? {
        return if (number <= 9) "0$number" else number.toString()
    }

    private fun showTimePickerDialog(){
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        var am_pm: String
        val mTimePicker: TimePickerDialog = TimePickerDialog(this,R.style.DialogTheme,
            { timePicker, selectedHour, selectedMinute ->
            if (selectedHour < 12){
                am_pm = "AM"
            }else{
                am_pm = "PM"
            }
                binding.tvTime.text = "" + checkDigit(selectedHour) + ":" +checkDigit(selectedMinute)
            },
            hour,
            minute,
            true
        ) //Yes 24 hour time
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
