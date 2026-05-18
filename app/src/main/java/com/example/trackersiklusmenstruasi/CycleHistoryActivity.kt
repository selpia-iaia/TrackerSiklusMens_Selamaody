package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackersiklusmenstruasi.databinding.ActivityCycleHistoryBinding

class CycleHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCycleHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCycleHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupHistoryList()
        loadSummaryData()
    }

    private fun setupHistoryList() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val periods = dbHelper.getAllPeriods()
        
        binding.rvCycleHistory.layoutManager = LinearLayoutManager(this)
        binding.rvCycleHistory.adapter = CycleHistoryAdapter(periods)
    }

    private fun loadSummaryData() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val profile = dbHelper.getUserProfile()
        profile?.let {
            binding.tvAvgPeriod.text = "${it.periodLength} Hari"
            binding.tvAvgCycle.text = "${it.cycleLength} Hari"
        }
        
        // Match UI from image: if profile is null, show defaults
        if (profile == null) {
            binding.tvAvgPeriod.text = "3 Hari"
            binding.tvAvgCycle.text = "26 Hari"
        }
    }
}
