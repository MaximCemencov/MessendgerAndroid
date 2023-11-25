package com.example.androidapp.features

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

suspend fun ClearToken(client: OkHttpClient, login: String, password: String, id: Int) {
    val jsonBody = JSONObject()
    jsonBody.put("id", id)
    jsonBody.put("login", login)
    jsonBody.put("password", password)


    try {
        val request = Request.Builder()
            .url("$mainUrl/logout")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).execute()
        }
    } catch (_: IOException) { }
}