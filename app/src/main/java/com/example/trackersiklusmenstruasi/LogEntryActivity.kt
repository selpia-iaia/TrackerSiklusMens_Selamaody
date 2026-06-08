package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivityLogEntryBinding

class LogEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogEntryBinding

    private var selectedFlow: String? = null
    private val selectedSymptoms = mutableSetOf<String>()
    private val selectedMoods = mutableSetOf<String>()
    private val selectedMeds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupCalendarDummy()
        setupClickListeners()
    }

    private fun setupUI() {
        val pinkColor = getColor(R.color.pink_main)
        val whiteColor = Color.WHITE
        val orangeLightColor = Color.parseColor("#FFF4E5")

        // 1. Aliran Darah
        setupPill(binding.aliran1.root, "Rendah", iconRes = R.drawable.ic_drop_1, bgColor = pinkColor, tintWhite = true, type = "FLOW")
        setupPill(binding.aliran2.root, "Normal", iconRes = R.drawable.ic_drop_2, bgColor = pinkColor, tintWhite = true, type = "FLOW")
        setupPill(binding.aliran3.root, "Tinggi", iconRes = R.drawable.ic_drop_3, bgColor = pinkColor, tintWhite = true, type = "FLOW")

        // 2. Gejala
        setupPill(binding.gejala1.root, "Kepala Sakit", iconRes = R.drawable.img_sakit_kepala, bgColor = whiteColor, type = "SYMPTOM")
        setupPill(binding.gejala2.root, "Tambahkan Berat", iconRes = R.drawable.img_tambah_berat, bgColor = whiteColor, type = "SYMPTOM")
        setupPill(binding.gejala3.root, "Pegal", iconRes = R.drawable.img_pegal, bgColor = whiteColor, type = "SYMPTOM")

        // 3. Mood
        setupPill(binding.mood1.root, "Normal", emoji = "😐", bgColor = orangeLightColor, type = "MOOD")
        setupPill(binding.mood2.root, "Kesal", emoji = "😡", bgColor = orangeLightColor, type = "MOOD")
        setupPill(binding.mood3.root, "Senang", emoji = "😊", bgColor = orangeLightColor, type = "MOOD")

        // 4. Obat
        setupPill(binding.obat1.root, "Pil Obat", iconRes = R.drawable.ic_pil_obat, bgColor = whiteColor, type = "MED")
        setupPill(binding.obat2.root, "kasa", iconRes = R.drawable.ic_kasa, bgColor = whiteColor, type = "MED")
        setupPill(binding.obat3.root, "Krim Penghilang Sakit", iconRes = R.drawable.ic_krim, bgColor = whiteColor, type = "MED")
    }

    private fun setupPill(view: View, text: String, iconRes: Int? = null, emoji: String? = null, bgColor: Int, tintWhite: Boolean = false, type: String) {
        val tvText = view.findViewById<TextView>(R.id.tvPillText)
        val ivIcon = view.findViewById<ImageView>(R.id.ivPillIcon)
        val tvEmoji = view.findViewById<TextView>(R.id.tvPillEmoji)
        val flBg = view.findViewById<View>(R.id.flIconBg)
        
        tvText.text = text
        flBg.backgroundTintList = ColorStateList.valueOf(bgColor)

        if (emoji != null) {
            tvEmoji.text = emoji
            tvEmoji.visibility = View.VISIBLE
            ivIcon.visibility = View.GONE
        } else if (iconRes != null) {
            ivIcon.setImageResource(iconRes)
            ivIcon.visibility = View.VISIBLE
            tvEmoji.visibility = View.GONE
            
            if (tintWhite) {
                ivIcon.setColorFilter(Color.WHITE)
            } else {
                ivIcon.clearColorFilter()
            }
        }
        
        view.setOnClickListener {
            when (type) {
                "FLOW" -> {
                    selectedFlow = text
                    // Visual feedback for selection can be added here
                }
                "SYMPTOM" -> {
                    if (selectedSymptoms.contains(text)) selectedSymptoms.remove(text) else selectedSymptoms.add(text)
                }
                "MOOD" -> {
                    if (selectedMoods.contains(text)) selectedMoods.remove(text) else selectedMoods.add(text)
                }
                "MED" -> {
                    if (selectedMeds.contains(text)) selectedMeds.remove(text) else selectedMeds.add(text)
                }
            }
            Toast.makeText(this, "Memilih: $text", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCalendarDummy() {
        val days = listOf(
            Triple("S", "05", false),
            Triple("M", "06", true), 
            Triple("T", "07", false),
            Triple("W", "08", false),
            Triple("F", "09", false),
            Triple("S", "10", false)
        )

        val calendarLayout = binding.calendarStrip
        for (i in 0 until calendarLayout.childCount) {
            if (i < days.size) {
                val dayView = calendarLayout.getChildAt(i)
                val tvLetter = dayView.findViewById<TextView>(R.id.tvDayLetter)
                val tvNumber = dayView.findViewById<TextView>(R.id.tvDayNumber)
                
                tvLetter.text = days[i].first
                tvNumber.text = days[i].second
                
                if (days[i].third) {
                    tvNumber.setBackgroundResource(R.drawable.bg_calendar_day_selected)
                    tvNumber.setTextColor(getColor(R.color.white))
                    tvLetter.setTextColor(getColor(R.color.pink_main))
                } else {
                    tvNumber.setBackgroundResource(R.drawable.bg_calendar_day_unselected)
                    tvNumber.setTextColor(getColor(R.color.black))
                    tvLetter.setTextColor(getColor(R.color.gray))
                }
            } else {
                calendarLayout.getChildAt(i).visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { finish() }
        
        binding.btnWeight.setOnClickListener {
            startActivity(Intent(this, WeightEntryActivity::class.java))
        }

        binding.btnTemp.setOnClickListener {
            startActivity(Intent(this, TempEntryActivity::class.java))
        }
        
        binding.btnSave.setOnClickListener {
            saveToDatabase()
            Toast.makeText(this, "Log berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveToDatabase() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        
        val symptomsStr = selectedSymptoms.joinToString(",")
        val moodsStr = selectedMoods.joinToString(",")
        val medsStr = selectedMeds.joinToString(",")
        
        dbHelper.saveDailyLog(
            today,
            selectedFlow,
            symptomsStr,
            moodsStr,
            medsStr,
            "", // Note
            null, // Weight
            null, // Water
            null  // Temp
        )
    }
}
