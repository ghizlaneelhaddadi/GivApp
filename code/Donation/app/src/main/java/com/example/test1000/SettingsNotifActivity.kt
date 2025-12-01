package com.example.test1000

import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsNotifActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_notif)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val pushSwitch = findViewById<Switch>(R.id.switch_push_notifications)
        val messageSwitch = findViewById<Switch>(R.id.switch_message_notifications)
        val reminderSwitch = findViewById<Switch>(R.id.switch_reminder_notifications)
        val soundSwitch = findViewById<Switch>(R.id.switch_sound_notifications)
        val vibrateSwitch = findViewById<Switch>(R.id.switch_vibrate_notifications)

        val sharedPreferences = getSharedPreferences("notification_prefs", MODE_PRIVATE)

        pushSwitch.isChecked = sharedPreferences.getBoolean("push_notifications", false)
        messageSwitch.isChecked = sharedPreferences.getBoolean("message_notifications", false)
        reminderSwitch.isChecked = sharedPreferences.getBoolean("reminder_notifications", false)
        soundSwitch.isChecked = sharedPreferences.getBoolean("sound_notifications", false)
        vibrateSwitch.isChecked = sharedPreferences.getBoolean("vibrate_notifications", false)

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("push_notifications", isChecked).apply()
        }

        messageSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("message_notifications", isChecked).apply()
        }

        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("reminder_notifications", isChecked).apply()
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("sound_notifications", isChecked).apply()
        }

        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("vibrate_notifications", isChecked).apply()
        }
    }
}
