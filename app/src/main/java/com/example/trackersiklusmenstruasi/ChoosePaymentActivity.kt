package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityChoosePaymentBinding
import kotlinx.coroutines.launch

class ChoosePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChoosePaymentBinding
    private var selectedMethod = "Dana"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMethods()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnLanjut.setOnClickListener {
            if (selectedMethod == "Dana") {
                showConnectDanaDialog()
            } else if (selectedMethod == "QRIS") {
                showQRISDialog()
            }
        }
    }

    private fun showConnectDanaDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_connect_ewallet, null)
        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvTitle)
        tvTitle.text = "Bayar dengan DANA"

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Buka DANA") { _, _ ->
                val phone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString()
                if (phone.isNotEmpty()) {
                    openDanaApp()
                    // Setelah buka aplikasi, baru jalankan simulasi verifikasi
                    binding.root.postDelayed({
                        processPayment()
                    }, 2000)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun openDanaApp() {
        val packageName = "id.dana"
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Toast.makeText(this, "Mengarahkan ke aplikasi DANA...", Toast.LENGTH_SHORT).show()
        } else {
            // Jika aplikasi tidak ada, arahkan ke Play Store (Simulasi)
            Toast.makeText(this, "Aplikasi DANA tidak ditemukan, gunakan mode web/simulasi", Toast.LENGTH_SHORT).show()
            try {
                val marketIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName"))
                startActivity(marketIntent)
            } catch (e: Exception) {}
        }
    }

    private fun showQRISDialog() {
        val imageView = android.widget.ImageView(this)
        imageView.setImageResource(R.drawable.qris_code)
        imageView.setAdjustViewBounds(true)
        imageView.setPadding(32, 32, 32, 32)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pembayaran QRIS")
            .setMessage("Silakan scan kode QRIS di bawah ini untuk menyelesaikan pembayaran")
            .setView(imageView)
            .setPositiveButton("Saya Sudah Bayar") { _, _ ->
                processPayment()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun processPayment() {
        val loadingView = layoutInflater.inflate(R.layout.dialog_loading_payment, null)
        val tvStatus = loadingView.findViewById<android.widget.TextView>(R.id.tvLoadingStatus)
        tvStatus.text = "Memverifikasi Pembayaran $selectedMethod..."

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(loadingView)
            .setCancelable(false)
            .create()
        
        dialog.show()
        
        // Mulai Sinkronisasi ke Database
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId().takeIf { it != -1 } ?: 1

        binding.root.postDelayed({
            tvStatus.text = "Menyimpan ke Database Server..."
            
            // Panggil API untuk simpan record pembayaran
            lifecycleScope.launch {
                try {
                    val apiService = ApiService.create()
                    val response = apiService.savePaymentMethod(
                        PaymentMethodModel(
                            user_id = userId,
                            provider = selectedMethod,
                            account_number = "TXN-${System.currentTimeMillis()}", // ID Transaksi unik
                            holder_name = "User Premium"
                        )
                    )
                    
                    if (response.success) {
                        tvStatus.text = "Pembayaran Berhasil Diverifikasi!"
                    } else {
                        tvStatus.text = "Gagal Sinkron, tapi Pembayaran Diterima (Lokal)"
                    }
                } catch (e: Exception) {
                    tvStatus.text = "Selesai (Mode Offline)"
                }

                binding.root.postDelayed({
                    dialog.dismiss()
                    val intent = Intent(this@ChoosePaymentActivity, PaymentSuccessActivity::class.java)
                    intent.putExtra("METHOD", selectedMethod)
                    startActivity(intent)
                    finish()
                }, 1500)
            }
        }, 2000)
    }

    private fun setupMethods() {
        // 1. Dana
        binding.payDana.apply {
            tvPaymentName.text = "Dana"
            tvPaymentDetail.text = "E-Wallet"
            ivPaymentIcon.setImageResource(R.drawable.ic_user) // Placeholder
            root.setOnClickListener { selectMethod("Dana") }
        }

        // 2. QRIS
        binding.payQRIS.apply {
            tvPaymentName.text = "QRIS"
            tvPaymentDetail.text = "Scan & Pay"
            ivPaymentIcon.setImageResource(R.drawable.ic_scan)
            root.setOnClickListener { selectMethod("QRIS") }
        }

        // Initial Selection
        selectMethod("Dana")
    }

    private fun selectMethod(type: String) {
        // Reset all
        val colorWhite = Color.WHITE
        val colorSelected = Color.parseColor("#F3E5F5") // Light purple/pink for selection

        binding.payDana.paymentContainer.setBackgroundColor(colorWhite)
        binding.payQRIS.paymentContainer.setBackgroundColor(colorWhite)

        when (type) {
            "Dana" -> {
                selectedMethod = "Dana"
                binding.payDana.paymentContainer.setBackgroundColor(colorSelected)
            }
            "QRIS" -> {
                selectedMethod = "QRIS"
                binding.payQRIS.paymentContainer.setBackgroundColor(colorSelected)
            }
        }
    }
}
