package com.example.reminderz

import android.app.ActivityOptions
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var darkModeSwitch: Switch
    private lateinit var dailyNotificationSwitch: Switch
    private lateinit var notificationTimeText: TextView
    private lateinit var selectTimeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)

        // Initialize views
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        dailyNotificationSwitch = findViewById(R.id.dailyNotificationSwitch)
        notificationTimeText = findViewById(R.id.notificationTimeText)
        selectTimeButton = findViewById(R.id.selectTimeButton)

        // Load saved preferences
        setupDarkModeSwitch()
        setupNotificationSettings()

        // Setup Bottom Navigation
        setupBottomNavigation()
    }

    private fun setupDarkModeSwitch() {
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkModeEnabled

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            recreate()
        }
    }

    private fun setupNotificationSettings() {
        // Load saved notification preferences
        val isDailyNotificationEnabled = sharedPreferences.getBoolean("daily_notification_enabled", false)
        val notificationTime = sharedPreferences.getString("notification_time", "09:00")

        dailyNotificationSwitch.isChecked = isDailyNotificationEnabled
        notificationTimeText.text = "Notification Time: $notificationTime"
        notificationTimeText.isVisible = isDailyNotificationEnabled
        selectTimeButton.isEnabled = isDailyNotificationEnabled

        // Handle notification switch changes
        dailyNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("daily_notification_enabled", isChecked).apply()
            selectTimeButton.isEnabled = isChecked
            notificationTimeText.isVisible = isChecked

            // Restart the foreground service to apply new settings
            val serviceIntent = Intent(this, ReminderForegroundService::class.java)
            if (isChecked) {
                startService(serviceIntent)
            } else {
                stopService(serviceIntent)
                startService(serviceIntent)
            }
        }

        // Handle time selection with button
        selectTimeButton.setOnClickListener {
            if (dailyNotificationSwitch.isChecked) {
                showTimePickerDialog()
            }
        }
    }

    private fun showTimePickerDialog() {
        val currentTime = sharedPreferences.getString("notification_time", "09:00")
        val hour = currentTime?.split(":")?.get(0)?.toInt() ?: 9
        val minute = currentTime?.split(":")?.get(1)?.toInt() ?: 0

        TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                sharedPreferences.edit().putString("notification_time", timeString).apply()
                notificationTimeText.text = "Selected Notification Time: $timeString"

                // Restart the service to apply new time
                val serviceIntent = Intent(this, ReminderForegroundService::class.java)
                stopService(serviceIntent)
                startService(serviceIntent)
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_settings

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivityWithNoAnimation(intent)
                    finish()
                    true
                }
                R.id.nav_completed -> {
                    val intent = Intent(this, CompletedActivity::class.java)
                    startActivityWithNoAnimation(intent)
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }

    private fun startActivityWithNoAnimation(intent: Intent) {
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
        startActivity(intent, options.toBundle())
    }
}