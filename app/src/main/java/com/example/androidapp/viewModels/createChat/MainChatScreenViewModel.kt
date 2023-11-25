package com.example.androidapp.viewModels.createChat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.androidapp.DataClass.Chat
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class MainChatScreenViewModel(
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    val allChats = mutableStateListOf<Chat>()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    fun parseChats() {
        _isLoading.value = true
        val jsonBody = JSONObject()
        jsonBody.put("user_id", sharedViewModel.userId)
        jsonBody.put("type", "get_chats")

        sharedViewModel.webSocket.send(jsonBody.toString())
    }

    fun getChats(messageObject: JSONObject) {
        allChats.clear()


       val chatsArray = messageObject.getJSONArray("chats")

        if (chatsArray.length() == 0) {
            _isLoading.value = false
            return
        }

        for (i in 0 until chatsArray.length()) {
            val chatObject = chatsArray.getJSONObject(i)

            val userName = chatObject.getString("user_name")
            val chatUserId = chatObject.getString("id")
            val chatId = chatObject.getString("chat_id")
            val content = chatObject.getString("last_message")
            val notViewedMessages = chatObject.getInt("not_readed_messages")
            val hasAvatar = chatObject.getBoolean("avatar")

            allChats.add(Chat(userName, chatUserId, chatId, content, notViewedMessages, hasAvatar))
        }
        _isLoading.value = false
    }

    fun deleteChat(chat: Chat) {
        val jsonBody = JSONObject()

        jsonBody.put("chat_id", chat.chatId)
        jsonBody.put("user_id", sharedViewModel.userId)
        jsonBody.put("type", "archive_chat")

        sharedViewModel.webSocket.send(jsonBody.toString())
    }

    fun archiveChats(messageObject: JSONObject) {
        val chatId = messageObject.getInt("chat_id").toString()
        allChats.removeAll { it.chatId == chatId }
    }
}
