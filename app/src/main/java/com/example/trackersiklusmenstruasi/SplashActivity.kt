package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Ensure database is initialized and opened for Database Inspector
        DatabaseHelper.getInstance(this).writableDatabase

        val sessionManager = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                // Jika sudah login, langsung ke Home
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Jika belum login, selalu tampilkan Onboarding (untuk mempermudah desain)
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 2000)
    }
}