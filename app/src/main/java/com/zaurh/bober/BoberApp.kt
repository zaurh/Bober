package com.zaurh.bober

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BoberApp : Application() {


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel1 = NotificationChannel(
            "foreground",
            "Foreground notifications",
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = "Get notifications everywhere."
        }

        val channel2 = NotificationChannel(
            "chat",
            "Chat notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving message notifications."
        }

        val channel3 = NotificationChannel(
            "like",
            "Like notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving like notifications."
        }

        val channel4 = NotificationChannel(
            "match",
            "Match notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving match notifications."
        }

        notificationManager.createNotificationChannels(listOf(channel1, channel2, channel3, channel4))
    }
}

