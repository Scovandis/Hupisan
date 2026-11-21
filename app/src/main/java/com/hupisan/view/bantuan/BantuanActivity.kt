package com.hupisan.view.bantuan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.hupisan.R
import com.hupisan.databinding.ActivityBantuanBinding
import com.hupisan.view.main.MainActivity

class BantuanActivity : AppCompatActivity() {
    lateinit var binding: ActivityBantuanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityBantuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener {
            finish()
        }
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
}