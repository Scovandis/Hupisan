package com.hupisan.view.splashscreen

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hupisan.R
import com.hupisan.databinding.ActivitySplashScreenBinding
import com.hupisan.util.AppsConfig
import com.hupisan.view.main.MainActivity
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    private val TAG = SplashScreenActivity::class.java.simpleName
    lateinit var appsConfig: AppsConfig

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        appsConfig = AppsConfig(this)
        setupLanguage()
        moveTo()
    }

    private fun moveTo() {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                appsConfig.saveThemePreferences("1")
                appsConfig.updateUserLoginStatus(true)
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 800)
        } catch (e: Exception) {
            Log.d(TAG, "ERROR WHILE $e")
        }
    }

    private fun setupLanguage() {
        val savedLang = appsConfig.getRegionLanguage()
        if (savedLang.isNotEmpty()) {
            val code = when (savedLang) {
                getString(R.string.indonesia) -> "in"
                getString(R.string.thailand) -> "tpi"
                getString(R.string.vietnamese) -> "vun"
                getString(R.string.india) -> "kok"
                else -> "en"
            }
            applyLocale(code)
        } else {
            // Auto-detect system locale:
            // Indonesian -> set Indonesia, other -> set English
            val systemLang = Locale.getDefault().language.lowercase()
            if (systemLang == "in" || systemLang == "id") {
                appsConfig.saveRegionLanguage(getString(R.string.indonesia))
                applyLocale("in")
            } else {
                appsConfig.saveRegionLanguage(getString(R.string.english))
                applyLocale("en")
            }
        }
    }

    private fun applyLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}
