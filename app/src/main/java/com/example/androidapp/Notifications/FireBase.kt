package com.example.androidapp.Notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.androidapp.R
import com.example.androidapp.features.SharedPreferencesManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val sharedPreferencesManager = SharedPreferencesManager(this)

        var id = 0

        if (!sharedPreferencesManager.getBoolean("isInApp", true)) {
            val messageData = remoteMessage.data
            var messageContent = messageData["content"]
            val fileName = messageData["file"]
            if (fileName != "null") {
                messageContent = fileName
            }
            val senderName = messageData["user_name"]

            // Создаем уведомление
            val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(senderName)
                .setContentText(messageContent)
                .setAutoCancel(true)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(++id, notificationBuilder.build())
        }
    }
}


