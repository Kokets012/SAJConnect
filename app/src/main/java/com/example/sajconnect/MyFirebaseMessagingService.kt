package com.example.sajconnect

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sajconnect.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM_TOKEN", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.d("FCM_RECEIVED", "From: ${remoteMessage.from}")

            // Check if message contains a notification payload
            remoteMessage.notification?.let {
                Log.d("FCM_NOTIFICATION", "Message Notification Body: ${it.body}")
                sendNotification(it.title ?: "SAJ Connect", it.body ?: "New message")
            }
        } catch (e: Exception) {
            Log.e("FCM_ERROR", "Error in onMessageReceived: ${e.message}", e)
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "saj_connect_channel",
                    "SAJ Connect Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notificationBuilder = NotificationCompat.Builder(this, "saj_connect_channel")
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Use system icon for now
                .setAutoCancel(true)

            notificationManager.notify(0, notificationBuilder.build())

            Log.d("FCM_SUCCESS", "Notification displayed: $title - $messageBody")

        } catch (e: Exception) {
            Log.e("FCM_ERROR", "Error sending notification: ${e.message}", e)
        }
    }
}