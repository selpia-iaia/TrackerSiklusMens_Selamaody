package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivityBillingMethodsBinding

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
        val methods = listOf("Bank BCA", "E-Wallet (OVO/Gopay/Dana)", "Google Play Billing")
        val cards = listOf(binding.cardBCA, binding.cardWallet, binding.cardGoogle)
        val radios = listOf(binding.rbBCA, binding.rbWallet, binding.rbGoogle)

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
                
                if (selectedName.contains("E-Wallet")) {
                    showConnectEWalletDialog(index)
                } else {
                    selectMethod(index, selectedName)
                }
            }
        }
    }

    private fun showConnectEWalletDialog(index: Int) {
        val options = arrayOf("OVO", "GoPay", "Dana", "QRIS (Tampilkan Kode)")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Hubungkan Pembayaran")
            .setItems(options) { _, which ->
                val selected = options[which]
                if (selected.contains("QRIS")) {
                    showQRISDialog(index)
                } else {
                    connectEWallet(index, selected)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun connectEWallet(index: Int, provider: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_connect_ewallet, null)
        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvTitle)
        tvTitle.text = "Hubungkan $provider"

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Hubungkan") { _, _ ->
                val phone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString()
                if (phone.isNotEmpty()) {
                    val methodName = "E-Wallet ($provider)"
                    selectMethod(index, methodName)
                    
                    // Open the actual app for authentication
                    openEWalletApp(provider)
                    
                    showToast("$provider Berhasil Terhubung!")
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun openEWalletApp(provider: String) {
        val packageName = when (provider) {
            "OVO" -> "com.pede.ovo"
            "GoPay" -> "com.gopay.app"
            "Dana" -> "id.dana"
            else -> null
        }
        
        if (packageName != null) {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // Fallback for GoPay specifically since it's sometimes inside Gojek
                if (provider == "GoPay") {
                    val gojekIntent = packageManager.getLaunchIntentForPackage("com.gojek.app")
                    if (gojekIntent != null) {
                        startActivity(gojekIntent)
                        return
                    }
                }
                
                // If not found, show info
                showToast("Aplikasi $provider tidak ditemukan di perangkat ini.")
                try {
                    startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, 
                        android.net.Uri.parse("market://details?id=$packageName")))
                } catch (e: Exception) {}
            }
        }
    }

    private fun showQRISDialog(index: Int) {
        val imageView = android.widget.ImageView(this)
        imageView.setImageResource(R.drawable.ic_scan) // Menggunakan ic_scan sebagai placeholder QRIS
        imageView.setPadding(100, 100, 100, 100)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pembayaran QRIS")
            .setMessage("Scan kode di bawah ini untuk menghubungkan pembayaran")
            .setView(imageView)
            .setPositiveButton("Selesai") { _, _ ->
                selectMethod(index, "E-Wallet (QRIS)")
                showToast("QRIS Terdeteksi & Terhubung!")
            }
            .show()
    }

    private fun selectMethod(index: Int, name: String) {
        val radios = listOf(binding.rbBCA, binding.rbWallet, binding.rbGoogle)
        radios.forEach { it.isChecked = false }
        radios[index].isChecked = true
        
        sharedPrefManager.setPreferredPayment(name)
        showToast("Metode Utama: $name")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
