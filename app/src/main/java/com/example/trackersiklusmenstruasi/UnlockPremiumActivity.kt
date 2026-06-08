package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivityUnlockPremiumBinding
import com.example.trackersiklusmenstruasi.databinding.ItemFeatureBinding

class UnlockPremiumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnlockPremiumBinding
    private var isYearly = false
    private var isBillingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUnlockPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.header.setPadding(binding.header.paddingLeft, systemBars.top, binding.header.paddingRight, binding.header.paddingBottom)
            insets
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.tabBulan.setOnClickListener { selectTab(false) }
        binding.tabTahun.setOnClickListener { selectTab(true) }

        setupFeatures()

        binding.btnAction.setOnClickListener {
            if (!isBillingMode) {
                val intent = Intent(this, ChoosePaymentActivity::class.java)
                intent.putExtra("PLAN_PRICE", if (isYearly) "Rp600.000" else "Rp50.000")
                intent.putExtra("PLAN_SUFFIX", if (isYearly) "/Tahun" else "/bulan")
                startActivity(intent)
            } else {
                finish()
            }
        }
        
        val mode = intent.getStringExtra("MODE")
        if (mode == "BILLING") {
            isBillingMode = true
            binding.tvTitle.text = "Penagihan & Paket"
            binding.btnAction.visibility = View.GONE // Hide button as per image 56
            binding.tvActivePackage.visibility = View.VISIBLE
            binding.tvExpiryInfo.visibility = View.VISIBLE
            selectTab(true) 
        } else {
            isBillingMode = false
            binding.tvTitle.text = "Buka kunci Premium"
            binding.btnAction.visibility = View.VISIBLE
            binding.btnAction.text = "Lanjut"
            binding.tvActivePackage.visibility = View.GONE
            binding.tvExpiryInfo.visibility = View.GONE
            selectTab(false)
        }
    }

    private fun setupFeatures() {
        val features = listOf(
            "Nikmati Tanpa Iklan",
            "Pelacakan Siklus Tingkat Lanjut",
            "Data & Analisis Lengkap",
            "Dapatkan Akses Eksklusif",
            "Dukungan Premium",
            "Coba Fitur Baru Lebih Awal"
        )
        
        val featureBindings = listOf(
            ItemFeatureBinding.bind(binding.feat1.root),
            ItemFeatureBinding.bind(binding.feat2.root),
            ItemFeatureBinding.bind(binding.feat3.root),
            ItemFeatureBinding.bind(binding.feat4.root),
            ItemFeatureBinding.bind(binding.feat5.root),
            ItemFeatureBinding.bind(binding.feat6.root)
        )
        
        features.forEachIndexed { index, s ->
            featureBindings[index].tvFeature.text = s
        }
    }

    private fun selectTab(yearly: Boolean) {
        isYearly = yearly
        val pinkMain = Color.parseColor("#FF5C7A")
        val gray = Color.parseColor("#D1D5DB")
        
        if (yearly) {
            binding.tabTahun.setTextColor(pinkMain)
            binding.tabTahun.setTypeface(null, android.graphics.Typeface.BOLD)
            binding.indicatorTahun.setBackgroundColor(pinkMain)
            
            binding.tabBulan.setTextColor(Color.GRAY)
            binding.tabBulan.setTypeface(null, android.graphics.Typeface.NORMAL)
            binding.indicatorBulan.setBackgroundColor(gray)
            
            binding.tvPrice.text = "Rp600.000"
            binding.tvPriceSuffix.text = "/Tahun"
            binding.tvHematBadge.visibility = View.VISIBLE
        } else {
            binding.tabBulan.setTextColor(pinkMain)
            binding.tabBulan.setTypeface(null, android.graphics.Typeface.BOLD)
            binding.indicatorBulan.setBackgroundColor(pinkMain)
            
            binding.tabTahun.setTextColor(Color.GRAY)
            binding.tabTahun.setTypeface(null, android.graphics.Typeface.NORMAL)
            binding.indicatorTahun.setBackgroundColor(gray)
            
            binding.tvPrice.text = "Rp50.000"
            binding.tvPriceSuffix.text = "/bulan"
            binding.tvHematBadge.visibility = View.GONE
        }
    }
}
