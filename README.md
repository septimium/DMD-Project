# Reminderz App

Reminderz is a simple Android app designed to help users manage their reminders and tasks efficiently. The app allows users to create, view, edit, and delete reminders, as well as receive notifications about due and daily reminders. With features like dark mode, daily reminder notifications, and an intuitive UI, Reminderz makes it easy for users to stay on top of their tasks.

## Features

- **Manage Reminders**: Users can add new reminders, mark them as completed, and delete them.
- **Daily Reminders**: Notifications are sent at a specified time each day to remind users about their tasks for the day.
- **Due Reminders**: The app sends notifications for reminders that are due or overdue.
- **Foreground Service**: Keeps track of reminders in the background and ensures notifications are sent.
- **Background Service**: Deletes reminders older than 30 days to keep the app clean and organized.
- **Dark Mode**: The app supports dark mode, allowing users to switch between light and dark themes based on their preferences.
- **Boot Completion Handling**: The app starts services after the device boots up, ensuring ongoing reminder management.

## Components Used

### 1. **Activities**
   - **HomeActivity**: The main screen that displays a list of active reminders, allows users to mark reminders as completed, and delete them. It also provides a floating action button (FAB) to add new reminders.
   - **CompletedActivity**: Displays a list of completed reminders. Users can restore or delete these reminders.
   - **AddReminderActivity**: A form for adding or editing reminders, including fields for title, description, due date, and repeat settings.
   - **SettingsActivity**: Manages user preferences, such as enabling dark mode and setting a time for daily notifications.
   - **BaseActivity**: Manages global settings, such as dark mode, for all activities.

### 2. **Services**
   - **ReminderForegroundService**: A foreground service that monitors reminders and ensures notifications are displayed for due and daily reminders. This service runs in the background and is tied to the app's UI through ongoing notifications.
   - **BackgroundService**: A background service that deletes reminders older than 30 days, keeping the reminders list clean.

### 3. **Broadcast Receiver**
   - **BackgroundReceiver**: A receiver that listens for `BOOT_COMPLETED` events. When the device boots up, this receiver starts the `BackgroundService` to ensure reminders are properly managed even after a reboot.

### 4. **Room Database**
   - **ReminderDatabase**: A Room database that stores reminders persistently. It includes methods to fetch active and completed reminders, as well as delete reminders.
   - **ReminderDao**: A Data Access Object (DAO) that provides methods for interacting with the database, including adding, updating, and deleting reminders.

### 5. **Notification Manager**
   - **NotificationCompat**: Used to create and display notifications for due and daily reminders. The app sends high-priority notifications to the user based on the reminder’s due date.

### 6. **Adapter**
   - **ReminderAdapter**: A RecyclerView adapter that binds reminder data to a list in the `HomeActivity` and `CompletedActivity`. It provides functionality to mark reminders as completed, delete them, and share them.

### 7. **SharedPreferences**
   - **SharedPreferences**: Stores user settings such as dark mode preferences and notification time for daily reminders. It ensures that these settings are preserved across app launches.

### 8. **Coroutines**
   - **CoroutineScope**: Used in services to perform background tasks (like deleting old reminders or monitoring reminders) without blocking the main thread. Coroutines are used for performing tasks asynchronously, such as checking for due reminders at regular intervals.

---

### Installation

To run the app locally, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/reminderz.git
