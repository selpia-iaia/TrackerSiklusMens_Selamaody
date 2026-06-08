package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivitySettingsBinding
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupItems()
        setupClickListeners()
        setupBottomNavUI()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        // Load Image safely
        try {
            val file = File(filesDir, "profile_pic_final.jpg")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.ivProfile.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val dbHelper = DatabaseHelper.getInstance(this)
        val profile = dbHelper.getUserProfile()
        profile?.let {
            binding.tvHeaderName.text = it.name
            binding.tvProfileName.text = it.name
            binding.tvProfileEmail.text = "${it.name.lowercase().replace(" ", "")}@gmail.com"
        }
    }

    private fun setupBottomNavUI() {
        val nav = binding.customBottomNav.root
        
        // Profile is active (Index 3)
        val items = listOf(
            R.id.btnNavHome to R.id.ivNavHome,
            R.id.btnNavCalendar to R.id.ivNavCalendar,
            R.id.btnNavStats to R.id.ivNavStats,
            R.id.btnNavProfile to R.id.ivNavProfile
        )
        val activeIndex = 3

        items.forEachIndexed { i, (btnId, ivId) ->
            val iv = nav.findViewById<ImageView>(ivId)
            val bgViewId = when(btnId) {
                R.id.btnNavHome -> R.id.vNavBgHome
                R.id.btnNavCalendar -> R.id.vNavBgCalendar
                R.id.btnNavStats -> R.id.vNavBgStats
                R.id.btnNavProfile -> R.id.vNavBgProfile
                else -> -1
            }
            val bgView = nav.findViewById<View>(bgViewId)
            
            if (i == activeIndex) {
                bgView?.setBackgroundResource(R.drawable.bg_circle_pink)
                iv?.setColorFilter(Color.WHITE)
            } else {
                bgView?.setBackgroundResource(R.drawable.bg_circle_white)
                iv?.setColorFilter(Color.BLACK)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnMenuTop.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.menu.add("Bantuan")
            
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Bantuan" -> startActivity(Intent(this, HelpActivity::class.java))
                }
                true
            }
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

        binding.itemKeluar.root.setOnClickListener {
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
            ivIcon.setImageResource(R.drawable.ic_volume_up)
        }
        binding.itemKeamanan.apply {
            tvTitle.text = "Akun & Keamanan"
            ivIcon.setImageResource(R.drawable.ic_lock)
        }
        binding.itemAkun.apply {
            tvTitle.text = "Akun Terhubung"
            ivIcon.setImageResource(R.drawable.ic_list)
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

        binding.itemKeluar.apply {
            tvTitle.text = "Keluar"
            tvTitle.setTextColor(Color.parseColor("#FF5C7A"))
            ivIcon.setImageResource(R.drawable.ic_close)
            ivIcon.setColorFilter(Color.parseColor("#FF5C7A"))
        }
    }
}
