package com.hupisan.view.pengaturan

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hupisan.BuildConfig
import com.hupisan.R
import com.hupisan.databinding.ActivitySettingsBinding
import com.hupisan.databinding.AlertLanguageBinding
import com.hupisan.util.AppsConfig
import com.hupisan.view.bantuan.BantuanActivity
import com.hupisan.view.main.MainActivity
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var appsConfig: AppsConfig

    val versionCode = BuildConfig.VERSION_CODE
    val versionName = BuildConfig.VERSION_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener {
            finish()
        }

        appsConfig = AppsConfig(this)
        binding.tvAppVersion.text = getString(R.string.app_version) +" " +  versionName
        setActionView()

        binding.progressbar.visibility = View.GONE
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val backStateName = fragment::class.java.simpleName
        val manager = supportFragmentManager
        val fragmentPoped: Boolean = manager.popBackStackImmediate(backStateName, 0)

        if (!fragmentPoped){
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.constraint, fragment)
            transaction.addToBackStack(backStateName)
            transaction.commit()
        }
    }
    private fun setActionView(){

        binding.relative5.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/paypalme/Dxy404"))
            startActivity(browserIntent)
        }
        binding.relative2.setOnClickListener {
            showAlertDialog()
        }

        val openTerms = View.OnClickListener {
            IS_TRIMES = 0
            supportFragmentManager.beginTransaction().add(R.id.constraint, PrivacyFragment())
                .commit()
        }
        binding.tvTrems.setOnClickListener(openTerms)
        binding.relative3.setOnClickListener(openTerms)

        val openPrivacy = View.OnClickListener {
            IS_TRIMES = 1
            supportFragmentManager.beginTransaction().add(R.id.constraint, PrivacyFragment())
                .commit()
        }
        binding.tvPrivacy.setOnClickListener(openPrivacy)
        binding.relative4.setOnClickListener(openPrivacy)

        binding.relativeBantuan.setOnClickListener {
            startActivity(Intent(this, BantuanActivity::class.java))
        }
    }

    var dLocaleLanguage: String? = null
    private fun showAlertDialog(){
        val alertBinding = AlertLanguageBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(alertBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val currentLang = appsConfig.getRegionLanguage()
        when (currentLang) {
            "English" -> alertBinding.rbLanguageEnglish.isChecked = true
            "Thailand" -> alertBinding.rbLanguageThailand.isChecked = true
            "Vietnamese" -> alertBinding.rbLanguageVietnamese.isChecked = true
            "Hindi" -> alertBinding.rbLanguageIndia.isChecked = true
            else -> alertBinding.rbLanguageIndonesia.isChecked = true
        }

        alertBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radiobtn: RadioButton = alertBinding.radioGroup.findViewById(checkedId)
            when (radiobtn.id) {
                R.id.rb_language_english -> dLocaleLanguage = "English"
                R.id.rb_language_indonesia -> dLocaleLanguage = "Indonesia"
                R.id.rb_language_thailand -> dLocaleLanguage = "Thailand"
                R.id.rb_language_vietnamese -> dLocaleLanguage = "Vietnamese"
                R.id.rb_language_india -> dLocaleLanguage = "Hindi"
            }
        }

        alertBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertBinding.btnSimpan.setOnClickListener {
            val lang = dLocaleLanguage ?: appsConfig.getRegionLanguage()
            if (lang.isNotEmpty()) {
                setLanguage(lang)
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun setLanguage(dLanguage: String){
        binding.progressbar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            var lang = "English"
            Log.d(TAG, "dLocaleLanguage $dLanguage")
            appsConfig.saveRegionLanguage(dLanguage!!)
            if (dLanguage.equals(getString(R.string.english))){
                lang = "en"
            }else if (dLanguage.equals(getString(R.string.indonesia))){
                lang = "in"
            }else if(dLanguage.equals(getString(R.string.thailand))){
                lang = "tpi"
            }else if(dLanguage.equals(getString(R.string.vietnamese))){
                lang = "vun"
            }else if(dLanguage.equals(getString(R.string.india))){
                lang = "kok"
            }

            val locale = Locale(lang)
            Locale.setDefault(locale)
            val configuration = Configuration()

            configuration.locale = locale
            baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
            binding.progressbar.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finishAffinity()
        }, 1200)
    }
    private val TAG = SettingsActivity::class.java.simpleName
    companion object{
        var IS_TRIMES = 2
    }
}