package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityMoodSelectionBinding

class MoodSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnOk.setOnClickListener {
            finish()
        }

        // Just simple toast for now to show it's clickable
        val moods = listOf("Normal", "Ceria", "Senang", "Sedih", "Marah")
        binding.root.setOnClickListener {
            // Placeholder for wheel interaction
        }
    }
}
