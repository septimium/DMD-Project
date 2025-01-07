package com.example.reminderz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // auto-generate ID
    val title: String,
    val description: String,
    val dueDate: String, // You can format it as needed
    var isCompleted: Boolean = false // Default to false
)
