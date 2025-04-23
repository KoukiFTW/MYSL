package com.surendramaran.yolov8tflite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.surendramaran.yolov8tflite.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example: Click on an item (expand this later)
        binding.layoutGesture1.setOnClickListener {
            Toast.makeText(this, "Gesture 1 Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.layoutGesture2.setOnClickListener {
            Toast.makeText(this, "Gesture 2 Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
