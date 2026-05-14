package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityWaterIntakeBinding
import java.util.Locale

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
            // Biasanya pengaturan, tapi di sini diarahkan ke ganti gelas sesuai logika sebelumnya
            startActivity(Intent(this, SwitchCupActivity::class.java))
        }
        
        binding.btnOk.setOnClickListener {
            finish()
        }
    }

    private fun updateUI() {
        // Format dengan ribuan separator titik
        binding.tvCurrentIntake.text = String.format(Locale.GERMANY, "%,d", currentIntake)
        
        val targetText = String.format(Locale.GERMANY, "%,d", targetIntake) + "ml"
        binding.tvTargetIntake.text = targetText
        
        val progress = currentIntake.toFloat() / targetIntake
        binding.waterGauge.setProgress(progress)
    }
}
