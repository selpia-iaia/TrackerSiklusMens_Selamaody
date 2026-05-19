package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupItems()

        binding.btnPremium.setOnClickListener {
            startActivity(Intent(this, UnlockPremiumActivity::class.java))
        }

        binding.btnKeluar.setOnClickListener {
            finish()
        }
    }

    private fun setupItems() {
        // Setup individual settings items
        binding.itemPengaturan.apply {
            ivIcon.setImageResource(R.drawable.ic_settings)
            tvTitle.text = "Pengaturan"
        }
        binding.itemNotifikasi.apply {
            ivIcon.setImageResource(R.drawable.ic_calendar)
            tvTitle.text = "Peringatan Pengingat"
        }
        binding.itemKeamanan.apply {
            ivIcon.setImageResource(R.drawable.ic_lock)
            tvTitle.text = "Akun & Keamanan"
        }
        binding.itemAkun.apply {
            ivIcon.setImageResource(R.drawable.ic_user)
            tvTitle.text = "Akun Terhubung"
        }

        binding.itemMetodePembayaran.apply {
            ivIcon.setImageResource(R.drawable.ic_plus)
            tvTitle.text = "Metode Pembayaran"
            root.setOnClickListener {
                startActivity(Intent(this@SettingsActivity, ChoosePaymentActivity::class.java))
            }
        }
        binding.itemAnalisis.apply {
            ivIcon.setImageResource(R.drawable.ic_stats)
            tvTitle.text = "Data & Analisis"
        }
        binding.itemPenampilan1.apply {
            ivIcon.setImageResource(R.drawable.ic_eye)
            tvTitle.text = "Penampilan Aplikasi"
        }
        binding.itemPenampilan2.apply {
            ivIcon.setImageResource(R.drawable.ic_list)
            tvTitle.text = "Riwayat Siklus"
        }
    }
}
