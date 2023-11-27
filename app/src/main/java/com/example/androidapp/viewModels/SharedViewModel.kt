package com.example.androidapp.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.features.ClearToken
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class SharedViewModel(private val context: Context) : ViewModel() {
    val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .pingInterval(5, TimeUnit.SECONDS)
        .build()

    val httpsClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(500000, TimeUnit.SECONDS)
        .pingInterval(5, TimeUnit.SECONDS)
        .build()

    var selectedBitmap by mutableStateOf<Pair<Bitmap, String>?>(null)

    lateinit var webSocket: WebSocket

    var avatarBase64 by mutableStateOf<String?>(null)
    var theme by mutableIntStateOf(1)
    var hasLogIn by mutableStateOf(false)
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var userName by mutableStateOf("")
    var userId by mutableIntStateOf(0)
    var userId2 by mutableIntStateOf(0)
    var userName2 by mutableStateOf("")
    var currentChatId by mutableIntStateOf(0)


    @SuppressLint("StaticFieldLeak")
    private val appContext = context.applicationContext

    fun saveToSharedPreferences() {
        val sharedPreferences = appContext.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("theme", theme)
        editor.putString("password", password)
        editor.putBoolean("hasLogIn", hasLogIn)
        editor.putString("login", login)
        editor.putString("userName", userName)
        editor.putInt("userId", userId)
        editor.putInt("user_id2", userId2)
        editor.putString("user_name2", userName2)
        editor.putInt("current_chat_id", currentChatId)
        editor.putString("avatarUri", avatarBase64)

        editor.apply()
    }

    private fun loadFromSharedPreferences() {
        val sharedPreferences = appContext.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)

        password = sharedPreferences.getString("password", "") ?: ""
        theme = sharedPreferences.getInt("theme", 0)
        hasLogIn = sharedPreferences.getBoolean("hasLogIn", false)
        login = sharedPreferences.getString("login", "") ?: ""
        userName = sharedPreferences.getString("userName", "") ?: ""
        userId = sharedPreferences.getInt("userId", 0)
        userId2 = sharedPreferences.getInt("user_id2", 0)
        userName2 = sharedPreferences.getString("user_name2", "") ?: ""
        currentChatId = sharedPreferences.getInt("current_chat_id", 0)
        currentChatId = sharedPreferences.getInt("current_chat_id", 0)
        avatarBase64 = sharedPreferences.getString("avatarUri", null)
    }

    fun logout() {
        viewModelScope.launch {
            ClearToken(client, login, password, userId)
        }
        hasLogIn = false
        password = ""
        login = ""
        userName = ""
        userId = 0
        userId2 = 0
        userName2 = ""
        currentChatId = 0
        avatarBase64 = null
        saveToSharedPreferences()
    }


    private fun isSystemInDarkTheme(): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
    }


    fun getTheme(): Boolean {
        return when (theme) {
            0 -> false
            1 -> isSystemInDarkTheme()
            2 -> true
            else -> false
        }
    }


    init {
        loadFromSharedPreferences()
    }
}

