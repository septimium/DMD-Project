package com.example.reminderz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class AddReminderActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var textViewDate: TextView
    private lateinit var buttonSaveReminder: Button
    private lateinit var buttonSelectDate: Button
    private lateinit var buttonSelectTime: Button

    private var selectedDate: String = "" // Holds the selected date and time
    private var selectedTime: String = ""
    private var selectedShortDate: String = ""

    // Room database instance
    private val reminderDatabase by lazy { ReminderDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        textViewDate = findViewById(R.id.textViewDate)
        buttonSaveReminder = findViewById(R.id.buttonSaveReminder)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)

        // Handle Date selection
        buttonSelectDate.setOnClickListener { selectDate() }

        // Handle Time selection
        buttonSelectTime.setOnClickListener { selectTime() }

        // Handle Save button
        buttonSaveReminder.setOnClickListener { saveReminder() }
    }

    private fun selectDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            selectedShortDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            textViewDate.text = "Selected Date: $selectedDate"
        }, year, month, day).show()
    }

    private fun selectTime() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            selectedTime = time
            selectedDate = "$selectedDate $time"
            textViewDate.text = "Selected Date and Time: $selectedDate"
        }, hour, minute, true).show()
    }

    private fun saveReminder() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        // Input validation
        if (title.isEmpty()) {
            editTextTitle.error = "Title is required"
            return
        }

        if (description.isEmpty()) {
            editTextDescription.error = "Description is required"
            return
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a time and a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedShortDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val reminder = Reminder(title = title, description = description, dueDate = selectedDate)

        // Save to Room database
        lifecycleScope.launch {
            reminderDatabase.reminderDao().insert(reminder)
            Toast.makeText(this@AddReminderActivity, "Reminder added successfully!", Toast.LENGTH_SHORT).show()

            // Navigate back to HomeActivity
            startActivity(Intent(this@AddReminderActivity, HomeActivity::class.java))
            finish()
        }
    }
}
