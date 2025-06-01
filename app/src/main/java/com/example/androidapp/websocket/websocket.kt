package com.example.androidapp.websocket

import android.util.Log
import com.example.androidapp.viewModels.SharedViewModel
import com.example.androidapp.features.createRequest
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MyWebSocketListener(
    private val workWithWebsocket: WorkWithWebsocket,
    private val sharedViewModel: SharedViewModel,
) : WebSocketListener() {
    private val TAG = "Test123412341"


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(TAG, "onOpen $response")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        workWithWebsocket.workWithResponse(text)
        Log.d(TAG, "onMessage: $text")
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
        reconnect()
    }

    private fun reconnect() {
        Log.d(TAG, "Reconnecting...")

        sharedViewModel.webSocket.cancel()
        sharedViewModel.webSocket = sharedViewModel.client.newWebSocket(createRequest(sharedViewModel),this)
    }
}
