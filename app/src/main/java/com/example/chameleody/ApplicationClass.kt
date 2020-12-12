package com.example.chameleody

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class ApplicationClass : Application() {
    companion object{
        const val CHANNEL_ID_1 = "channel1"
        const val CHANNEL_ID_2 = "channel2"
        const val ACTION_PREVIOUS = "actionPrevious"
        const val ACTION_NEXT = "actionNext"
        const val ACTION_PLAY = "actionPlay"
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel1 = NotificationChannel(CHANNEL_ID_1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 1 Desc..."
            val channel2 = NotificationChannel(CHANNEL_ID_2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH)
            channel2.description = "Channel 2 Desc..."
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
        }
    }
}