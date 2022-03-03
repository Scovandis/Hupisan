package my.notes.HHHH.util

import android.content.Context
import android.content.SharedPreferences
import my.notes.HHHH.R


class AppsConfig {
    private var sharedPreference: SharedPreferences? = null
    private var context: Context? = null
    fun AppsConfig(context: Context) {
        this.context = context
        sharedPreference = context.getSharedPreferences(
            context.getString(R.string.pref_file_key),
            Context.MODE_PRIVATE
        )
    }

    fun isUserLogin(): Boolean{
        return sharedPreference!!.getBoolean(context!!.getString(R.string.pref_is_user_login), false)
    }

    fun saveRegionLanguage(language: String){
        val editor = sharedPreference!!.edit()
        editor.putString("region_language", language)
        editor.apply()
    }
    fun saveThemePreferences(theme: String){
        val editor = sharedPreference!!.edit()
        editor.putString("theme_preferences", theme)
        editor.apply()
    }
    fun updateUserLoginStatus(status: Boolean){
        val editor = sharedPreference!!.edit()
        editor.putBoolean(context!!.getString(R.string.pref_is_user_login), status)
        editor.apply()
    }
    fun getRegionLanguage(): String{
        return sharedPreference!!.getString("region_language", "").toString()
    }
    fun getThemePreferences(): String{
        return sharedPreference!!.getString("theme_preferences", "1").toString()
    }

}
