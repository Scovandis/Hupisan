package my.notes.HHHH.view.bantuan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import my.notes.HHHH.R
import my.notes.HHHH.databinding.ActivityBantuanBinding
import my.notes.HHHH.view.main.MainActivity

class BantuanActivity : AppCompatActivity() {
    lateinit var binding: ActivityBantuanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBantuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = getString(R.string.bantuan)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
}