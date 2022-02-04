package com.example.hhhh.view.main.adapter

import android.content.Context
import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.hhhh.R
import com.example.hhhh.view.main.AutoSliderFragment
import com.example.hhhh.view.onboarding.IntroFragment

class AutoSliderAdapter(manager: FragmentManager, val context: Context): FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount()= NUMB_ITEM

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> AutoSliderFragment.newInstance(
                context.resources.getString(R.string.buat_catatan_title),
                context.resources.getString(R.string.buat_catatan_des),
                R.raw.buat_cataatan,
//                Color.parseColor("#FF018786")
            )
            1 -> AutoSliderFragment.newInstance(
                context.resources.getString(R.string.monitoring_pengeluaran_title),
                context.resources.getString(R.string.monitoring_pengeluaran_des),
                R.raw.line_chart,
//                Color.parseColor("#EC9411")
//                R.color.publicRed
            )
            2 -> AutoSliderFragment.newInstance(
                context.resources.getString(R.string.lihat_log_aktivasi_title),
                context.resources.getString(R.string.lihat_log_aktivasi_desk),
                R.raw.log_aktivitas,
//                Color.parseColor("#FF3700B3")
//                R.color.Minggu
            )
            else -> {
                null
            }
        }!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "page $position"
    }


    companion object{
        const val NUMB_ITEM = 3
    }
}