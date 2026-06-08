package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentStatsBinding
import com.google.android.material.tabs.TabLayout

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()
        loadData()
        setupHealthStatsView()
        setupClickListeners()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.scrollLogCount.visibility = View.VISIBLE
                    binding.scrollHealthStats.visibility = View.GONE
                } else {
                    binding.scrollLogCount.visibility = View.GONE
                    binding.scrollHealthStats.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadData() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val logs = dbHelper.getAllDailyLogs()

        // 1. Siklus Saya (Keeping them mostly static for now as per design)
        binding.cardCycle1.apply {
            tvDuration.text = "3 Hari"
            ivIcon.setImageResource(R.drawable.ic_drop)
            ivIcon.setColorFilter(Color.parseColor("#FF5C7A"))
            tvType.text = "Siklus Khas"
        }
        binding.cardCycle2.apply {
            tvDuration.text = "26 Hari"
            ivIcon.setImageResource(R.drawable.ic_calendar)
            ivIcon.setColorFilter(Color.BLACK)
            tvType.text = "Siklus Khas"
        }

        // Count occurrences
        val flowCounts = mutableMapOf("Rendah" to 0, "Normal" to 0, "Tinggi" to 0)
        val symptomCounts = mutableMapOf("Kepala Sakit" to 0, "Tambahkan Berat" to 0, "Pegal" to 0)
        val moodCounts = mutableMapOf("Normal" to 0, "Kesal" to 0, "Senang" to 0)
        val medicineCounts = mutableMapOf("Pil Obat" to 0, "kasa" to 0, "Krim Penghilang Sakit" to 0)

        logs.forEach { log ->
            log.flow?.let { flowCounts[it] = (flowCounts[it] ?: 0) + 1 }
            log.symptoms?.let { sym ->
                sym.split(",").forEach { s ->
                    val sTrim = s.trim()
                    if (sTrim.isNotEmpty()) {
                        symptomCounts[sTrim] = (symptomCounts[sTrim] ?: 0) + 1
                    }
                }
            }
            log.moods?.let { m ->
                m.split(",").forEach { mood ->
                    val mTrim = mood.trim()
                    if (mTrim.isNotEmpty()) {
                        moodCounts[mTrim] = (moodCounts[mTrim] ?: 0) + 1
                    }
                }
            }
            log.medicine?.let { med ->
                med.split(",").forEach { m ->
                    val mTrim = m.trim()
                    if (mTrim.isNotEmpty()) {
                        medicineCounts[mTrim] = (medicineCounts[mTrim] ?: 0) + 1
                    }
                }
            }
        }

        // 2. Aliran Darah
        setupPills(binding.containerAliran, listOf(
            Triple("Rendah(${flowCounts["Rendah"]}x)", R.drawable.ic_drop_1, "#FFEBEE"),
            Triple("Normal(${flowCounts["Normal"]}x)", R.drawable.ic_drop_2, "#FFEBEE"),
            Triple("Tinggi(${flowCounts["Tinggi"]}x)", R.drawable.ic_drop_3, "#FFEBEE")
        ))

        // 3. Gejala
        setupPills(binding.containerGejala, listOf(
            Triple("Kepala Sakit(${symptomCounts["Kepala Sakit"]}x)", R.drawable.img_sakit_kepala, "#FFFFFF"),
            Triple("Tambahkan Berat(${symptomCounts["Tambahkan Berat"]}x)", R.drawable.img_tambah_berat, "#FFFFFF"),
            Triple("Pegal(${symptomCounts["Pegal"]}x)", R.drawable.img_pegal, "#FFFFFF")
        ))

        // 4. Mood
        setupPills(binding.containerMood, listOf(
            Triple("Normal(${moodCounts["Normal"]}x)", "😐", "#FFF4E5"),
            Triple("Kesal(${moodCounts["Kesal"]}x)", "😡", "#FFF4E5"),
            Triple("Senang(${moodCounts["Senang"]}x)", "😊", "#FFF4E5")
        ))

        // 5. Obat
        setupPills(binding.containerObat, listOf(
            Triple("Pil Obat(${medicineCounts["Pil Obat"]}x)", R.drawable.ic_pil_obat, "#FFFFFF"),
            Triple("Kasa(${medicineCounts["kasa"]}x)", R.drawable.ic_kasa, "#FFFFFF"),
            Triple("Krim(${medicineCounts["Krim Penghilang Sakit"]}x)", R.drawable.ic_krim, "#FFFFFF")
        ))
    }

    private fun setupHealthStatsView() {
        val weightData = listOf(45f, 48f, 50f, 52f, 48f, 53f, 49f)
        binding.chartWeight.setData(weightData, 80f, Color.parseColor("#4CAF50"), HealthChartView.ChartType.BAR, 3, "kg")
        binding.sectionWeight.tvChartTitle.text = "Berat(kg)"
        
        binding.sectionWeight.btnBarChart.setOnClickListener {
            updateToggleUI(binding.sectionWeight.root, true)
            binding.chartWeight.setData(weightData, 80f, Color.parseColor("#4CAF50"), HealthChartView.ChartType.BAR, 3, "kg")
        }
        binding.sectionWeight.btnLineChart.setOnClickListener {
            updateToggleUI(binding.sectionWeight.root, false)
            binding.chartWeight.setData(weightData, 80f, Color.parseColor("#4CAF50"), HealthChartView.ChartType.LINE, 3, "kg")
        }

        binding.bmiGauge.setBMI(18.0f)
        binding.tvBmiStatus.text = "Normal"

        val waterData = listOf(1200f, 1540f, 1100f, 1800f, 1400f, 1600f, 1300f)
        binding.chartWater.setData(waterData, 2400f, Color.parseColor("#4285F4"), HealthChartView.ChartType.BAR, 1, "ml")
        binding.sectionWater.tvChartTitle.text = "Air (ml)"
        
        binding.sectionWater.btnBarChart.setOnClickListener {
            updateToggleUI(binding.sectionWater.root, true)
            binding.chartWater.setData(waterData, 2400f, Color.parseColor("#4285F4"), HealthChartView.ChartType.BAR, 1, "ml")
        }
        binding.sectionWater.btnLineChart.setOnClickListener {
            updateToggleUI(binding.sectionWater.root, false)
            binding.chartWater.setData(waterData, 2400f, Color.parseColor("#4285F4"), HealthChartView.ChartType.LINE, 1, "ml")
        }

        val tempData = listOf(36.5f, 36.8f, 34.5f, 37.2f, 36.9f, 36.6f, 36.7f)
        binding.chartTemp.setData(tempData, 40f, Color.parseColor("#3DDC84"), HealthChartView.ChartType.BAR, 2, "°C")
        binding.sectionTemp.tvChartTitle.text = "Temperature (°C)"

        binding.sectionTemp.btnBarChart.setOnClickListener {
            updateToggleUI(binding.sectionTemp.root, true)
            binding.chartTemp.setData(tempData, 40f, Color.parseColor("#3DDC84"), HealthChartView.ChartType.BAR, 2, "°C")
        }
        binding.sectionTemp.btnLineChart.setOnClickListener {
            updateToggleUI(binding.sectionTemp.root, false)
            binding.chartTemp.setData(tempData, 40f, Color.parseColor("#3DDC84"), HealthChartView.ChartType.LINE, 2, "°C")
        }
    }

    private fun updateToggleUI(rootView: View, isBar: Boolean) {
        val btnBar = rootView.findViewById<ImageView>(R.id.btnBarChart)
        val btnLine = rootView.findViewById<ImageView>(R.id.btnLineChart)
        
        if (isBar) {
            btnBar.setBackgroundResource(R.drawable.bg_button_pink)
            btnBar.setColorFilter(Color.WHITE)
            btnLine.setBackgroundResource(0)
            btnLine.setColorFilter(Color.GRAY)
        } else {
            btnLine.setBackgroundResource(R.drawable.bg_button_pink)
            btnLine.setColorFilter(Color.WHITE)
            btnBar.setBackgroundResource(0)
            btnBar.setColorFilter(Color.GRAY)
        }
    }

    private fun setupPills(container: ViewGroup, items: List<Any>) {
        container.removeAllViews()
        for (item in items) {
            val pillView = LayoutInflater.from(requireContext()).inflate(R.layout.item_stats_pill_flat, container, false)
            val tvText = pillView.findViewById<TextView>(R.id.tvPillText)
            val ivIcon = pillView.findViewById<ImageView>(R.id.ivPillIcon)
            val tvEmoji = pillView.findViewById<TextView>(R.id.tvPillEmoji)
            val flBg = pillView.findViewById<View>(R.id.flIconBg)

            if (item is Triple<*, *, *>) {
                tvText.text = item.first as String
                val icon = item.second
                if (icon is Int) {
                    ivIcon.setImageResource(icon)
                    ivIcon.visibility = View.VISIBLE
                    tvEmoji.visibility = View.GONE
                    
                    if (icon == R.drawable.ic_drop_1 || icon == R.drawable.ic_drop_2 || icon == R.drawable.ic_drop_3) {
                        ivIcon.setColorFilter(Color.parseColor("#FF5C7A"))
                    } else {
                        ivIcon.clearColorFilter()
                    }
                } else if (icon is String) {
                    tvEmoji.text = icon
                    tvEmoji.visibility = View.VISIBLE
                    ivIcon.visibility = View.GONE
                }
                
                flBg.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor(item.third as String))
            }
            container.addView(pillView)
        }
    }

    private fun setupClickListeners() {
        binding.btnMenu.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.menu.add("Bantuan")
            
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Bantuan" -> startActivity(Intent(requireContext(), HelpActivity::class.java))
                }
                true
            }
            popup.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
