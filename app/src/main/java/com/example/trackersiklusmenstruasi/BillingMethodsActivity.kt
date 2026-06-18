package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityBillingMethodsBinding
import kotlinx.coroutines.launch

class BillingMethodsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillingMethodsBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBillingMethodsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPrefManager = SharedPrefManager(this)

        // Transparent status bar with dark icons
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.header.setPadding(binding.header.paddingLeft, systemBars.top, binding.header.paddingRight, binding.header.paddingBottom)
            insets
        }

        binding.btnBack.setOnClickListener { finish() }
        
        setupSelection()
    }

    private fun setupSelection() {
        val methods = listOf("Dana", "QRIS")
        val cards = listOf(binding.cardDana, binding.cardQRIS)
        val radios = listOf(binding.rbDana, binding.rbQRIS)

        val currentMethod = sharedPrefManager.getPreferredPayment()
        
        // Load initial state
        methods.forEachIndexed { index, name ->
            if (name == currentMethod) {
                radios[index].isChecked = true
            }
        }

        cards.forEachIndexed { index, card ->
            card.setOnClickListener {
                val selectedName = methods[index]
                
                if (selectedName == "Dana") {
                    showConnectDanaDialog(index)
                } else if (selectedName == "QRIS") {
                    showQRISDialog(index)
                }
            }
        }
    }

    private fun showConnectDanaDialog(index: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_connect_ewallet, null)
        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvTitle)
        tvTitle.text = "Hubungkan DANA"

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Hubungkan") { _, _ ->
                val phone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString()
                if (phone.isNotEmpty()) {
                    selectMethod(index, "Dana")
                    openDanaApp()
                    showToast("DANA Berhasil Terhubung!")
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun openDanaApp() {
        val packageName = "id.dana"
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            showToast("Aplikasi DANA tidak ditemukan.")
            try {
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, 
                    android.net.Uri.parse("market://details?id=$packageName")))
            } catch (e: Exception) {}
        }
    }

    private fun showQRISDialog(index: Int) {
        val imageView = android.widget.ImageView(this)
        imageView.setImageResource(R.drawable.qris_code)
        imageView.setAdjustViewBounds(true)
        imageView.setPadding(32, 32, 32, 32)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pembayaran QRIS")
            .setMessage("Silakan simpan atau scan kode QRIS di bawah ini untuk menghubungkan pembayaran")
            .setView(imageView)
            .setPositiveButton("Selesai") { _, _ ->
                selectMethod(index, "QRIS")
                showToast("QRIS Terdeteksi & Terhubung!")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun selectMethod(index: Int, name: String) {
        val radios = listOf(binding.rbDana, binding.rbQRIS)
        radios.forEach { it.isChecked = false }
        radios[index].isChecked = true
        
        sharedPrefManager.setPreferredPayment(name)
        
        // Simpan ke database via API
        val userId = sharedPrefManager.getUserId().takeIf { it != -1 } ?: 1
        lifecycleScope.launch {
            try {
                val apiService = ApiService.create()
                val response = apiService.savePaymentMethod(
                    PaymentMethodModel(
                        user_id = userId,
                        provider = name,
                        account_number = "Linked Account",
                        holder_name = "User Account"
                    )
                )
                
                if (response.success) {
                    showToast("Metode Utama: $name (Tersimpan di Cloud)")
                } else {
                    showToast("Metode Utama: $name (Gagal Sinkron)")
                }
            } catch (e: Exception) {
                showToast("Metode Utama: $name (Lokal)")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
