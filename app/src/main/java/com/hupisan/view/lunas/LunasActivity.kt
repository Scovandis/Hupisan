package com.hupisan.view.lunas

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.hupisan.R
import com.hupisan.databinding.ActivityLunasBinding
import com.hupisan.listener.OnBottomSheetCallback
import com.hupisan.view.main.MainActivity

class LunasActivity : AppCompatActivity() {
    private val TAG = LunasActivity::class.java.simpleName
    lateinit var binding: ActivityLunasBinding
    private var listener: OnBottomSheetCallback? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLunasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_primary_dark)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.lunas)
        supportActionBar!!.elevation = 0f

        configureBackdrop()
        setToggleMenuButtons()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    fun setOnBottomSheetCallbacks(onBottomSheetCallbacks: OnBottomSheetCallback) {
        this.listener = onBottomSheetCallbacks
    }

    fun closeBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setToggleMenuButtons() {
        binding.materialButtonToggleGroupDifficulty.addOnButtonCheckedListener { _, checkedId, _ ->
            toggleButton(findViewById(checkedId))
            handleCheckedButtonsInDifficulty()
        }
    }
    private fun toggleButton(button: MaterialButton){
        if (button.textColors.defaultColor == ContextCompat.getColor(this, R.color.white)) {
            button.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.hupisan_accent_amber))
            button.setTextColor(ContextCompat.getColor(this, R.color.hupisan_accent_amber))
        } else {
            button.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            button.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun handleCheckedButtonsInDifficulty() {
        when (binding.materialButtonToggleGroupDifficulty.checkedButtonId){
            R.id.type_hutang -> {
                STATUS_TYPE = getString(R.string.hutang)
            }
            R.id.type_piutang -> {
                STATUS_TYPE = getString(R.string.piutang)
            }
            else -> {
            STATUS_TYPE = null
        }
        }
    }

    private fun configureBackdrop() {
        val fragment = supportFragmentManager.findFragmentById(R.id.filter_fragment)
        fragment?.view?.let {
            BottomSheetBehavior.from(it).let { bs ->
                bs.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        listener?.onStateChanged(bottomSheet, newState)
                    }
                })
                bs.state = BottomSheetBehavior.STATE_EXPANDED
                mBottomSheetBehavior = bs
            }
        }
    }

    companion object{
        var STATUS_TYPE: String? = null
    }
}