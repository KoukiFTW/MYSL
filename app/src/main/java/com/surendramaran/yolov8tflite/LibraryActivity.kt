package com.surendramaran.yolov8tflite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.card.MaterialCardView
import com.surendramaran.yolov8tflite.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        // Adjust padding for status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            binding.tvLibraryTitle.updatePadding(top = statusBarHeight + 5)
            insets
        }

        setupGestureCards()
    }

    private fun setupGestureCards() {
        val gesturePairs = listOf(
            Pair(R.id.layoutGesture1, R.id.imageGesture1),
            Pair(R.id.layoutGesture2, R.id.imageGesture2),
            Pair(R.id.layoutGesture3, R.id.imageGesture3),
        )

        gesturePairs.forEach { (cardId, imageId) ->
            findViewById<MaterialCardView>(cardId).setOnClickListener {
                val card = findViewById<MaterialCardView>(cardId)
                val imageView = findViewById<ImageView>(imageId)

                TransitionManager.beginDelayedTransition(card as ViewGroup)
                if (imageView.visibility == View.VISIBLE) {
                    imageView.visibility = View.GONE
                    card.setContentPadding(0, 0, 0, 0)
                } else {
                    imageView.visibility = View.VISIBLE
                    // Adjust padding if needed for your specific layout
                    card.setContentPadding(16, 16, 16, 16)
                }
            }
        }
    }

    fun openLearnMoreLink(view: View?) {
        val uri = Uri.parse("https://fliphtml5.com/gutjj/jkuj/Bahasa_Isyarat_Komunikasi_%28Ketidakupayaan_Pendengaran%29_Tahun_1/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}
