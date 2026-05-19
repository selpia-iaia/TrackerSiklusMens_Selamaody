package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityUnlockPremiumBinding

class UnlockPremiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnlockPremiumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnlockPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupFeatures()

        binding.tabBulan.setOnClickListener {
            selectTab(true)
        }

        binding.tabTahun.setOnClickListener {
            selectTab(false)
        }

        binding.btnLanjut.setOnClickListener {
            startActivity(Intent(this, ChoosePaymentActivity::class.java))
        }
    }

    private fun selectTab(isBulan: Boolean) {
        if (isBulan) {
            binding.tabBulan.apply {
                setTextColor(getColor(R.color.pink_main))
                setBackgroundResource(R.drawable.bg_tab_selected)
            }
            binding.tabTahun.apply {
                setTextColor(getColor(R.color.gray))
                setBackgroundResource(R.drawable.bg_tab_unselected)
            }
            binding.tvPrice.text = "Rp50.000"
            binding.tvPriceSuffix.text = "/bulan"
        } else {
            binding.tabTahun.apply {
                setTextColor(getColor(R.color.pink_main))
                setBackgroundResource(R.drawable.bg_tab_selected)
            }
            binding.tabBulan.apply {
                setTextColor(getColor(R.color.gray))
                setBackgroundResource(R.drawable.bg_tab_unselected)
            }
            binding.tvPrice.text = "Rp600.000"
            binding.tvPriceSuffix.text = "/tahun"
        }
    }

    private fun setupFeatures() {
        binding.feature1.tvFeature.text = "Nikmati Tanpa Iklan"
        binding.feature2.tvFeature.text = "Pelacakan Siklus Tingkat Lanjut"
        binding.feature3.tvFeature.text = "Data & Analisis Lengkap"
        binding.feature4.tvFeature.text = "Dapatkan Akses Eksklusif"
        binding.feature5.tvFeature.text = "Dukungan Premium"
        binding.feature6.tvFeature.text = "Coba Fitur Baru Lebih Awal"
    }
}
