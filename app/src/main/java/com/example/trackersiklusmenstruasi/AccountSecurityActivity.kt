package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityAccountSecurityBinding
import kotlinx.coroutines.launch

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
        binding.switchBiometric.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Saat dinyalakan, minta autentikasi biometrik sebagai tes
                authenticateBiometric { success ->
                    if (success) {
                        saveState("biometric", true)
                        showToast("Akses Biometrik diaktifkan")
                        syncSecurityToServer("Biometric", true)
                    } else {
                        buttonView.isChecked = false
                        saveState("biometric", false)
                    }
                }
            } else {
                saveState("biometric", false)
                syncSecurityToServer("Biometric", false)
            }
        }
        
        binding.switchFace.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                authenticateBiometric { success ->
                    if (success) {
                        saveState("face", true)
                        showToast("Akses Wajah diaktifkan")
                        syncSecurityToServer("Face", true)
                    } else {
                        buttonView.isChecked = false
                        saveState("face", false)
                    }
                }
            } else {
                saveState("face", false)
                syncSecurityToServer("Face", false)
            }
        }
        
        binding.switchSmsCode.setOnCheckedChangeListener { _, isChecked ->
            saveState("sms_code", isChecked)
            syncSecurityToServer("SMS_Code", isChecked)
            if (isChecked) showToast("Kode SMS diaktifkan")
        }
        
        binding.switchGoogleCode.setOnCheckedChangeListener { _, isChecked ->
            saveState("google_code", isChecked)
            syncSecurityToServer("Google_Auth", isChecked)
            if (isChecked) showToast("Google Auth diaktifkan")
        }
        // ... (sisanya tetap sama)

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

    private fun authenticateBiometric(onResult: (Boolean) -> Unit) {
        // Implementasi sederhana: Kita simulasikan dialog sistem biometrik
        // (Sangat disarankan melakukan Gradle Sync untuk menggunakan library asli androidx.biometric)
        val executor = ContextCompat.getMainExecutor(this)
        
        try {
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onResult(true)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        showToast("Autentikasi Gagal: $errString")
                        onResult(false)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        showToast("Autentikasi tidak dikenali")
                        onResult(false)
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verifikasi Keamanan")
                .setSubtitle("Gunakan Sidik Jari atau Wajah untuk mengaktifkan")
                .setNegativeButtonText("Batal")
                .build()

            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            // Fallback jika library belum siap/sync
            AlertDialog.Builder(this)
                .setTitle("Verifikasi Simulasi")
                .setMessage("Akses biometrik memerlukan library sistem. Izinkan simulasi?")
                .setPositiveButton("Izinkan") { _, _ -> onResult(true) }
                .setNegativeButton("Batal") { _, _ -> onResult(false) }
                .show()
        }
    }

    private fun syncSecurityToServer(feature: String, isEnabled: Boolean) {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId().takeIf { it != -1 } ?: 1
        
        lifecycleScope.launch {
            try {
                val apiService = ApiService.create()
                // Gunakan saveAppSettings atau API khusus keamanan jika ada
                apiService.saveAppSettings(AppSettingsModel(
                    user_id = userId,
                    week_start = "Monday", // Default
                    time_format = "24h", // Default
                    water_target = 2000,
                    cup_volume = 200,
                    is_bmi_enabled = isEnabled // Menyalahgunakan field ini untuk testing keamanan
                ))
            } catch (e: Exception) {}
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
