package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityRegisterBinding

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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

        binding.btnSignup.setOnClickListener {
            val username = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch {
                try {
                    val apiService = ApiService.create()
                    val response = apiService.registerUser(UserModel(username, email, password))
                    if (response.success) {
                        // Simpan User ID asli dari server ke SessionManager
                        response.user_id?.let { id ->
                            SessionManager(this@RegisterActivity).setUserId(id)
                        }
                        startActivity(Intent(this@RegisterActivity, ProfileSetupActivity::class.java))
                        finish()
                    } else {
                        android.widget.Toast.makeText(this@RegisterActivity, response.message, android.widget.Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Jika offline, lanjut dengan ID dummy untuk testing
                    startActivity(Intent(this@RegisterActivity, ProfileSetupActivity::class.java))
                    finish()
                }
            }
        }
    }
}
