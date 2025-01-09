package com.example.reminderz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BackgroundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Start the BackgroundService when the alarm triggers
        val serviceIntent = Intent(context, BackgroundService::class.java)
        context.startService(serviceIntent)
        Log.d("BACKGROUND RECEIVER", "a pornit asta da celalalt u prea")
        // Optionally, show a toast for debugging purposes
        Toast.makeText(context, "Deleting old reminders...", Toast.LENGTH_SHORT).show()
    }
}
