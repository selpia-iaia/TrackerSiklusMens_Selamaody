package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivityAccountSecurityBinding

class AccountSecurityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSecurityBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ensure status bar is transparent and icons are dark
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding = ActivityAccountSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.header.setPadding(binding.header.paddingLeft, systemBars.top, binding.header.paddingRight, binding.header.paddingBottom)
            insets
        }
        
        loadSwitchStates()
        setupClickListeners()
    }

    private fun loadSwitchStates() {
        val prefs = getSharedPreferences("security_prefs", MODE_PRIVATE)
        binding.switchBiometric.isChecked = prefs.getBoolean("biometric", false)
        binding.switchFace.isChecked = prefs.getBoolean("face", false)
        binding.switchSmsCode.isChecked = prefs.getBoolean("sms_code", false)
        binding.switchGoogleCode.isChecked = prefs.getBoolean("google_code", false)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        // Switch listeners
        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            saveState("biometric", isChecked)
            if (isChecked) showToast("Akses Biometrik diaktifkan")
        }
        
        binding.switchFace.setOnCheckedChangeListener { _, isChecked ->
            saveState("face", isChecked)
            if (isChecked) showToast("Akses Wajah diaktifkan")
        }
        
        binding.switchSmsCode.setOnCheckedChangeListener { _, isChecked ->
            saveState("sms_code", isChecked)
            if (isChecked) showToast("Kode SMS diaktifkan")
        }
        
        binding.switchGoogleCode.setOnCheckedChangeListener { _, isChecked ->
            saveState("google_code", isChecked)
            if (isChecked) showToast("Google Auth diaktifkan")
        }

        binding.btnEditPassword.setOnClickListener {
            // Navigate to Forgot Password as a way to "edit" or reset
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnManageDevices.setOnClickListener {
            showToast("Fitur Kelola Perangkat segera hadir!")
        }

        binding.btnDeactivate.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Nonaktifkan Akun")
                .setMessage("Apakah Anda yakin ingin menonaktifkan akun sementara?")
                .setPositiveButton("Ya") { _, _ -> 
                    showToast("Akun Anda telah dinonaktifkan.")
                    // Simulate logout
                    val sessionManager = SessionManager(this)
                    sessionManager.setLoggedIn(false)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Tindakan ini permanen. Semua data siklus dan profil Anda akan dihapus. Lanjutkan?")
                .setPositiveButton("Hapus") { _, _ -> 
                    showToast("Permintaan penghapusan akun dikirim.")
                    // Logic to clear database could go here
                    val sessionManager = SessionManager(this)
                    sessionManager.setLoggedIn(false)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun saveState(key: String, value: Boolean) {
        val prefs = getSharedPreferences("security_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
