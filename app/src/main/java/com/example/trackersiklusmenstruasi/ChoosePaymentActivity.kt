package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityChoosePaymentBinding

class ChoosePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChoosePaymentBinding
    private var selectedMethod = "Google Pay"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMethods()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnLanjut.setOnClickListener {
            // Success simulated
            Toast.makeText(this, "Pembayaran dengan $selectedMethod Berhasil!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, PaymentSuccessActivity::class.java)
            intent.putExtra("METHOD", selectedMethod)
            startActivity(intent)
        }
    }

    private fun setupMethods() {
        // 1. Bank BCA
        binding.payBCA.apply {
            tvPaymentName.text = "Bank BCA"
            tvPaymentDetail.text = "Transfer Virtual Account"
            ivPaymentIcon.setImageResource(R.drawable.ic_visa) // Placeholder
            root.setOnClickListener { selectMethod("BCA") }
        }

        // 2. E-Wallet
        binding.payWallet.apply {
            tvPaymentName.text = "E-Wallet"
            tvPaymentDetail.text = "OVO, GoPay, Dana, QRIS"
            ivPaymentIcon.setImageResource(R.drawable.ic_scan)
            root.setOnClickListener { selectMethod("Wallet") }
        }

        // 3. Google Pay (SELECTED in image)
        binding.payGoogle.apply {
            tvPaymentName.text = "Google Pay"
            tvPaymentDetail.text = "sela123@gmail.com"
            ivPaymentIcon.setImageResource(R.drawable.ic_google)
            root.setOnClickListener { selectMethod("Google") }
        }

        // 4. Tambahkan
        binding.payAdd.apply {
            tvPaymentName.text = "Tambahkan"
            tvPaymentDetail.text = "Metode Pembayaran Baru"
            ivPaymentIcon.setImageResource(R.drawable.ic_plus)
            root.setOnClickListener {
                startActivity(Intent(this@ChoosePaymentActivity, AddPayOptionActivity::class.java))
            }
        }
        
        // Initial Selection
        selectMethod("Google")
    }

    private fun selectMethod(type: String) {
        // Reset all
        val colorWhite = Color.WHITE
        val colorPurple = Color.parseColor("#E0D7FF") // Match screenshot selected color

        binding.payBCA.paymentContainer.setBackgroundColor(colorWhite)
        binding.payWallet.paymentContainer.setBackgroundColor(colorWhite)
        binding.payGoogle.paymentContainer.setBackgroundColor(colorWhite)
        binding.payAdd.paymentContainer.setBackgroundColor(colorWhite)

        when (type) {
            "BCA" -> {
                selectedMethod = "Bank BCA"
                binding.payBCA.paymentContainer.setBackgroundColor(colorPurple)
            }
            "Wallet" -> {
                selectedMethod = "E-Wallet"
                binding.payWallet.paymentContainer.setBackgroundColor(colorPurple)
            }
            "Google" -> {
                selectedMethod = "Google Pay"
                binding.payGoogle.paymentContainer.setBackgroundColor(colorPurple)
            }
        }
    }
}
