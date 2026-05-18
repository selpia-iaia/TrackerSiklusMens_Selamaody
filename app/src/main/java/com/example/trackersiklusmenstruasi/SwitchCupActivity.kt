package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ActivitySwitchCupBinding

class SwitchCupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwitchCupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySwitchCupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupCupPicker()
    }

    private fun setupCupPicker() {
        val cups = listOf("200ml", "250ml", "300ml", "350ml", "400ml", "450ml", "500ml")
        val adapter = CupAdapter(cups) { selected ->
            // Optional: immediately update UI if needed
        }
        
        binding.rvCupPicker.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvCupPicker.adapter = adapter

        // Add SnapHelper to center the items
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCupPicker)
        
        // Initial position
        binding.rvCupPicker.scrollToPosition(2)

        // Use OK button to save and finish
        binding.btnOk.setOnClickListener {
            val selected = adapter.getSelectedItem()
            val resultIntent = android.content.Intent()
            resultIntent.putExtra("SELECTED_CUP", selected)
            setResult(android.app.Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
