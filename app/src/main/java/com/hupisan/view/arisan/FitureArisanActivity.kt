package com.hupisan.view.arisan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.ArisanGroupViewModel

import com.hupisan.R
import com.hupisan.databinding.ActivityFitureArisanBinding
import com.hupisan.view.main.MainActivity

class FitureArisanActivity : AppCompatActivity() {
    lateinit var binding: ActivityFitureArisanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityFitureArisanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener {
            finish()
        }

        setOnActionView()
    }

    private fun setOnActionView(){
        binding.cardViewBuatAcara.setOnClickListener {
            statusDimulai = false
            startActivity(Intent(this, ArisanActivity::class.java))
        }
        binding.cardViewAcaraYangDimulai.setOnClickListener {
            statusDimulai = true
            startActivity(Intent(this, ArisanActivity::class.java))
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

    companion object{
        var statusDimulai: Boolean = false
        var DATA_ISNULL = 0
    }
}