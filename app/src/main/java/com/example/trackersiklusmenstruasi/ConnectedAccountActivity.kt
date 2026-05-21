package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityConnectedAccountBinding

class ConnectedAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectedAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectedAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnConnectGoogle.setOnClickListener { showToast("Menghubungkan ke Google...") }
        binding.btnConnectFacebook.setOnClickListener { showToast("Menghubungkan ke Facebook...") }
        binding.btnConnectTwitter.setOnClickListener { showToast("Menghubungkan ke Twitter...") }
        binding.btnConnectApple.setOnClickListener { showToast("Menghubungkan ke Apple ID...") }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
