package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivitySummaryReportBinding

class SummaryReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Transparent status bar with dark icons
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding = ActivitySummaryReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener { finish() }

        // Get data from intent
        val price = intent.getStringExtra("PLAN_PRICE") ?: "Rp50.000"
        val suffix = intent.getStringExtra("PLAN_SUFFIX") ?: "/bulan"
        val method = intent.getStringExtra("METHOD_NAME") ?: "Google Pay"

        binding.tvPrice.text = price
        binding.tvName.text = method
        
        // Setup icon based on method
        when(method) {
            "Google Pay" -> binding.ivIcon.setImageResource(R.drawable.ic_google)
            "QRIS", "E-Wallet" -> binding.ivIcon.setImageResource(R.drawable.ic_scan)
            "Bank BCA" -> binding.ivIcon.setImageResource(R.drawable.ic_visa)
            else -> binding.ivIcon.setImageResource(R.drawable.ic_google)
        }

        binding.btnConfirm.setOnClickListener {
            showLoadingAndComplete()
        }
    }

    private fun showLoadingAndComplete() {
        val dialog = LoadingDialogFragment()
        dialog.show(supportFragmentManager, "loading")

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            startActivity(Intent(this, PaymentSuccessActivity::class.java))
            finish()
        }, 2000)
    }
}
