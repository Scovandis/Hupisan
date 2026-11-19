package com.hupisan.util

import android.content.Context
import android.content.SharedPreferences
import com.hupisan.R

class AppsConfig(private var context: Context? = null) {
    private var sharedPreference: SharedPreferences? = null

    init {
        context?.let { initPrefs(it) }
    }

    fun AppsConfig(context: Context) {
        this.context = context
        initPrefs(context)
    }

    private fun initPrefs(ctx: Context) {
        sharedPreference = ctx.getSharedPreferences(
            ctx.getString(R.string.pref_file_key),
            Context.MODE_PRIVATE
        )
    }

    fun isUserLogin(): Boolean {
        val key = context?.getString(R.string.pref_is_user_login) ?: "pref_is_user_login"
        return sharedPreference?.getBoolean(key, false) ?: false
    }

    fun saveRegionLanguage(language: String) {
        sharedPreference?.edit()?.putString("region_language", language)?.apply()
    }

    fun saveThemePreferences(theme: String) {
        sharedPreference?.edit()?.putString("theme_preferences", theme)?.apply()
    }

    fun updateUserLoginStatus(status: Boolean) {
        val key = context?.getString(R.string.pref_is_user_login) ?: "pref_is_user_login"
        sharedPreference?.edit()?.putBoolean(key, status)?.apply()
    }

    fun getRegionLanguage(): String {
        return sharedPreference?.getString("region_language", "") ?: ""
    }

    fun getThemePreferences(): String {
        return sharedPreference?.getString("theme_preferences", "1") ?: "1"
    }
}
