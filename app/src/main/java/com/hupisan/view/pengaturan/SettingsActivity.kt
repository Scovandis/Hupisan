package com.hupisan.view.pengaturan

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import com.hupisan.view.main.MainActivity
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var appsConfig: AppsConfig

    val versionCode = BuildConfig.VERSION_CODE
    val versionName = BuildConfig.VERSION_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_primary_dark)

        appsConfig = AppsConfig(this)
        binding.tvAppVersion.text = getString(R.string.app_version) +" " +  versionName
        setActionView()

        binding.progressbar.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.Pengaturan)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
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
    }

    var dLocaleLanguage: String? = null
    private fun showAlertDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBinding = AlertLanguageBinding.inflate(inflater)
        dialogBuilder.setView(alertBinding.root)

        alertBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radiobtn: RadioButton = alertBinding.radioGroup.findViewById(checkedId)
            when (radiobtn.id) {
                R.id.rb_language_english -> {
                    dLocaleLanguage = radiobtn.text.toString()
                }
                R.id.rb_language_indonesia -> {
                    dLocaleLanguage = radiobtn.text.toString()

                }
                R.id.rb_language_thailand -> {
                    dLocaleLanguage = radiobtn.text.toString()
                }
                R.id.rb_language_vietnamese -> {
                    dLocaleLanguage = radiobtn.text.toString()
                }
                R.id.rb_language_india -> {
                    dLocaleLanguage = radiobtn.text.toString()
                }
                else -> {
                    Toast.makeText(this, R.string.pilih_salah_satu, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogBuilder.setPositiveButton(getString(R.string.iya)){
            dialogInterface, p0 ->
            val lang = dLocaleLanguage ?: appsConfig.getRegionLanguage()
            if (lang.isNotEmpty()) {
                setLanguage(lang)
            }
            dialogInterface.cancel()
        }
        dialogBuilder.setNegativeButton(getString(R.string.kembali)){
            dialogInterface, p0 -> dialogInterface.dismiss()
        }
        val alertDialog = dialogBuilder.create()
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
            startActivity(Intent(this, MainActivity::class.java))
        },1200)
    }
    private val TAG = SettingsActivity::class.java.simpleName
    companion object{
        var IS_TRIMES = 2
    }
}