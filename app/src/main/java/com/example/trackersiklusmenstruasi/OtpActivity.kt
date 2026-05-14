package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private var otpCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnVerify.setOnClickListener {
            if (otpCode.length == 4) {
                SessionManager(this).setLoggedIn(true)
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
        }

        setupKeypad()
    }

    private fun setupKeypad() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6, binding.btn7,
            binding.btn8, binding.btn9
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                if (otpCode.length < 4) {
                    otpCode += button.text
                    updateOtpDisplay()
                }
            }
        }

        binding.btnBackspace.setOnClickListener {
            if (otpCode.isNotEmpty()) {
                otpCode = otpCode.substring(0, otpCode.length - 1)
                updateOtpDisplay()
            }
        }
    }

    private fun updateOtpDisplay() {
        val displays = listOf(binding.tvOtp1, binding.tvOtp2, binding.tvOtp3, binding.tvOtp4)
        displays.forEach { it.text = "" }
        
        for (i in otpCode.indices) {
            displays[i].text = otpCode[i].toString()
        }
    }
}
