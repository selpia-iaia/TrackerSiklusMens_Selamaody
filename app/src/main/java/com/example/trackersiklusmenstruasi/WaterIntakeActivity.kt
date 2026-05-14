package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityWaterIntakeBinding

class WaterIntakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterIntakeBinding
    private var currentIntake = 1230
    private val targetIntake = 2400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDrink.setOnClickListener {
            currentIntake += 300
            updateUI()
        }

        binding.btnGlass.setOnClickListener {
            startActivity(Intent(this, SwitchCupActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SwitchCupActivity::class.java))
        }
        
        binding.btnOk.setOnClickListener {
            finish()
        }
    }

    private fun updateUI() {
        binding.tvCurrentIntake.text = currentIntake.toString()
        // Format dengan ribuan separator
        val targetText = String.format("%,d", targetIntake).replace(',', '.') + "ml"
        binding.tvTargetIntake.text = targetText
        
        val progress = currentIntake.toFloat() / targetIntake
        binding.waterGauge.setProgress(progress)
    }
}
