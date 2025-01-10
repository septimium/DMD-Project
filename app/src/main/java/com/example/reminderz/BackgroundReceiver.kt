package com.example.reminderz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BackgroundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, BackgroundService::class.java)
        context.startService(serviceIntent)
    }
}
