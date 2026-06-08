package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            // Membuka halaman Lupa Kata Sandi
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnLogin.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.setLoggedIn(true)
            sessionManager.setUserId(1) // Default user ID untuk simulasi
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Tambahkan tombol Google & Apple di Login juga
        setupSocialLogin()
    }

    private fun setupSocialLogin() {
        binding.btnGoogleLogin.setOnClickListener {
            // Simulasi Login Google
            val sessionManager = SessionManager(this)
            sessionManager.setLoggedIn(true)
            sessionManager.setUserId(1)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
