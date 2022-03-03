package my.notes.HHHH.view.spalshscreen

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import my.notes.HHHH.R
import my.notes.HHHH.databinding.ActivitySplashScreenBinding
import my.notes.HHHH.databinding.AlertLanguageBinding
import my.notes.HHHH.util.AppsConfig
import my.notes.HHHH.view.main.MainActivity
import my.notes.HHHH.view.onboarding.IntroActivity
import java.util.*


class SplashScreenActivity : AppCompatActivity() {
    private val TAG = SplashScreenActivity::class.java.simpleName
    lateinit var appsConfig: AppsConfig

    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appsConfig = AppsConfig()
        appsConfig.AppsConfig(this)
        val nLanguage = appsConfig.getRegionLanguage()
        Log.d(TAG, "N LANGUAGE $nLanguage")
        if (nLanguage != null){
            loadLanguage(false)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Snackbar.make(binding.constraint, getString(R.string.izin_diberikan), Snackbar.LENGTH_SHORT).show()
            moveTo()
        }else {

            requestPermissions()
        }
    }

    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Toast.makeText(
                            this@SplashScreenActivity,
                            getString(R.string.semua_izin_diberikan),
                            Toast.LENGTH_SHORT
                        ).show()
                        moveTo()
                    }else{
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, getString(R.string.terjadi_kesalahan), Toast.LENGTH_SHORT).show()
            }
            .onSameThread().check()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@SplashScreenActivity, R.style.AlertDialog)
        builder.setTitle(getString(R.string.butuh_perizinan))

        builder.setMessage(getString(R.string.aplikasi_ini_memerlukan_izin_untuk_menggunakan_fitur_ini_anda_dapat_memeberikannya_di_pengaturan_aplikasi))
        builder.setPositiveButton(getString(R.string.pergi_ke_pengaturan)){ dialog, which ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton(getString(R.string.kembali)){ dialog, which ->
            Snackbar.make(binding.constraint, getString(R.string.izin_ditolak), Snackbar.LENGTH_SHORT).show()
            dialog.cancel()
        }
        builder.show()
    }

    private fun moveTo() {
        try{
            Handler(Looper.getMainLooper()).postDelayed({
                val isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true)
                appsConfig.saveThemePreferences("1")
                if (isFirstRun){
                    showAlertDialog()
                }else{
                    loadLanguage(false)
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }, 1000)
        }catch (e: Exception){
            Log.d(TAG, "ERROR WHILE $e")
        }
    }
    var dLocalLanguage: String= "English"
    private fun showAlertDialog(){
            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val alertBinding = AlertLanguageBinding.inflate(inflater)
            dialogBuilder.setView(alertBinding.root)
            val lang = appsConfig.getRegionLanguage()
            Log.d(TAG, "LANG APPSCONFIG $lang")

            alertBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radiobtn: RadioButton = alertBinding.radioGroup.findViewById(checkedId)
                when (radiobtn.id) {
                    R.id.rb_language_english -> {
                        dLocalLanguage = radiobtn.text.toString()
                    }
                    R.id.rb_language_indonesia -> {
                        dLocalLanguage = radiobtn.text.toString()
                    }
                    R.id.rb_language_thailand -> {
                        dLocalLanguage = radiobtn.text.toString()
                    }
                    R.id.rb_language_vietnamese -> {
                        dLocalLanguage = radiobtn.text.toString()
                    }
                    R.id.rb_language_india -> {
                        dLocalLanguage = radiobtn.text.toString()
                    }
                    else -> {
                        Toast.makeText(this, R.string.pilih_salah_satu, Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d(TAG, "dLOCALELANGUASE $dLocalLanguage")
            }

            dialogBuilder.setPositiveButton(getString(R.string.iya)){
                    dialogInterface, p0 ->
                Log.d(TAG, "Language dLocaleLanguage $dLocalLanguage")
                appsConfig.saveRegionLanguage(dLocalLanguage!!)
                Log.d(TAG, "DLOCALE LANGUAGE $dLocalLanguage")
                appsConfig.updateUserLoginStatus(true)
                loadLanguage(true)
                startActivity(Intent(this, IntroActivity::class.java))
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply()
                dialogInterface.cancel()
            }
            val alertDialog = dialogBuilder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF6200EE"))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FF6200EE"))
        }
            alertDialog.show()
    }
    private fun loadLanguage(isLoad: Boolean){
        if (isLoad){
            var lang = dLocalLanguage.toString()
            if(lang.equals(getString(R.string.english))){
                lang ="en"
            }else if (lang.equals(getString(R.string.indonesia))){
                lang = "in"
            }else if(lang.equals(getString(R.string.thailand))){
                lang = "tpi"
            }else if(lang.equals(getString(R.string.vietnamese))){
                lang = "vun"
            }else if(lang.equals(getString(R.string.india))){
                lang = "kok"
            }

            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
//            val myLocale = Locale(lang)
//            val res: Resources = resources
//            val dm: DisplayMetrics = res.getDisplayMetrics()
//            val conf: Configuration = res.getConfiguration()
//            conf.locale = myLocale
//            res.updateConfiguration(conf, dm)
//            val refresh = Intent(this, MainActivity::class.java)
//            finish()
//            startActivity(refresh)
        }else {
            var language = appsConfig.getRegionLanguage()
            Log.d(TAG, "LANGUAGE getRegion $language")

            if(language.equals(getString(R.string.english))){
                language ="en"
            }else if (language.equals(getString(R.string.indonesia))){
                language = "in"
            }else if(language.equals(getString(R.string.thailand))){
                language = "tpi"
            }else if(language.equals(getString(R.string.vietnamese))){
                language = "vun"
            }else if(language.equals(R.string.india)){
                language = "kok"
            }

            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()

            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }
}