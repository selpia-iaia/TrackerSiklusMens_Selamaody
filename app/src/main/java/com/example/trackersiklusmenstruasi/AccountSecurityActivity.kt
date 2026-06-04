package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityAccountSecurityBinding

class AccountSecurityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSecurityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.ivNext1.setOnClickListener { showToast("Kelola Perangkat") }
        binding.ivNext2.setOnClickListener { showToast("Nonaktifkan Akun") }
        binding.ivNext3.setOnClickListener { showToast("Hapus Akun") }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
