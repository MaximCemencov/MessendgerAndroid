package com.example.androidapp.viewModels.createChat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DataClass.Users
import com.example.androidapp.features.mainUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CreateChatViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _errorText = MutableStateFlow("")
    val errorText = _errorText.asStateFlow()


    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()


    private val _users = MutableStateFlow(listOf<Users>())

    val users = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_users) { _, users ->
            if (_searchText.value.isNotEmpty())
                _users.value = parseUsers(_searchText.value)
            users
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _users.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private suspend fun parseUsers(userName: String): List<Users> {
        val client = OkHttpClient()


        val jsonBody = JSONObject()
        jsonBody.put("user_name", userName)

        val allUsers = mutableListOf<Users>()


        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$mainUrl/search")
            .post(requestBody)
            .build()

        try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            if (response.code == 200) {
                val data = response.body?.string()
                Log.d("MyTag", data.toString())

                // Парсим JSON-ответ
                val usersArray = JSONArray(data)

                // Проходим по массиву и извлекаем данные
                for (i in 0 until usersArray.length()) {
                    val userObject = usersArray.getJSONObject(i)
                    val userId = userObject.getString("id")
                    val userName = userObject.getString("user_name")

                    // Создаем объект Users и добавляем его в список
                    val user = Users(userId, userName)
                    allUsers.add(user)
                }
            } else if (response.code == 404) {
                _errorText.value = "Users not found!"
            } else {}

        } catch (_: IOException) {

        }
        return allUsers
    }

}


