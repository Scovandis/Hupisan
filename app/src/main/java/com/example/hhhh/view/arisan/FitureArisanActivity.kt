package com.example.hhhh.view.arisan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hhhh.R
import com.example.hhhh.database.ArisanGroupViewModel
import com.example.hhhh.databinding.ActivityFitureArisanBinding
import com.example.hhhh.view.main.MainActivity

class FitureArisanActivity : AppCompatActivity() {
    lateinit var binding: ActivityFitureArisanBinding
    lateinit var groupVM: ArisanGroupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFitureArisanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.arisan)
        groupVM = ViewModelProvider(this).get(ArisanGroupViewModel::class.java)
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