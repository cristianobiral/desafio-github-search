package br.com.igorbag.githubsearch.data.local

import android.app.Activity
import android.content.Context

class SharedPreferencesManager(private val activity: Activity) {

    fun save(key: SharedPreferencesKey, value: String) {
        val preferences = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(preferences.edit()) {
            putString(key.toString(), value)
            apply()
        }
    }

    fun get(key: SharedPreferencesKey): String? {
        val preferences = activity.getPreferences(Context.MODE_PRIVATE)
        return preferences.getString(key.toString(), null)
    }

}