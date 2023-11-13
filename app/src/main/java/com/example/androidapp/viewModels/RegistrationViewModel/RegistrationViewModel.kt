package com.example.androidapp.viewModels.RegistrationViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.androidapp.features.mainUrl
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class RegistrationViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {
    var userName by mutableStateOf("")
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var userMessage by mutableStateOf("")


    suspend fun createUser(): Int {


        val jsonBody = JSONObject()
        jsonBody.put("user_name", userName)
        jsonBody.put("login", login)
        jsonBody.put("password", password)


        val request = Request.Builder()
            .url("$mainUrl/registration") // Замените на URL вашего сервера
            .post(jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        try {
            val response = withContext(Dispatchers.IO) {
                sharedViewModel.client.newCall(request).execute()
            }

             if (response.isSuccessful) {
                 userMessage =   "You successfully create an account"
                 delay(1000)
                 return 0
            } else if (response.code == 409) {
                 userMessage =   "this user already exist"
                 return 1
            } else {
                 userMessage =  "Occurred error in user registration"
                 return 1
             }
        } catch (e: IOException) {
            userMessage = "A network error has occurred"
            return 1
        }
    }
}
