package com.example.hhhh.view.pengaturan

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.fragment.app.Fragment
import com.example.hhhh.R
import com.example.hhhh.databinding.ActivitySettingsBinding
import com.example.hhhh.databinding.AlertLanguageBinding
import com.example.hhhh.util.AppsConfig
import com.example.hhhh.view.main.MainActivity
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var appsConfig: AppsConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appsConfig = AppsConfig()
        appsConfig.AppsConfig(this)
        setActionView()
        binding.progressbar.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.Pengaturan)
        if (IS_TRIMES == 0 || IS_TRIMES ==1){
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.title = ""
        }

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
            transaction.replace(R.id.frame_layout, fragment)
            transaction.addToBackStack(backStateName)
            transaction.commit()
        }
    }

//    override fun onBackPressed() {
//        if(supportFragmentManager.backStackEntryCount == 1){
//            finish()
//        }else {
//            super.onBackPressed()
//        }
//
//    }
    private fun setActionView(){
//        if (binding.switchOnTheme.isChecked){
//            application.setTheme(R.style.LightTheme)
//        }else {
//            application.setTheme(R.style.DarkTheme)
//        }

        binding.relative2.setOnClickListener {
            showAlertDialog()
        }

        binding.tvTrems.setOnClickListener {
            IS_TRIMES = 0
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, PrivacyFragment())
                .commit()
        }
        binding.tvPrivacy.setOnClickListener {
            IS_TRIMES = 1
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, PrivacyFragment())
                .commit()
        }
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
                else -> {
                    Toast.makeText(this, R.string.pilih_salah_satu, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogBuilder.setPositiveButton(getString(R.string.iya)){
            dialogInterface, p0 ->
            setLanguage(dLocaleLanguage!!)
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
            if (dLanguage.equals("English")){
                lang = "en"
            }else if (dLanguage.equals("Indonesia")){
                lang = "in"
            }else if(dLanguage.equals("Thailand")){
                lang = "tpi"
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