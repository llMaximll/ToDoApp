package com.github.llmaximll.todoapp.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat.*
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.utils.vectorToBitmap

class NotifyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
        val workId = inputData.getLong(NOTIFICATION_WORK_ID, 0)
        val title = inputData.getString(NOTIFICATION_SUBTITLE)
            ?: applicationContext.getString(R.string.notification_title)
        sendNotification(id, workId, title)

        return success()
    }

    private fun sendNotification(id: Int, workId: Long, title: String) {
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_launcher_foreground)

        val pendingIntent = NavDeepLinkBuilder(applicationContext).apply {
            setGraph(R.navigation.nav_graph)
            setDestination(R.id.detailsFragment)
            setArguments(bundleOf("taskId" to workId))
        }.createPendingIntent()

        val notification = Builder(applicationContext, NOTIFICATION_CHANNEL).apply {
            setLargeIcon(bitmap).setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            setContentTitle(title)
            setDefaults(DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true)
        }
        notification.priority = PRIORITY_MAX

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            channel.apply {
                enableLights(true)
                lightColor = RED
                channel.setSound(ringtoneManager, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = "ToDoApp_notification_id"
        const val NOTIFICATION_NAME = "ToDoApp"
        const val NOTIFICATION_CHANNEL = "ToDoApp_channel_01"
        const val NOTIFICATION_WORK = "ToDoApp_notification_work"
        const val NOTIFICATION_WORK_ID = "ToDoApp_notification_work_id"
        const val NOTIFICATION_SUBTITLE = "ToDoApp_notification_subtitle"
    }

}