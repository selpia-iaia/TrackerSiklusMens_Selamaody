package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        setupHealthCharts()
        setupLogIcons()
        setupClickListeners()
        loadTodayNote()
        loadSummaryData()
    }

    private fun loadSummaryData() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile()
        profile?.let {
            binding.tvAvgPeriodStats.text = "${it.period_length} Hari"
            binding.tvAvgCycleStats.text = "${it.cycle_length} Hari"
        }
    }

    private fun setupClickListeners() {
        binding.btnMenu.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), it)
            popup.menu.add("Data Pribadi")
            popup.menu.add("Pengaturan")
            popup.menu.add("Tips Kesehatan")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Data Pribadi" -> {
                        startActivity(android.content.Intent(requireContext(), PersonalDataActivity::class.java))
                        true
                    }
                    "Pengaturan" -> {
                        startActivity(android.content.Intent(requireContext(), SettingsActivity::class.java))
                        true
                    }
                    "Tips Kesehatan" -> {
                        startActivity(android.content.Intent(requireContext(), HealthTipsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        binding.btnSaveNote.setOnClickListener {
            saveNote()
        }

        binding.btnMyCycle.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), CycleHistoryActivity::class.java))
        }
    }

    private fun loadTodayNote() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val today = sdf.format(java.util.Date())
        
        // Cek log hari ini
        val logs = dbHelper.getAllDailyLogs()
        val todayLog = logs.find { it.date == today }
        todayLog?.let {
            binding.etStatsNote.setText(it.note)
        }
    }

    private fun saveNote() {
        val note = binding.etStatsNote.text.toString()
        if (note.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Catatan tidak boleh kosong", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val today = sdf.format(java.util.Date())

        // Ambil log yang sudah ada atau buat baru
        val logs = dbHelper.getAllDailyLogs()
        val existingLog = logs.find { it.date == today }

        dbHelper.saveDailyLog(
            today,
            existingLog?.flow ?: "Normal",
            existingLog?.symptoms ?: "",
            existingLog?.moods ?: "",
            existingLog?.medicine ?: "",
            note,
            existingLog?.weight,
            existingLog?.water,
            existingLog?.temp
        )

        android.widget.Toast.makeText(requireContext(), "Catatan berhasil disimpan!", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun setupTabs() {
        binding.tabLogCount.setOnClickListener { showTabContent(true) }
        binding.tabHealth.setOnClickListener { showTabContent(false) }
        
        // Default ke "Jumlah Log" sesuai tampilan yang diinginkan user
        showTabContent(true)
    }

    private fun showTabContent(isLogCount: Boolean) {
        if (isLogCount) {
            binding.containerLogCount.visibility = View.VISIBLE
            binding.containerHealth.visibility = View.GONE
            
            binding.tvTabLogCount.setTextColor(resources.getColor(R.color.pink_main, null))
            binding.tvTabLogCount.paint.isFakeBoldText = true
            binding.indicatorLogCount.visibility = View.VISIBLE
            
            binding.tvTabHealth.setTextColor(Color.GRAY)
            binding.tvTabHealth.paint.isFakeBoldText = false
            binding.indicatorHealth.visibility = View.INVISIBLE
        } else {
            binding.containerLogCount.visibility = View.GONE
            binding.containerHealth.visibility = View.VISIBLE
            
            binding.tvTabHealth.setTextColor(resources.getColor(R.color.pink_main, null))
            binding.tvTabHealth.paint.isFakeBoldText = true
            binding.indicatorHealth.visibility = View.VISIBLE
            
            binding.tvTabLogCount.setTextColor(Color.GRAY)
            binding.tvTabLogCount.paint.isFakeBoldText = false
            binding.indicatorLogCount.visibility = View.INVISIBLE
        }
    }

    private fun setupHealthCharts() {
        // Data Dummy untuk Grafik Berat
        val weightData = listOf(48f, 49f, 52f, 50f, 48f, 51f, 49f)
        binding.weightChart.setData(weightData, 60f, Color.parseColor("#81C784"), HealthChartView.ChartType.BAR)

        // BMI Gauge
        binding.bmiGauge.setBMI(19.0f)
        binding.tvBMICategory.text = "Normal"

        // Data Dummy untuk Air
        val waterData = listOf(1200f, 1500f, 1800f, 1540f, 1300f, 1600f, 1400f)
        binding.waterChart.setData(waterData, 2000f, Color.parseColor("#4FC3F7"), HealthChartView.ChartType.BAR)

        // Data Dummy untuk Suhu
        val tempData = listOf(36.2f, 36.5f, 36.8f, 34.5f, 36.6f, 37.0f, 36.4f)
        binding.tempChart.setData(tempData, 40f, Color.parseColor("#FF8A65"), HealthChartView.ChartType.BAR)

        // Setup Chart Switchers (Bar vs Line)
        setupChartSwitchers()
    }

    private fun setupChartSwitchers() {
        // Berat
        binding.btnWeightBar.setOnClickListener { updateChartType(binding.weightChart, HealthChartView.ChartType.BAR, it, binding.btnWeightLine) }
        binding.btnWeightLine.setOnClickListener { updateChartType(binding.weightChart, HealthChartView.ChartType.LINE, it, binding.btnWeightBar) }

        // Air
        binding.btnWaterBar.setOnClickListener { updateChartType(binding.waterChart, HealthChartView.ChartType.BAR, it, binding.btnWaterLine) }
        binding.btnWaterLine.setOnClickListener { updateChartType(binding.waterChart, HealthChartView.ChartType.LINE, it, binding.btnWaterBar) }

        // Suhu
        binding.btnTempBar.setOnClickListener { updateChartType(binding.tempChart, HealthChartView.ChartType.BAR, it, binding.btnTempLine) }
        binding.btnTempLine.setOnClickListener { updateChartType(binding.tempChart, HealthChartView.ChartType.LINE, it, binding.btnTempBar) }
    }

    private fun updateChartType(chart: HealthChartView, type: HealthChartView.ChartType, activeBtn: View, inactiveBtn: View) {
        // Logika sederhana ganti tampilan grafik
        // Data tetap sama, cuma tipe yang berubah
        val currentData = when(chart.id) {
            R.id.weightChart -> listOf(48f, 49f, 52f, 50f, 48f, 51f, 49f)
            R.id.waterChart -> listOf(1200f, 1500f, 1800f, 1540f, 1300f, 1600f, 1400f)
            else -> listOf(36.2f, 36.5f, 36.8f, 34.5f, 36.6f, 37.0f, 36.4f)
        }
        val color = when(chart.id) {
            R.id.weightChart -> Color.parseColor("#81C784")
            R.id.waterChart -> Color.parseColor("#4FC3F7")
            else -> Color.parseColor("#FF8A65")
        }
        val max = if (chart.id == R.id.tempChart) 40f else if (chart.id == R.id.waterChart) 2000f else 60f
        
        chart.setData(currentData, max, color, type)
        
        activeBtn.setBackgroundResource(R.drawable.bg_circle_pink)
        (activeBtn as ImageView).setColorFilter(Color.WHITE)
        
        inactiveBtn.setBackgroundResource(R.drawable.bg_circle_white)
        (inactiveBtn as ImageView).setColorFilter(Color.GRAY)
    }

    private fun setupLogIcons() {
        // Mengisi ikon-ikon di tab "Jumlah Log" agar tidak kosong
        addIconToContainer(binding.flowContainer, "🩸")
        addIconToContainer(binding.symptomsContainer, "🤕", "🤢", "😴")
        addIconToContainer(binding.moodContainer, "😊", "😐", "😔")
        addIconToContainer(binding.medicineContainer, "💊")
    }

    private fun addIconToContainer(container: LinearLayout, vararg emojis: String) {
        container.removeAllViews()
        for (emoji in emojis) {
            val tv = TextView(requireContext()).apply {
                text = emoji
                textSize = 24f
                layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                    setMargins(0, 0, 20, 0)
                }
                gravity = Gravity.CENTER
                background = resources.getDrawable(R.drawable.bg_pill_pink_light, null)
            }
            container.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
