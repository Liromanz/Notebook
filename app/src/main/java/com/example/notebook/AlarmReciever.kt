package com.example.notebook

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val noteName = intent!!.getStringExtra("Name_Note")
        val noteTime = intent.getStringExtra("Time_Note")

        val intent = Intent(context, MainActivity::class.java)
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context!!, "channelID")
            .setSmallIcon(R.drawable.ic_baseline_calendar_today_24)
            .setContentTitle("Ежедневник")
            .setContentText("$noteName $noteTime")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notification: Notification = builder.build()
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, notification)
    }
}