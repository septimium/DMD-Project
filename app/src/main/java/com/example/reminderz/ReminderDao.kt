package com.example.reminderz

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReminderDao {

    @Insert
    suspend fun insert(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE isCompleted = 0")
    suspend fun getActiveReminders(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE isCompleted = 1")
    suspend fun getCompletedReminders(): List<Reminder>
}