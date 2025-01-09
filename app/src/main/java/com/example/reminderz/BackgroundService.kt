package com.example.reminderz

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BackgroundService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startDeletingOldReminders()
    }

    private fun startDeletingOldReminders() {
        scope.launch {
            deleteOldReminders()
            stopSelf() // Stop the service after performing the task
        }
    }

    private suspend fun deleteOldReminders() {
        val reminderDao = ReminderDatabase.getDatabase(applicationContext).reminderDao()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date30DaysAgo = calendar.time
        val activeReminders = reminderDao.getActiveReminders()
        val completedReminders = reminderDao.getCompletedReminders()
        activeReminders.forEach { reminder ->
            val reminderDate = sdf.parse(reminder.dueDate)
            // Compare dates
            if (reminderDate != null && reminderDate.before(date30DaysAgo)) {
                Log.d("BackgroundService", "DELETING REMINDER OLDER THAN 30 DAYS: ${reminder.title}")
                reminderDao.delete(reminder)
            }
        }
        completedReminders.forEach { reminder ->
            val reminderDate = sdf.parse(reminder.dueDate)
            // Compare dates
            if (reminderDate != null && reminderDate.before(date30DaysAgo)) {
                Log.d("BackgroundService", "DELETING REMINDER OLDER THAN 30 DAYS: ${reminder.title}")
                reminderDao.delete(reminder)
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
