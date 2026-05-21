package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    // State for selected date and currently displayed month
    private var selectedDay = 11
    private var currentCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2026)
        set(Calendar.MONTH, Calendar.MARCH)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        displayUserInfo()
        updateCalendarUI()

        binding.btnEditPeriod.setOnClickListener {
            startActivity(Intent(requireContext(), EditPeriodActivity::class.java))
        }

        binding.btnSuntingSiklus.setOnClickListener {
            startActivity(Intent(requireContext(), WaterIntakeActivity::class.java))
        }

        // Navigation between months
        binding.btnPrevMonth.setOnClickListener {
            changeMonth(-1)
        }

        binding.btnNextMonth.setOnClickListener {
            changeMonth(1)
        }
    }

    private fun changeMonth(delta: Int) {
        currentCalendar.add(Calendar.MONTH, delta)
        
        // Ensure selectedDay doesn't exceed new month's max days
        val maxDays = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (selectedDay > maxDays) {
            selectedDay = maxDays
        }
        
        updateCalendarUI()
    }

    private fun updateCalendarUI() {
        val localeID = Locale("in", "ID") // Indonesia locale
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", localeID)
        binding.tvMonthYear.text = monthYearFormat.format(currentCalendar.time)

        setupGrid()
        
        // Update Bottom Card Info
        val monthNameFormat = SimpleDateFormat("MMMM", localeID)
        val monthName = monthNameFormat.format(currentCalendar.time)
        binding.tvSelectedDate.text = "$monthName $selectedDay"
        
        // Update "Siklus Hari X" logic
        updateCycleDayDisplay()

        // --- LOGIKA KEMUNGKINAN HAMIL (TINGGI JIKA TELAT 2 MINGGU) ---
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile()
        var likelihood = "Rendah"

        profile?.let {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                // Tanggal haid terakhir (Default dummy: 2026-03-07)
                val lastPeriodDate = sdf.parse(it.lastPeriod) ?: Date()
                
                // 1. Hitung Perkiraan Haid Berikutnya (Misal: 7 Maret + 28 hari = 4 April)
                val nextPeriodCal = Calendar.getInstance()
                nextPeriodCal.time = lastPeriodDate
                nextPeriodCal.add(Calendar.DAY_OF_YEAR, it.cycleLength)
                
                // 2. Hitung Batas Telat 2 Minggu (4 April + 14 hari = 18 April)
                val lateLimitCal = nextPeriodCal.clone() as Calendar
                lateLimitCal.add(Calendar.DAY_OF_YEAR, 14)

                // 3. Tanggal yang Sedang Dipilih di Kalender
                val selectedDateCal = currentCalendar.clone() as Calendar
                selectedDateCal.set(Calendar.DAY_OF_MONTH, selectedDay)
                
                // Reset jam/menit agar perbandingan hanya tanggal
                selectedDateCal.set(Calendar.HOUR_OF_DAY, 0)
                selectedDateCal.set(Calendar.MINUTE, 0)
                selectedDateCal.set(Calendar.SECOND, 0)
                selectedDateCal.set(Calendar.MILLISECOND, 0)
                
                lateLimitCal.set(Calendar.HOUR_OF_DAY, 0)
                lateLimitCal.set(Calendar.MINUTE, 0)
                lateLimitCal.set(Calendar.SECOND, 0)
                lateLimitCal.set(Calendar.MILLISECOND, 0)

                // Jika tanggal dipilih >= batas telat 2 minggu
                if (!selectedDateCal.before(lateLimitCal)) {
                    likelihood = "Tinggi"
                } else {
                    // Logika visual untuk hari-hari biasa
                    likelihood = when (selectedDay) {
                        in 16..19 -> "Sedang"
                        in 20..25 -> "Tinggi"
                        else -> "Rendah"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Update teks status secara langsung menggunakan ID
        binding.tvLikelihoodStatus.text = likelihood
    }

    private fun updateCycleDayDisplay() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val month = currentCalendar.get(Calendar.MONTH)
        val year = currentCalendar.get(Calendar.YEAR)
        val periods = dbHelper.getPeriodsForMonth(month, year)
        
        if (periods.isNotEmpty()) {
            val period = periods.first()
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDate = sdf.parse(period.startDate)!!
                val startCal = Calendar.getInstance().apply { time = startDate }
                
                val selectedCal = currentCalendar.clone() as Calendar
                selectedCal.set(Calendar.DAY_OF_MONTH, selectedDay)
                
                if (!selectedCal.before(startCal)) {
                    val diff = selectedCal.timeInMillis - startCal.timeInMillis
                    val days = (diff / (24 * 60 * 60 * 1000)).toInt() + 1
                    binding.tvCycleDay.text = "Siklus Hari $days"
                    return
                }
            } catch (e: Exception) {}
        }
        binding.tvCycleDay.text = "Bukan masa siklus"
    }

    private fun displayUserInfo() {
        val dbHelper =  DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile()
        profile?.let {
            binding.tvUserName.text = it.name
        }
    }

    private fun setupGrid() {
        val grid = binding.gridCalendar
        grid.removeAllViews()
        val dayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        
        for (dayName in dayNames) {
            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
            tv.text = dayName
            tv.setTextColor(resources.getColor(R.color.gray, null))
            grid.addView(tv)
        }

        // Get info for the current month
        val tempCal = currentCalendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        
        var firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 2
        if (firstDayOfWeek < 0) firstDayOfWeek = 6 // Sunday

        val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Get periods from database for highlighting
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val periods = dbHelper.getPeriodsForMonth(currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.YEAR))
        var startDay = -1
        var endDay = -1
        if (periods.isNotEmpty()) {
            val p = periods.first()
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                startDay = Calendar.getInstance().apply { time = sdf.parse(p.startDate)!! }.get(Calendar.DAY_OF_MONTH)
                endDay = Calendar.getInstance().apply { time = sdf.parse(p.endDate)!! }.get(Calendar.DAY_OF_MONTH)
            } catch (e: Exception) {}
        }

        // Empty spaces
        for (i in 0 until firstDayOfWeek) {
            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
            tv.text = ""
            grid.addView(tv)
        }

        for (i in 1..daysInMonth) {
            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
            tv.text = i.toString()
            
            // Priority highlighting
            if (i == selectedDay) {
                // Pink circle for selected day (active/moving)
                tv.setBackgroundResource(R.drawable.bg_button_pink)
                tv.setTextColor(resources.getColor(R.color.white, null))
            } else if (startDay != -1 && endDay != -1 && i in startDay..endDay) {
                // Pink text for period range
                tv.setTextColor(resources.getColor(R.color.pink_main, null))
            }

            tv.setOnClickListener {
                selectedDay = i
                updateCalendarUI()
            }
            
            grid.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
