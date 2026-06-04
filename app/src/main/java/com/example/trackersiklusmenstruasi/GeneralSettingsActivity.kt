package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityGeneralSettingsBinding

class GeneralSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
