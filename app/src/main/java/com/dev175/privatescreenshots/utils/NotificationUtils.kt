package com.dev175.privatescreenshots.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.util.Pair
import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.ScreenshotApplication
import com.dev175.privatescreenshots.ScreenshotApplication.Companion.CHANNEL_ID
import com.dev175.privatescreenshots.receiver.NotificationReceiver


object NotificationUtils {

    fun getNotification(context: Context): Pair<Int, Notification> {
        val notification = createNotification(context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(175, notification)
        return Pair(175, notification)
    }

    private fun createNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_menu)
        builder.setContentTitle("APP nAME")
        builder.setContentText("Recording")
        builder.setOngoing(true)
        builder.setCategory(Notification.CATEGORY_SERVICE)
        builder.priority = Notification.PRIORITY_LOW
        builder.setShowWhen(true)
        return builder.build()
    }

    fun showNotification(context: Context) : Pair<Int, Notification>{
        val notificationManager = NotificationManagerCompat.from(context)

        val customView = RemoteViews(
            context.packageName,
            R.layout.notification_record
        )
        val recordIntent = Intent(context, NotificationReceiver::class.java)
        val galleryIntent = Intent(context, NotificationReceiver::class.java)
        val settingsIntent = Intent(context, NotificationReceiver::class.java)
        val clickIntent = Intent(context, NotificationReceiver::class.java)
        val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)


        customView.setOnClickPendingIntent(R.id.close_iv, clickPendingIntent)
        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setCustomContentView(customView)
//            .setCustomBigContentView(expandedView)
            .build()
        notificationManager.notify(1, notification)
        return Pair(1, notification)
    }
}
