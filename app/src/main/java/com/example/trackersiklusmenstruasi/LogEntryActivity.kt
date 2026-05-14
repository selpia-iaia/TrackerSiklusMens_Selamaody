package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityLogEntryBinding
import java.text.SimpleDateFormat
import java.util.*

class LogEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogEntryBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dbHelper = DatabaseHelper.getInstance(this)
        dbHelper.writableDatabase

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.sectionSymptoms.setOnClickListener {
            Toast.makeText(this, "Gejala terpilih: Kepala Sakit", Toast.LENGTH_SHORT).show()
        }

        binding.sectionMoods.setOnClickListener {
            startActivity(Intent(this, MoodSelectionActivity::class.java))
        }

        binding.sectionMeds.setOnClickListener {
            Toast.makeText(this, "Obat terpilih: Pill", Toast.LENGTH_SHORT).show()
        }

        binding.itemAddWeight.setOnClickListener {
            startActivity(Intent(this, WeightEntryActivity::class.java))
        }

        binding.sectionWeightEntry.setOnClickListener {
            startActivity(Intent(this, WeightEntryActivity::class.java))
        }

        binding.sectionHeightEntry.setOnClickListener {
            // Reusing weight or you can create HeightEntryActivity later
            startActivity(Intent(this, WeightEntryActivity::class.java))
        }

        binding.sectionTempEntry.setOnClickListener {
            startActivity(Intent(this, TempEntryActivity::class.java))
        }

        setupFlowSelection()

        binding.btnSave.setOnClickListener {
            saveLog()
        }
    }

    private var selectedFlow = "Normal"

    private fun setupFlowSelection() {
        binding.flowLow.setOnClickListener { selectFlow("Rendah") }
        binding.flowNormal.setOnClickListener { selectFlow("Normal") }
        binding.flowHigh.setOnClickListener { selectFlow("Tinggi") }
    }

    private fun selectFlow(flow: String) {
        selectedFlow = flow
        Toast.makeText(this, "Aliran: $flow terpilih", Toast.LENGTH_SHORT).show()
        
        binding.flowLow.alpha = if (flow == "Rendah") 1.0f else 0.5f
        binding.flowNormal.alpha = if (flow == "Normal") 1.0f else 0.5f
        binding.flowHigh.alpha = if (flow == "Tinggi") 1.0f else 0.5f
    }

    private fun saveLog() {
        // Simple logic for now, using current date
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        
        val note = binding.etNote.text.toString()
        
        val result = dbHelper.saveDailyLog(
            currentDate,
            "Normal",
            "Kepala Sakit",
            "Senang",
            "Pil Obat",
            note
        )

        if (result != -1L) {
            Toast.makeText(this, "Log Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Gagal menyimpan log", Toast.LENGTH_SHORT).show()
        }
    }
}
