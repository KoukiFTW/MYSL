package com.surendramaran.yolov8tflite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.surendramaran.yolov8tflite.databinding.ActivityLibraryBinding


class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Adjust padding for status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            binding.tvLibraryTitle.updatePadding(top = statusBarHeight + 5) // existing 5dp + status bar
            insets
        }

        // Example: Click on an item (expand this later)
        binding.layoutGesture1.setOnClickListener {
            Toast.makeText(this, "Gesture 1 Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.layoutGesture2.setOnClickListener {
            Toast.makeText(this, "Gesture 2 Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun openLearnMoreLink(view: View?) {
        val uri =
            Uri.parse("https://fliphtml5.com/gutjj/jkuj/Bahasa_Isyarat_Komunikasi_%28Ketidakupayaan_Pendengaran%29_Tahun_1/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
