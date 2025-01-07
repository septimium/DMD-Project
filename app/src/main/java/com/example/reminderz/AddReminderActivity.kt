package com.example.reminderz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
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

    private var selectedDate: String = "" // Will hold the final date and time

    // Get the Room database instance
    private val reminderDatabase by lazy { ReminderDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        supportActionBar?.apply {
            // Set a custom title
            title = "Add a new reminder "
        }

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        textViewDate = findViewById(R.id.textViewDate)
        buttonSaveReminder = findViewById(R.id.buttonSaveReminder)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)

        // Handle Date selection
        buttonSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog
            val datePickerDialog = DatePickerDialog(this,
                { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    // Format date to string (yyyy-MM-dd)
                    selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    textViewDate.text = "Selected Date: $selectedDate"
                }, year, month, day)

            datePickerDialog.show()
        }

        // Handle Time selection
        buttonSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // TimePickerDialog
            val timePickerDialog = TimePickerDialog(this,
                { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    // Format time to string (HH:mm)
                    val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                    selectedDate = "$selectedDate $time" // Append the selected time to the selected date
                    textViewDate.text = "Selected Date and Time: $selectedDate"
                }, hour, minute, true)

            timePickerDialog.show()
        }

        // Handle Save Button
        buttonSaveReminder.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create new reminder object
            val reminder = Reminder(
                title = title,
                description = description,
                dueDate = selectedDate // Pass the selected date and time
            )

            // Save reminder to Room database
            saveReminder(reminder)

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Close AddReminderActivity

        }
    }

    // Function to save reminder to the database
    private fun saveReminder(reminder: Reminder) {
        lifecycleScope.launch {
            reminderDatabase.reminderDao().insert(reminder)
        }
    }
}

