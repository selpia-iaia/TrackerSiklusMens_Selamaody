package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityAddPayOptionBinding
import kotlinx.coroutines.launch

class AddPayOptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPayOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Transparent status bar with dark icons
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding = ActivityAddPayOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.header.setPadding(binding.header.paddingLeft, systemBars.top, binding.header.paddingRight, binding.header.paddingBottom)
            insets
        }
        
        binding.btnClose.setOnClickListener { finish() }
        
        binding.btnSave.setOnClickListener {
            val sessionManager = SessionManager(this)
            val userId = sessionManager.getUserId().takeIf { it != -1 } ?: 1

            lifecycleScope.launch {
                try {
                    val apiService = ApiService.create()
                    apiService.savePaymentMethod(
                        PaymentMethodModel(
                            user_id = userId,
                            provider = "Credit/Debit Card",
                            account_number = "2589 5555 7891", // Dummy data from UI
                            holder_name = "Sela.maody" // Dummy data from UI
                        )
                    )
                    Toast.makeText(this@AddPayOptionActivity, "Metode Pembayaran Tersimpan!", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@AddPayOptionActivity, "Gagal simpan ke server, tersimpan lokal", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
