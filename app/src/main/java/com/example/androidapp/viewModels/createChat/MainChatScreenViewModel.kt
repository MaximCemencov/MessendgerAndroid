package com.example.androidapp.viewModels.createChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.DataClass.Chat
import com.example.androidapp.features.mainUrl
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainChatScreenViewModel(sharedViewModel: SharedViewModel) : ViewModel() {


    private val _allUsers = MutableStateFlow(listOf<Chat>())
    val allUsers = _allUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    init {
        parseChats(sharedViewModel)
    }


    fun parseChats(sharedViewModel: SharedViewModel) {
        viewModelScope.launch {
            _isLoading.value = true

            _allUsers.value = listOf()

            val jsonBody = JSONObject()

            jsonBody.put("id", sharedViewModel.userId)
            jsonBody.put("password", sharedViewModel.password)
            jsonBody.put("login", sharedViewModel.login)


            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$mainUrl/get_chats")
                .post(requestBody)
                .build()

            try {
                val response = withContext(Dispatchers.IO) {
                    sharedViewModel.client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val data = response.body?.string()

                    val chatsArray = JSONArray(data)

                    val chatList = mutableListOf<Chat>()

                    for (i in 0 until chatsArray.length()) {
                        val chatObject = chatsArray.getJSONObject(i)

                        val userName = chatObject.getString("user_name")
                        val chatUserId = chatObject.getString("id")
                        val chatId = chatObject.getString("chat_id")
                        val content = chatObject.getString("last_message")

                        chatList.add(Chat(userName, chatUserId, chatId, content))
                    }
                    _allUsers.value = chatList
                }
            } catch (_: IOException) {
            }
            _isLoading.value = false
        }
    }
}