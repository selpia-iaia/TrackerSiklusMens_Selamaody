package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityWaterIntakeBinding
import java.text.SimpleDateFormat
import java.util.*

class WaterIntakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterIntakeBinding
    private var currentIntake = 0
    private val targetIntake = 2400
    private var selectedCupValue = 300

    private val startCupPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selected = result.data?.getStringExtra("SELECTED_CUP") ?: "300ml"
            selectedCupValue = selected.replace("ml", "").toInt()
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCurrentData()
        updateUI()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDrink.setOnClickListener {
            currentIntake += selectedCupValue
            updateUI()
        }

        binding.btnGlass.setOnClickListener {
            startCupPicker.launch(Intent(this, SwitchCupActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startCupPicker.launch(Intent(this, SwitchCupActivity::class.java))
        }
        
        binding.btnOk.setOnClickListener {
            saveToDatabase()
            finish()
        }
    }

    private fun loadCurrentData() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        currentIntake = dbHelper.getWaterIntakeForDate(today)
    }

    private fun saveToDatabase() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dbHelper.updateWaterIntake(today, currentIntake)
    }

    private fun updateUI() {
        binding.tvCurrentIntake.text = currentIntake.toString()
        
        // Match image 41: Minum(300ml)
        binding.btnDrink.text = "Minum(${selectedCupValue}ml)"

        // Format target with comma separator as per image (e.g., 2,400ml)
        val targetText = String.format("%,d", targetIntake) + "ml"
        binding.tvTargetIntake.text = targetText
        
        val progress = currentIntake.toFloat() / targetIntake
        binding.waterGauge.setProgress(progress)
    }
}
