package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        enableEdgeToEdge()
        binding = ActivityWaterIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        // Ensure color is pink_main for current intake as per image 38
        binding.tvCurrentIntake.text = currentIntake.toString()
        binding.tvCurrentIntake.setTextColor(getColor(R.color.pink_main))
        
        binding.btnDrink.text = "Minum(${selectedCupValue}ml)"

        // Format target with comma separator as per image (e.g., 2,400ml)
        val targetText = String.format(Locale.US, "%,d", targetIntake) + "ml"
        binding.tvTargetIntake.text = targetText
        
        val progress = currentIntake.toFloat() / targetIntake
        binding.waterGauge.setProgress(progress)
    }
}
