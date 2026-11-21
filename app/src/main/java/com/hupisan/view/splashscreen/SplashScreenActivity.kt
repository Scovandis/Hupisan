package com.hupisan.view.splashscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.hupisan.R
import com.hupisan.databinding.ActivitySplashScreenBinding
import com.hupisan.util.AppsConfig
import com.hupisan.view.main.MainActivity
import com.hupisan.view.onboarding.IntroActivity
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    private val TAG = SplashScreenActivity::class.java.simpleName
    lateinit var appsConfig: AppsConfig

    private lateinit var binding: ActivitySplashScreenBinding
    private val requestNotificationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            moveTo()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        appsConfig = AppsConfig(this)
        setupLanguage()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                moveTo()
            } else {
                requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            moveTo()
        }
    }

    private fun moveTo() {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                val isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true)
                appsConfig.saveThemePreferences("1")
                if (isFirstRun) {
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply()
                    appsConfig.updateUserLoginStatus(true)
                    startActivity(Intent(this, IntroActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
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
