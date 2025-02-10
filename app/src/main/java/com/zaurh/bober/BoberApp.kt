package com.zaurh.bober

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BoberApp : Application(), ImageLoaderFactory {


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
            "chat",
            "Chat notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving message notifications."
        }

        val channel2 = NotificationChannel(
            "like",
            "Like notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving like notifications."
        }

        val channel3 = NotificationChannel(
            "match",
            "Match notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving match notifications."
        }

        val channel4 = NotificationChannel(
            "react",
            "React notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Receiving react notifications."
        }

        notificationManager.createNotificationChannels(listOf(channel1, channel2, channel3, channel4))
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .respectCacheHeaders(false)
            .build()
    }
}

