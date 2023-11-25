package com.example.androidapp.viewModels.RegistrationViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.androidapp.features.mainUrl
import com.example.androidapp.viewModels.SharedViewModel
import com.google.firebase.messaging.FirebaseMessaging
import createRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocketListener
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginViewModel(
    private val sharedViewModel: SharedViewModel,
    private val webSocketListener: WebSocketListener) : ViewModel() {
    var login by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var userMessage by mutableStateOf("")


    suspend fun login(navController: NavHostController) {

        val jsonBody = JSONObject()
        jsonBody.put("login", login)
        jsonBody.put("password", password)

        try {
            val token = FirebaseMessaging.getInstance().token.await() // Дождитесь получения токена

            jsonBody.put("FCMtoken", token)

            val request = Request.Builder()
                .url("$mainUrl/login")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            val response = withContext(Dispatchers.IO) {
                sharedViewModel.httpsClient.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseString = response.body?.string()
                try {
                    val jsonObject = JSONObject(responseString!!)
                    sharedViewModel.userId = jsonObject.optInt("id")
                    sharedViewModel.userName = jsonObject.optString("user_name")
                    sharedViewModel.login = login
                    sharedViewModel.password = password
                    sharedViewModel.hasLogIn = true
                    sharedViewModel.webSocket = sharedViewModel.client.newWebSocket(createRequest(sharedViewModel), webSocketListener)
                    sharedViewModel.saveToSharedPreferences()
                    navController.navigate("main_screen")
                } catch (_: JSONException) {}
            } else if (response.code == 401) {
                userMessage = "Wrong login or password"
            } else {
                userMessage = "Got error while log in"
            }

            response.close()
        } catch (e: IOException) {
            userMessage = "A network error has occurred"
        }
    }
}