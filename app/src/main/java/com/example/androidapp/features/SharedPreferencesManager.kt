package com.example.androidapp.features

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)

    fun saveBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}
