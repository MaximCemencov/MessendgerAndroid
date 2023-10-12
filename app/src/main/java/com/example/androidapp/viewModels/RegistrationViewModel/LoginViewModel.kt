package com.example.androidapp.viewModels.RegistrationViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.androidapp.features.mainUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginViewModel : ViewModel() {
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var userMessage by mutableStateOf("")


    suspend fun checkLogin(sharedViewModel: SharedViewModel, navController: NavHostController) {
        val client = OkHttpClient()


        // Создаем JSON объект с логином и паролем
        val jsonBody = JSONObject()
        jsonBody.put("login", login)
        jsonBody.put("password", password)


        // Создаем запрос
        val request = Request.Builder()
            .url("$mainUrl/login") // Замените на URL вашего сервера
            .post(jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()


        try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseString = response.body?.string() // Получаем тело ответа в виде строки
                try {
                    val jsonObject = JSONObject(responseString)
                    sharedViewModel.userId = jsonObject.optInt("id") // Устанавливаем userId в sharedViewModel
                    sharedViewModel.userName = jsonObject.optString("user_name")
                    sharedViewModel.login = login
                    sharedViewModel.hasLogIn = true
                    userMessage = "You successfully Log in"
                    navController.navigate("main_screen")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Обработка ошибки при разборе JSON
                }
            } else if (response.code == 401) {
                userMessage =  "wrong login or password"
            } else {
                "Got error while log in: ${response.message}"
            }

            response.close()
        } catch (e: IOException) {
            userMessage = "Произошла сетевая ошибка: ${e.message}"
        }
    }
}