package com.example.androidapp.viewModels

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient

class SharedViewModel(context: Context) : ViewModel() {
    val client = OkHttpClient()


    var hasLogIn by mutableStateOf(false)
    var login by mutableStateOf("")
    var userName by mutableStateOf("")
    var userId by mutableStateOf(0)
    var user_id2 by mutableStateOf(0)
    var user_name2 by mutableStateOf("")
    var current_chat_id by mutableStateOf(0)


    // Поле для хранения Context
    @SuppressLint("StaticFieldLeak")
    private val appContext = context.applicationContext

    fun saveToSharedPreferences() {
        val sharedPreferences = appContext.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("hasLogIn", hasLogIn)
        editor.putString("login", login)
        editor.putString("userName", userName)
        editor.putInt("userId", userId)
        editor.putInt("user_id2", user_id2)
        editor.putString("user_name2", user_name2)
        editor.putInt("current_chat_id", current_chat_id)

        editor.apply()
    }

    // Функция для загрузки переменных из SharedPreferences
    fun loadFromSharedPreferences() {
        val sharedPreferences = appContext.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)

        hasLogIn = sharedPreferences.getBoolean("hasLogIn", false)
        login = sharedPreferences.getString("login", "") ?: ""
        userName = sharedPreferences.getString("userName", "") ?: ""
        userId = sharedPreferences.getInt("userId", 0)
        user_id2 = sharedPreferences.getInt("user_id2", 0)
        user_name2 = sharedPreferences.getString("user_name2", "") ?: ""
        current_chat_id = sharedPreferences.getInt("current_chat_id", 0)
    }

    init {
        loadFromSharedPreferences()
    }
}

