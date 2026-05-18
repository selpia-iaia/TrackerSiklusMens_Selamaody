package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityWaterIntakeBinding
import java.text.SimpleDateFormat
import java.util.*
=======
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityWaterIntakeBinding
import java.util.Locale
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba

class WaterIntakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterIntakeBinding
<<<<<<< HEAD
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
=======
    private var currentIntake = 1230
    private val targetIntake = 2400
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterIntakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        loadCurrentData()
=======
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
        updateUI()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDrink.setOnClickListener {
<<<<<<< HEAD
            currentIntake += selectedCupValue
=======
            currentIntake += 300
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
            updateUI()
        }

        binding.btnGlass.setOnClickListener {
<<<<<<< HEAD
            startCupPicker.launch(Intent(this, SwitchCupActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startCupPicker.launch(Intent(this, SwitchCupActivity::class.java))
        }
        
        binding.btnOk.setOnClickListener {
            saveToDatabase()
=======
            startActivity(Intent(this, SwitchCupActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            // Biasanya pengaturan, tapi di sini diarahkan ke ganti gelas sesuai logika sebelumnya
            startActivity(Intent(this, SwitchCupActivity::class.java))
        }
        
        binding.btnOk.setOnClickListener {
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
            finish()
        }
    }

<<<<<<< HEAD
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
=======
    private fun updateUI() {
        // Format dengan ribuan separator titik
        binding.tvCurrentIntake.text = String.format(Locale.GERMANY, "%,d", currentIntake)
        
        val targetText = String.format(Locale.GERMANY, "%,d", targetIntake) + "ml"
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
        binding.tvTargetIntake.text = targetText
        
        val progress = currentIntake.toFloat() / targetIntake
        binding.waterGauge.setProgress(progress)
    }
}
