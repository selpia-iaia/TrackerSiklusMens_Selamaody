package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityWeightEntryBinding

class WeightEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeightEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupBMIItems()

        binding.btnClose.setOnClickListener { finish() }

        binding.weightSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvWeightValue.text = "$progress"
                binding.arcRulerView.setValue(progress)
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.btnOk.setOnClickListener {
            val dbHelper = DatabaseHelper.getInstance(this)
            // Simpan ke DB
            finish()
        }
    }

    private fun setupUI() {
        binding.arcRulerView.setValue(57)
        binding.weightSeekBar.progress = 57
        binding.tvWeightValue.text = "57"
    }

    private fun setupBMIItems() {
        binding.bmi1.apply {
            tvLabel.text = "Berat Sangat Rendah"
            tvRange.text = "BMI < 11.0"
            vDot.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4285F4"))
        }
        binding.bmi2.apply {
            tvLabel.text = "Sangat Kurus"
            tvRange.text = "BMI 11.0-11.9"
            vDot.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4DB6AC"))
        }
        binding.bmi3.apply {
            tvLabel.text = "Berat Badan Rendah"
            tvRange.text = "BMI 12.0-13.4"
            vDot.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#43A047"))
        }
        binding.bmi4.apply {
            tvLabel.text = "Normal"
            tvRange.text = "BMI 20.5-26.9"
            vDot.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FBBC04"))
        }
        binding.bmi5.apply {
            tvLabel.text = "Berat Badan Tinggi"
            tvRange.text = "BMI 25.0-28.9"
            vDot.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EA4335"))
        }
        binding.bmi6.apply {
            tvLabel.text = "Obesitas Kelas III"
            tvRange.text = "BMI ≥ 45.0"
            vDot.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1A73E8"))
        }
    }
}
