package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupItems()
        setupClickListeners()
        setupBottomNavUI()
    }

    private fun loadUserData() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val profile = dbHelper.getUserProfile()
        profile?.let {
            binding.tvProfileName.text = it.name
            binding.tvProfileEmail.text = "${it.name.lowercase().replace(" ", "")}@gmail.com"
        }
    }

    private fun setupBottomNavUI() {
        val nav = binding.customBottomNav.root
        nav.findViewById<View>(R.id.vNavBgProfile).setBackgroundResource(R.drawable.bg_circle_pink)
        nav.findViewById<ImageView>(R.id.ivNavProfile).setColorFilter(Color.WHITE)

        nav.findViewById<View>(R.id.vNavBgHome).setBackgroundResource(R.drawable.bg_circle_white)
        nav.findViewById<ImageView>(R.id.ivNavHome).setColorFilter(Color.BLACK)
        nav.findViewById<View>(R.id.vNavBgCalendar).setBackgroundResource(R.drawable.bg_circle_white)
        nav.findViewById<ImageView>(R.id.ivNavCalendar).setColorFilter(Color.BLACK)
        nav.findViewById<View>(R.id.vNavBgStats).setBackgroundResource(R.drawable.bg_circle_white)
        nav.findViewById<ImageView>(R.id.ivNavStats).setColorFilter(Color.BLACK)
    }

    private fun setupClickListeners() {
        binding.btnMenuTop.setOnClickListener {
            val popup = android.widget.PopupMenu(this, it)
            popup.menu.add("Bantuan")
            popup.menu.add("Privasi")
            popup.show()
        }

        binding.profileHeader.setOnClickListener {
            startActivity(Intent(this, PersonalDataActivity::class.java))
        }

        binding.btnPremium.setOnClickListener {
            startActivity(Intent(this, UnlockPremiumActivity::class.java))
        }

        // Click listeners for setting items
        binding.itemPengaturan.root.setOnClickListener {
            startActivity(Intent(this, GeneralSettingsActivity::class.java))
        }
        binding.itemNotifikasi.root.setOnClickListener {
            startActivity(Intent(this, ReminderAlertActivity::class.java))
        }
        binding.itemKeamanan.root.setOnClickListener {
            startActivity(Intent(this, AccountSecurityActivity::class.java))
        }
        binding.itemAkun.root.setOnClickListener {
            startActivity(Intent(this, ConnectedAccountActivity::class.java))
        }
        binding.itemMetodePembayaran.root.setOnClickListener {
            startActivity(Intent(this, BillingMethodsActivity::class.java))
        }
        binding.itemAnalisis.root.setOnClickListener {
            startActivity(Intent(this, CycleHistoryActivity::class.java))
        }

        binding.btnKeluar.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    val sessionManager = SessionManager(this)
                    sessionManager.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        val nav = binding.customBottomNav.root
        nav.findViewById<View>(R.id.btnNavHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        nav.findViewById<View>(R.id.btnNavCalendar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply { putExtra("TARGET_FRAGMENT", "CALENDAR") })
            finish()
        }
        nav.findViewById<View>(R.id.btnNavStats).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply { putExtra("TARGET_FRAGMENT", "STATS") })
            finish()
        }
    }

    private fun setupItems() {
        // Section Umum
        binding.itemPengaturan.apply {
            tvTitle.text = "Pengaturan"
            ivIcon.setImageResource(R.drawable.ic_settings)
        }
        binding.itemNotifikasi.apply {
            tvTitle.text = "Peringatan Pengingat"
            ivIcon.setImageResource(R.drawable.ic_calendar) // Harusnya ikon jam jika ada
        }
        binding.itemKeamanan.apply {
            tvTitle.text = "Akun & Keamanan"
            ivIcon.setImageResource(R.drawable.ic_lock)
        }
        binding.itemAkun.apply {
            tvTitle.text = "Akun Terhubung"
            ivIcon.setImageResource(R.drawable.ic_user) // Harusnya ikon panah bolak balik
        }

        // Section Support
        binding.itemMetodePembayaran.apply {
            tvTitle.text = "Metode Pembayaran"
            ivIcon.setImageResource(R.drawable.ic_visa)
        }
        binding.itemAnalisis.apply {
            tvTitle.text = "Data & Analisis"
            ivIcon.setImageResource(R.drawable.ic_bar_chart)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
