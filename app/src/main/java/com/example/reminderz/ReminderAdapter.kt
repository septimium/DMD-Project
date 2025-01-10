package com.example.reminderz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderz.databinding.ItemReminderBinding

class ReminderAdapter(
    private var reminders: List<Reminder>,
    private val onMarkAsCompleted: (Reminder, Boolean) -> Unit,
    private val onDeleteReminder: (Reminder) -> Unit,
    private val onShareReminder: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.binding.textViewTitle.text = reminder.title
        holder.binding.textViewDescription.text = reminder.description
        holder.binding.textViewDueDate.text = reminder.dueDate
        holder.binding.checkBoxComplete.setOnCheckedChangeListener(null)
        holder.binding.checkBoxComplete.isChecked = reminder.isCompleted
        holder.binding.checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            onMarkAsCompleted(reminder, isChecked)
        }
        holder.binding.buttonDelete.setOnClickListener {
            onDeleteReminder(reminder)
        }
        holder.binding.buttonShare.setOnClickListener {
            onShareReminder(reminder)
        }
    }

    override fun getItemCount() = reminders.size

    fun updateList(newList: List<Reminder>) {
        reminders = newList
        notifyDataSetChanged()
    }

    inner class ReminderViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)
}


