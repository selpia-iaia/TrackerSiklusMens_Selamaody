package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityReminderAlertBinding

class ReminderAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderAlertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.btnBack.setOnClickListener { finish() }
        
        // Menambahkan listener untuk elemen yang bisa diinteraksi
        setupListeners()
    }
    
    private fun setupListeners() {
        // Karena layout aslinya belum punya banyak ID, kita hanya tambahkan back button.
        // Tapi kita bisa menunjukkan toast saat switch ditekan jika kita beri ID nanti.
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
