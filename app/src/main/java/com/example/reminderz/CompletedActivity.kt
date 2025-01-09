package com.example.reminderz

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class CompletedActivity : BaseActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private val reminderDatabase by lazy { ReminderDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_completed
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivityWithNoAnimation(intent)
                    finish()
                }
                R.id.nav_completed -> return@setOnItemSelectedListener true
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivityWithNoAnimation(intent)
                    finish()
                }
            }
            true
        }
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewReminders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchReminders()

        reminderAdapter = ReminderAdapter(
            reminders = emptyList(),
            onMarkAsCompleted = { reminder, isChecked ->  // This accepts both reminder and isChecked
                markAsUnCompleted(reminder, isChecked)       // Pass both reminder and isChecked
            },
            onDeleteReminder = { reminder ->
                deleteReminder(reminder)
            },
            onShareReminder = { reminder ->
                shareReminder(reminder)
            }
        )

        recyclerView.adapter = reminderAdapter

        // Fetch and display active reminders from Room database
        fetchReminders()

    }
    private fun fetchReminders() {
        lifecycleScope.launch {
            val completedReminders = reminderDatabase.reminderDao().getCompletedReminders()
            reminderAdapter.updateList(completedReminders)
        }
    }

    private fun markAsUnCompleted(reminder: Reminder, isChecked: Boolean) {
        // Mark the reminder as completed in the database
        lifecycleScope.launch {
            reminder.isCompleted = isChecked
            reminderDatabase.reminderDao().update(reminder)
            fetchReminders()  // Refresh the list after marking as completed
        }
    }

    private fun deleteReminder(reminder: Reminder) {
        // Show confirmation dialog and delete the reminder
        android.app.AlertDialog.Builder(this)
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    reminderDatabase.reminderDao().delete(reminder)
                    fetchReminders()  // Refresh the list after deletion
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun shareReminder(reminder: Reminder) {
        // Create a sharing intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Reminder Title: ${reminder.title}\nDescription: ${reminder.description}\nDue Date: ${reminder.dueDate}\nCompleted: ${reminder.isCompleted}")
            type = "text/plain"
        }

        val chooser = Intent.createChooser(shareIntent, "Share Reminder")
        startActivity(chooser)
    }

    private fun startActivityWithNoAnimation(intent: Intent) {
        val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
        startActivity(intent, options.toBundle())
    }
}
