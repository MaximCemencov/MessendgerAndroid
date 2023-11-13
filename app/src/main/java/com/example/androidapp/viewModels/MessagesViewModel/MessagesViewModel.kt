package com.example.androidapp.viewModels.MessagesViewModel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.androidapp.DataClass.Message
import com.example.androidapp.features.getCurrentDateTimeInUTC
import com.example.androidapp.features.limit
import com.example.androidapp.features.mainUrl
import com.example.androidapp.features.parseCustomTime
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MessagesViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {
    var messages = mutableStateListOf(listOf<Message>())
    val textState = mutableStateOf("")

    fun clearMessages() {
        if (messages.isNotEmpty()) {
            messages.clear()
        }
    }

    suspend fun getMessages(
        offset: Int,
        lazyListState: LazyListState,
        coroutineScope: CoroutineScope
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("chat_id", sharedViewModel.current_chat_id)
        jsonBody.put("offset", offset)
        jsonBody.put("login", sharedViewModel.login)
        jsonBody.put("password", sharedViewModel.password)

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$mainUrl/get_past_messages")
            .post(requestBody)
            .build()

        try {
            val data = withContext(Dispatchers.IO) {
                val response = sharedViewModel.client.newCall(request).execute()
                if (response.isSuccessful)
                    response.body?.string()
                else
                    null
            }

            if (data != null) {
                val jsonArray = JSONArray(data)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    val senderId = jsonObject.getInt("sender_id")
                    val content = jsonObject.getString("content")
                    val timeStamp = jsonObject.getString("time_stamp")

                    val message = Message(
                        content,
                        senderId,
                        parseCustomTime(timeStamp)!!,
                        senderId == sharedViewModel.userId
                    )

                    messages.add(listOf(message))
                }

                coroutineScope.launch {
                    if (offset == 0) {
                        val lastIndex = messages.size - 1
                        lazyListState.scrollToItem(lastIndex)
                    } else {
                        lazyListState.scrollToItem(limit + 1)
                    }
                }
            }
        } catch (_: IOException) {
        }
    }


    fun newMessage(webSocket: WebSocket) {
        if (textState.value.isBlank()) {
            return
        }

        val jsonBody = JSONObject()
        sharedViewModel.loadFromSharedPreferences()
        jsonBody.put("chat_id", sharedViewModel.current_chat_id)
        jsonBody.put("sender_id", sharedViewModel.userId)
        jsonBody.put("recipient_id", sharedViewModel.user_id2)
        jsonBody.put("content", textState.value)
        textState.value = ""
        jsonBody.put("time_of_day", getCurrentDateTimeInUTC())

        webSocket.send(jsonBody.toString())
    }

    fun getNewMessage(data: String, lazyListState: LazyListState, coroutineScope: CoroutineScope) {
        try {
            val messageObject = JSONObject(data)

            val senderId = messageObject.getInt("sender_id")
            val chatId = messageObject.getInt("chat_id")
            val timeStamp = messageObject.getString("time_of_day")
            val content = messageObject.getString("content")

            if (chatId != sharedViewModel.current_chat_id) {
                return
            }
            val newMessage = Message(
                content,
                senderId,
                parseCustomTime(timeStamp)!!,
                sharedViewModel.userId == senderId
            )


            messages.add(0, listOf(newMessage))
            coroutineScope.launch {
                val lastIndex = messages.size - 1
                lazyListState.scrollToItem(lastIndex)
            }


        } catch (_: IOException) {
        }
    }
}
