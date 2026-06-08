package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivityGeneralSettingsBinding

class GeneralSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGeneralSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Satuan Berat
        binding.rowWeightUnit.setOnClickListener {
            val options = arrayOf("Kilogram (kg)", "Pound (lbs)")
            AlertDialog.Builder(this)
                .setTitle("Pilih Satuan Berat")
                .setItems(options) { _, which ->
                    binding.tvWeightUnit.text = options[which]
                    showToast("Satuan berat diubah ke ${options[which]}")
                }
                .show()
        }

        // Bahasa
        binding.rowLanguage.setOnClickListener {
            val options = arrayOf("Bahasa Indonesia", "English", "日本語")
            AlertDialog.Builder(this)
                .setTitle("Pilih Bahasa")
                .setItems(options) { _, which ->
                    binding.tvLanguage.text = options[which]
                    showToast("Bahasa diubah ke ${options[which]}")
                }
                .show()
        }

        // Sinkronisasi
        binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
            showToast("Sinkronisasi Otomatis: ${if(isChecked) "Aktif" else "Nonaktif"}")
        }

        binding.rowSync.setOnClickListener {
            binding.switchSync.toggle()
        }

        // Kebijakan Privasi
        binding.rowPrivacy.setOnClickListener {
            showToast("Membuka Kebijakan Privasi...")
        }

        // Ketentuan Layanan
        binding.rowTerms.setOnClickListener {
            showToast("Membuka Ketentuan Layanan...")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}