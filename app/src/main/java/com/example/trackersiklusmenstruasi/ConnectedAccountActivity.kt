package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityConnectedAccountBinding
import kotlinx.coroutines.launch

class ConnectedAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectedAccountBinding
    private lateinit var syncRepository: SyncRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Ensure status bar is transparent and icons are dark
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding = ActivityConnectedAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.header.setPadding(binding.header.paddingLeft, systemBars.top, binding.header.paddingRight, binding.header.paddingBottom)
            insets
        }
        
        syncRepository = SyncRepository(ApiService.create())
        sessionManager = SessionManager(this)

        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnConnectGoogle.setOnClickListener { simulateConnection("Google") }
    }

    private fun simulateConnection(provider: String) {
        if (provider == "Google") {
            try {
                // Try to open Google account sync settings
                val intent = android.content.Intent("android.settings.GOOGLE_ACCOUNT_SETTINGS")
                startActivity(intent)
                showToast("Membuka Pengaturan Google...")
            } catch (e: Exception) {
                showSimpleConnectionDialog(provider)
            }
        } else {
            showSimpleConnectionDialog(provider)
        }
    }

    private fun showSimpleConnectionDialog(provider: String) {
        val dbHelper = DatabaseHelper.getInstance(this)
        val profile = dbHelper.getUserProfile()
        val email = profile?.name?.lowercase()?.replace(" ", "") + "@gmail.com"

        AlertDialog.Builder(this)
            .setTitle("Hubungkan ke $provider")
            .setMessage("Apakah Anda ingin menghubungkan akun ini dengan email $email?")
            .setPositiveButton("Hubungkan") { _, _ ->
                saveAccountToServer(provider, email)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveAccountToServer(provider: String, email: String) {
        var userId = sessionManager.getUserId()
        if (userId <= 0) userId = 1

        lifecycleScope.launch {
            try {
                val success = syncRepository.syncConnectedAccount(ConnectedAccountModel(
                    userId, provider, email
                ))
                if (success) {
                    showToast("Akun $provider Berhasil Terhubung!")
                } else {
                    showToast("Gagal menghubungkan akun")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
