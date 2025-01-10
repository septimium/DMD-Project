package com.example.reminderz

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderForegroundService : Service() {
    private val CHANNEL_ID = "ReminderzChannel"
    private val NOTIFICATION_ID = 1
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        dailyReminders()
        monitorReminders()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reminderz Running")
            .setContentText("Currently monitoring your reminders!")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Reminder Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun monitorReminders() {
        scope.launch {
            while (true) {
                checkDueReminders()
                delay(60*60*1000)
            }
        }
    }

    private fun dailyReminders() {
        scope.launch {
            while (true) {
                val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
                val isDailyNotificationEnabled = sharedPreferences.getBoolean("daily_notification_enabled", false)
                if (isDailyNotificationEnabled) {
                    val notificationTime = sharedPreferences.getString("notification_time", "09:00")
                    val (targetHour, targetMinute) = notificationTime!!.split(":").map { it.toInt() }
                    val calendar = Calendar.getInstance()
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = calendar.get(Calendar.MINUTE)
                    if (currentHour == targetHour && currentMinute == targetMinute) {
                        checkTodayReminders()
                    }
                }
                delay(60000)
            }
        }
    }

    private suspend fun checkTodayReminders() {
        val reminderDao = ReminderDatabase.getDatabase(applicationContext).reminderDao()
        val activeReminders = reminderDao.getActiveReminders()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdfDate.format(Calendar.getInstance().time)
        val sdfDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        activeReminders.forEach { reminder ->
            val reminderDate = sdfDateTime.parse(reminder.dueDate)
            if (reminderDate != null && sdfDate.format(reminderDate) == todayDate) {
                showReminderNotification(reminder)
            }
        }
    }

    private suspend fun checkDueReminders() {
        val reminderDao = ReminderDatabase.getDatabase(applicationContext).reminderDao()
        val activeReminders = reminderDao.getActiveReminders()
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        activeReminders.forEach { reminder ->
            val dueDate = sdf.parse(reminder.dueDate)
            if (dueDate != null && currentTime.after(dueDate)) {
                showDueReminderNotification(reminder)
            }
        }
    }

    private fun showReminderNotification(reminder: Reminder) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "ReminderzChannel")
            .setContentTitle("Daily Active Reminder Notification")
            .setContentText(reminder.title+" - "+reminder.dueDate)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(reminder.id, notification)
    }

    private fun showDueReminderNotification(reminder: Reminder) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "ReminderzChannel")
            .setContentTitle("Due Reminder")
            .setContentText(reminder.title+" - "+reminder.dueDate)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(reminder.id, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?) = null
}
