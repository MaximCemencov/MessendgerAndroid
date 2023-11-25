package com.example.androidapp.websocket

import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.createChat.MainChatScreenViewModel
import org.json.JSONObject
import java.io.IOException

class WorkWithWebsocket(
    private val messagesViewModel: MessagesViewModel,
    private val mainChatScreenViewModel: MainChatScreenViewModel,
    ) {
    fun workWithResponse(data: String) {
        try {
            val messageObject = JSONObject(data)

            when (messageObject.getString("type")) {
                "new_message" -> messagesViewModel.newMessage(messageObject)
                "updated_message" -> messagesViewModel.updatedMessage(messageObject)
                "delete_message" -> messagesViewModel.deleteMessage(messageObject)
                "readed_message" -> messagesViewModel.messageViewed(messageObject)
                "deleted_chat" -> mainChatScreenViewModel.archiveChats(messageObject)
                "get_chats" -> mainChatScreenViewModel.getChats(messageObject)
                "update_chat" -> {}
                "get_new_chats" -> {}
                "create_chat" -> {}

            }
        } catch (_: IOException) { }
    }


}