package com.example.androidapp.websocket

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.example.androidapp.viewModels.MessagesViewModel.MessagesViewModel
import com.example.androidapp.viewModels.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class MyWebSocketListener(
    private val messagesViewModel: MessagesViewModel,
    private val sharedViewModel: SharedViewModel,
    private val coroutineScope: CoroutineScope,
    private val lazyListState: LazyListState
): WebSocketListener() {
    private val TAG = "Test123412341"


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

        val jsonBody = JSONObject()
        jsonBody.put("chat_id", 0)
        jsonBody.put("sender_id", sharedViewModel.userId)
        jsonBody.put("recipient_id", -1)
        jsonBody.put("content", "")
        jsonBody.put("time_of_day", "")

        webSocket.send(jsonBody.toString())
        Log.d(TAG, "onOpen $response")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        messagesViewModel.viewModelScope.launch {
            messagesViewModel.getNewMessage(sharedViewModel, text, lazyListState, coroutineScope)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d(TAG, "onClosing: $code $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d(TAG, "onClosed: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d(TAG, "onFailure: ${t.message} $response")
        super.onFailure(webSocket, t, response)
    }
}
