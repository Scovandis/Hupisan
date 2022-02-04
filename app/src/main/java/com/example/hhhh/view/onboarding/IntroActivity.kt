package com.example.hhhh.view.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.hhhh.view.main.MainActivity
import com.example.hhhh.R
import com.example.hhhh.databinding.ActivityIntroBinding
import com.rd.animation.type.AnimationType.WORM

class IntroActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = IntroAdapter(supportFragmentManager, this)

        binding.tvNext.setOnClickListener {
            if (getItem(+1) > binding.viewPager.childCount -1){
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }else {
                binding.viewPager.setCurrentItem(getItem(+1), true)
            }
        }

        binding.tvPrev.setOnClickListener {
            if (getItem(-1) > binding.viewPager.childCount + 1) {
                finish()
            } else {
                binding.viewPager.setCurrentItem(getItem(-1), true)
            }
        }
        binding.tvSkip.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.indicatorView.setAnimationType(WORM)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.tvPrev.visibility = View.INVISIBLE
                        binding.tvNext.visibility = View.VISIBLE
                        binding.tvSkip.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.tvSkip.visibility = View.GONE
                        binding.tvNext.visibility = View.VISIBLE
                        binding.tvPrev.visibility = View.VISIBLE
                        binding.tvNext.text = getString(R.string.selesai)
                    }
                    else -> {
                        binding.tvNext.visibility = View.VISIBLE
                        binding.tvPrev.visibility = View.VISIBLE
                        binding.tvSkip.visibility = View.VISIBLE
                        binding.tvNext.text = getString(R.string.selanjutnya)
                    }
                }
            }
            override fun onPageScrollStateChanged(position: Int) {}
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        })
    }

    private fun getItem(i: Int): Int = binding.viewPager.currentItem + i

}