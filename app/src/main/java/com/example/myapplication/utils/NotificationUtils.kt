package com.example.myapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R

object NotificationUtils {
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "expense_channel",
                "Expenses",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, message: String) {
        val notification = NotificationCompat.Builder(context, "expense_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Внимание!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(1, notification)
    }
}
