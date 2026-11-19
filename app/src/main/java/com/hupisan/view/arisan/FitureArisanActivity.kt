package com.hupisan.view.arisan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.ArisanGroupViewModel

import com.hupisan.R
import com.hupisan.databinding.ActivityFitureArisanBinding
import com.hupisan.view.main.MainActivity

class FitureArisanActivity : AppCompatActivity() {
    lateinit var binding: ActivityFitureArisanBinding
    val groupVM: ArisanGroupViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFitureArisanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.arisan)
        setOnActionView()
    }
    private fun setOnActionView(){
        binding.cardViewBuatAcara.setOnClickListener {
            statusDimulai = false
            groupVM.getAllGroupWhereStatus(statusDimulai).observe(this, Observer {
                DATA_ISNULL = if (it.isEmpty()){ 0 }else{ 1 }
            })
            val intent = Intent(this, ArisanActivity::class.java)
            startActivity(intent)
        }
        binding.cardViewAcaraYangDimulai.setOnClickListener {
            statusDimulai = true
            groupVM.getAllGroupWhereStatus(statusDimulai).observe(this, Observer {
                DATA_ISNULL = if (it.isEmpty()){0} else {1}
            })
            startActivity(Intent(this, ArisanActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }else ->{
                super.onOptionsItemSelected(item)
            }
        }

    }

    companion object{
        var statusDimulai: Boolean = false
        var DATA_ISNULL = 0
    }
}