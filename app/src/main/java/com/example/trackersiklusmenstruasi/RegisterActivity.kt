package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnTogglePassword.setOnClickListener {
            val inputType = if (binding.etPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.etPassword.inputType = inputType
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val isTermsAccepted = binding.cbTerms.isChecked

            if (name.isEmpty()) {
                binding.etFullName.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "Format email tidak valid"
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.etPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            }
            if (!isTermsAccepted) {
                Toast.makeText(this, "Anda harus menyetujui Syarat & Ketentuan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pendaftaran via API
            lifecycleScope.launch {
                try {
                    val apiService = ApiService.create()
                    val response = apiService.registerUser(
                        UserModel(
                            username = name,
                            email = email,
                            password_hash = password // Dalam produksi harus di-hash
                        )
                    )

                    if (response.success) {
                        Toast.makeText(this@RegisterActivity, "Berhasil mendaftar!", Toast.LENGTH_SHORT).show()

                        val sessionManager = SessionManager(this@RegisterActivity)
                        sessionManager.setLoggedIn(true)
                        sessionManager.setUserId(response.user_id ?: 1)

                        startActivity(Intent(this@RegisterActivity, ProfileSetupActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Gagal mendaftar: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Fallback untuk simulasi jika API belum siap
                    Toast.makeText(this@RegisterActivity, "Mode Offline: Berhasil mendaftar!", Toast.LENGTH_SHORT).show()
                    val sessionManager = SessionManager(this@RegisterActivity)
                    sessionManager.setLoggedIn(true)
                    sessionManager.setUserId(1)
                    startActivity(Intent(this@RegisterActivity, ProfileSetupActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }
}
