package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSignup.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        binding.btnSignin.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        }

        // Menambahkan listener untuk social buttons agar bisa di-klik
        binding.btnGoogle.setOnClickListener {
            // Untuk sementara diarahkan ke RegisterActivity
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnApple.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnFacebook.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}