package com.shorman.shoppinglist.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shorman.shoppinglist.R
import com.shorman.shoppinglist.ui.activities.MainActivity
import kotlin.random.Random

const val CHANNEL_ID = "my_channel"

class FirebaseService:FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationId= Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.mipmap.logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId,notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "Notification Channel"
        val channel = NotificationChannel(CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "this channel get the last notifications"
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }
}