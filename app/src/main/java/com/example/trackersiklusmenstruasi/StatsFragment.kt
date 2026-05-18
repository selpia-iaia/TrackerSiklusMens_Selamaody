package com.example.trackersiklusmenstruasi

<<<<<<< HEAD
import android.graphics.Color
=======
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentStatsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private var currentMonthOffset = 0
    private var weightChartType = HealthChartView.ChartType.BAR
    private var waterChartType = HealthChartView.ChartType.BAR
    private var tempChartType = HealthChartView.ChartType.BAR

    // Simulasi data jumlah klik (log)
    private val logCounts = mutableMapOf(
        "Rendah" to 39, "Normal" to 21, "Tinggi" to 15, "Tinggi Sekali" to 5,
        "Kepala Sakit" to 142, "Tambahkan Berat" to 10, "Pegal" to 8,
        "Normal_Mood" to 111, "Kesal" to 125, "Senang" to 45, "Sedih" to 30, "Bosan" to 12,
        "Pil Obat" to 123, "Kasa" to 100, "Obat Penghilang" to 15
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper.getInstance(requireContext())
        setupDynamicItems()
        loadExistingNote()
        setupTabs()
        setupChartControls()
        updateHealthData()
        
        binding.btnMenu.setOnClickListener {
            Toast.makeText(requireContext(), "Menu diklik", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveNote.setOnClickListener {
            saveNote()
        }

        // Connect to Cycle History (Siklus Saya)
        binding.containerLogCount.getChildAt(0).setOnClickListener {
            val intent = android.content.Intent(requireContext(), CycleHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTabs() {
        binding.tabLogCount.setOnClickListener {
            switchTab(true)
        }
        binding.tabHealth.setOnClickListener {
            switchTab(false)
        }
    }

    private fun switchTab(isLogCount: Boolean) {
        binding.containerLogCount.visibility = if (isLogCount) View.VISIBLE else View.GONE
        binding.containerHealth.visibility = if (isLogCount) View.GONE else View.VISIBLE
        
        binding.tvTabLogCount.setTextColor(resources.getColor(if (isLogCount) R.color.pink_main else R.color.gray, null))
        binding.indicatorLogCount.setBackgroundColor(resources.getColor(if (isLogCount) R.color.pink_main else R.color.stroke, null))
        binding.tvTabLogCount.setTypeface(null, if (isLogCount) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)

        binding.tvTabHealth.setTextColor(resources.getColor(if (!isLogCount) R.color.pink_main else R.color.gray, null))
        binding.indicatorHealth.setBackgroundColor(resources.getColor(if (!isLogCount) R.color.pink_main else R.color.stroke, null))
        binding.tvTabHealth.setTypeface(null, if (!isLogCount) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
    }

    private fun setupChartControls() {
        // Shared date navigation for health tab
        val nextBtn = binding.containerHealth.findViewById<View>(R.id.btnNextMonth)
        val prevBtn = binding.containerHealth.findViewById<View>(R.id.btnPrevMonth)
        
        nextBtn.setOnClickListener { 
            currentMonthOffset++
            updateHealthData()
        }
        prevBtn.setOnClickListener { 
            currentMonthOffset--
            updateHealthData()
        }

        // Toggle Buttons
        binding.btnWeightBar.setOnClickListener { weightChartType = HealthChartView.ChartType.BAR; updateHealthData() }
        binding.btnWeightLine.setOnClickListener { weightChartType = HealthChartView.ChartType.LINE; updateHealthData() }
        binding.btnWaterBar.setOnClickListener { waterChartType = HealthChartView.ChartType.BAR; updateHealthData() }
        binding.btnWaterLine.setOnClickListener { waterChartType = HealthChartView.ChartType.LINE; updateHealthData() }
        binding.btnTempBar.setOnClickListener { tempChartType = HealthChartView.ChartType.BAR; updateHealthData() }
        binding.btnTempLine.setOnClickListener { tempChartType = HealthChartView.ChartType.LINE; updateHealthData() }
    }

    private fun updateHealthData() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, currentMonthOffset)
        
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val dateRangeStr = monthYearFormat.format(calendar.time)
        binding.containerHealth.findViewById<TextView>(R.id.tvChartDateRange).text = dateRangeStr

        // Mock data for charts (In real app, fetch from db using date range)
        val weightData = listOf(50f, 52f, 51f, 55f, 53f, 54f, 52f)
        val waterData = listOf(1200f, 1540f, 1300f, 1600f, 1100f, 1400f, 1500f)
        val tempData = listOf(36.5f, 36.8f, 37.2f, 36.6f, 36.9f, 37.0f, 36.7f)

        binding.weightChart.setData(weightData, 70f, Color.parseColor("#3DDC84"), weightChartType)
        binding.waterChart.setData(waterData, 2000f, Color.parseColor("#3DA9FF"), waterChartType)
        binding.tempChart.setData(tempData, 40f, Color.parseColor("#FF5C7A"), tempChartType)

        // BMI logic
        val user = dbHelper.getUserProfile()
        user?.let {
            val weight = it.weight
            val heightM = it.height / 100
            val bmi = (weight / (heightM * heightM)).toFloat()
            binding.bmiGauge.setBMI(bmi)
            binding.tvBMICategory.text = getBMICategory(bmi)
        }
    }

    private fun getBMICategory(bmi: Float): String = when {
        bmi < 17.0f -> "Sangat Kurus"
        bmi < 18.5f -> "Kurus"
        bmi < 25.0f -> "Normal"
        bmi < 27.0f -> "Berlebih"
        else -> "Obesitas"
    }

    private fun loadExistingNote() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_DAILY_LOGS,
            arrayOf(DatabaseHelper.COLUMN_NOTE),
            "${DatabaseHelper.COLUMN_LOG_DATE} = ?",
            arrayOf(currentDate),
            null, null, null
        )
        
        if (cursor.moveToFirst()) {
            val existingNote = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE))
            binding.etStatsNote.setText(existingNote)
        }
        cursor.close()
    }

    private fun saveNote() {
        val noteText = binding.etStatsNote.text.toString().trim()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val result = dbHelper.saveDailyLog(
            currentDate,
            null,
            null,
            null,
            null,
            noteText
        )

        if (result != -1L) {
            Toast.makeText(requireContext(), "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Gagal menyimpan catatan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDynamicItems() {
        // 1. Aliran Darah
        val flowContainer = binding.flowContainer
        flowContainer.removeAllViews()
        addPillItem(flowContainer, "Rendah", R.drawable.ic_drop, true)
        addPillItem(flowContainer, "Normal", R.drawable.ic_drop, true)
        addPillItem(flowContainer, "Tinggi", R.drawable.ic_drop, true)
        addPillItem(flowContainer, "Tinggi Sekali", R.drawable.ic_drop, true)

        // 2. Gejala (Menggunakan foto ilustrasi baru)
        val symptomsContainer = binding.symptomsContainer
        symptomsContainer.removeAllViews()
        
        // Menggunakan drawable yang benar untuk gejala
        addPillItem(symptomsContainer, "Kepala Sakit", R.drawable.img_sakit_kepala, false)
        addPillItem(symptomsContainer, "Tambahkan Berat", R.drawable.img_tambah_berat, false)
        addPillItem(symptomsContainer, "Pegal", R.drawable.img_pegal, false)

        // 3. Mood
        val moodContainer = binding.moodContainer
        moodContainer.removeAllViews()
        addMoodItem(moodContainer, "Normal", "😊")
        addMoodItem(moodContainer, "Kesal", "😡")
        addMoodItem(moodContainer, "Senang", "😄")
        addMoodItem(moodContainer, "Sedih", "😢")
        addMoodItem(moodContainer, "Bosan", "😑")

        // 4. Obat
        val medicineContainer = binding.medicineContainer
        medicineContainer.removeAllViews()
        addPillItem(medicineContainer, "Pil Obat", R.drawable.ic_onboarding_drop, true, R.color.water_blue)
        addPillItem(medicineContainer, "Kasa", R.drawable.ic_onboarding_uterus, true, R.color.pink_main)
        addPillItem(medicineContainer, "Obat Penghilang", R.drawable.ic_onboarding_drop, true, R.color.google_blue)
    }

    private fun addPillItem(container: LinearLayout?, label: String, iconRes: Int, isSmall: Boolean, tintColor: Int? = null) {
        val layoutRes = if (isSmall) R.layout.item_stats_pill else R.layout.item_stats_pill_large
        val view = layoutInflater.inflate(layoutRes, container, false)
        val tv = view.findViewById<TextView>(R.id.tvLabel)
        val iv = view.findViewById<ImageView>(R.id.ivIcon)
        
        val count = logCounts[label] ?: 0
        tv.text = "$label(${count}x)"
        iv.setImageResource(iconRes)

        // Jangan gunakan tint untuk ilustrasi gejala (Kepala Sakit, Berat, Pegal)
        val isIllustration = label.contains("Kepala Sakit") || label.contains("Tambahkan Berat") || label.contains("Pegal")
        if (!isIllustration) {
            tintColor?.let { iv.setColorFilter(resources.getColor(it, null)) }
            if (tintColor == null && isSmall) {
                iv.setColorFilter(resources.getColor(R.color.pink_main, null))
            }
        } else {
            iv.clearColorFilter()
        }

        view.setOnClickListener {
            val currentCount = logCounts[label] ?: 0
            logCounts[label] = currentCount + 1
            tv.text = "$label(${logCounts[label]}x)"
            Toast.makeText(context, "$label ditambahkan!", Toast.LENGTH_SHORT).show()
        }
        container?.addView(view)
    }

    private fun addMoodItem(container: LinearLayout?, label: String, emoji: String) {
        val view = layoutInflater.inflate(R.layout.item_stats_pill, container, false)
        val tv = view.findViewById<TextView>(R.id.tvLabel)
        val iv = view.findViewById<ImageView>(R.id.ivIcon)
        iv.visibility = View.GONE
        
        // Menambahkan emoji secara manual (trik karena layout aslinya pake ImageView)
        val parent = tv.parent as LinearLayout
        val emojiTv = TextView(context).apply {
            text = emoji
            textSize = 18f
        }
        parent.addView(emojiTv, 0)

        val key = if (label == "Normal") "Normal_Mood" else label
        val count = logCounts[key] ?: 0
        tv.text = "$label(${count}x)"

        view.setOnClickListener {
            val currentCount = logCounts[key] ?: 0
            logCounts[key] = currentCount + 1
            tv.text = "$label(${logCounts[key]}x)"
            Toast.makeText(context, "Mood $label ditambahkan!", Toast.LENGTH_SHORT).show()
        }
        container?.addView(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
=======
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_placeholder, container, false)
        view.findViewById<TextView>(R.id.tvPlaceholder).text = "Stats & Analytics"
        return view
>>>>>>> f18d1391228f4e01f19254cbf5262f1ef836cbba
    }
}
