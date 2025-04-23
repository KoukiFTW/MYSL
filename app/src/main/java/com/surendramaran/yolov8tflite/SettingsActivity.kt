package com.surendramaran.yolov8tflite

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.surendramaran.yolov8tflite.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display Settings Click
        binding.layoutDisplay.setOnClickListener {
            Toast.makeText(this, "Display Settings Clicked", Toast.LENGTH_SHORT).show()
        }

        // Notifications Click
        binding.layoutNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show()
        }

        // Dark Mode Toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Dark Mode Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Account Settings Click
        binding.layoutAboutApp.setOnClickListener {
            Toast.makeText(this, "Version 0.23", Toast.LENGTH_SHORT).show()
        }
    }
}

