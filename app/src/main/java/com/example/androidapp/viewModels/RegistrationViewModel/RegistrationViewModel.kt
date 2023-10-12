package com.example.androidapp.viewModels.RegistrationViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.androidapp.features.mainUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException


class RegistrationViewModel : ViewModel() {
    var userName by mutableStateOf("")
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var userMessage by mutableStateOf("")

    suspend fun createUser() {
        // Создаем клиент OkHttp
        val client = OkHttpClient()


        val jsonBody = JSONObject()
        jsonBody.put("user_name", userName)
        jsonBody.put("login", login)
        jsonBody.put("password", password)


        // Создаем запрос
        val request = Request.Builder()
            .url("$mainUrl/registration") // Замените на URL вашего сервера
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString()))
            .build()

        try {
            // Отправляем запрос на сервер в фоновом потоке
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            // Обработка успешного ответа
            userMessage = if (response.isSuccessful) {
                "You successfully create an account"
            } else if (response.code == 409) {
                "this user already exist"
            } else {
                "Произошла ошибка при регистрации пользователя: ${response.message}"
            }

            response.close()
        } catch (e: IOException) {
            // Обработка сетевой ошибки
            userMessage = "Произошла сетевая ошибка: ${e.message}"
        }
    }
}
