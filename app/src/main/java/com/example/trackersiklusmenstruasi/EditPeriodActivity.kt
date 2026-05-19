package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ActivityEditPeriodBinding
import com.example.trackersiklusmenstruasi.databinding.ItemEditCalendarMonthBinding
import java.text.SimpleDateFormat
import java.util.*

class EditPeriodActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEditPeriodBinding
    
    // Data class to hold selection for each month
    data class MonthSelection(var startDay: Int = -1, var endDay: Int = -1)
    
    // Store selections using key: "MONTH_YEAR" (e.g., "2_2026" for March 2026)
    private val monthSelections = mutableMapOf<String, MonthSelection>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPeriodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.rvCalendars.layoutManager = LinearLayoutManager(this)
        binding.rvCalendars.adapter = CalendarAdapter()

        binding.btnSave.setOnClickListener {
            Toast.makeText(this, "Semua rentang periode berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    inner class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

        private val baseCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.MARCH)
        }

        inner class ViewHolder(val binding: ItemEditCalendarMonthBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemEditCalendarMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val monthCal = baseCalendar.clone() as Calendar
            monthCal.add(Calendar.MONTH, position)
            
            val localeID = Locale("in", "ID")
            val monthYearFormat = SimpleDateFormat("MMMM yyyy", localeID)
            holder.binding.tvMonthYear.text = monthYearFormat.format(monthCal.time)
            
            val month = monthCal.get(Calendar.MONTH)
            val year = monthCal.get(Calendar.YEAR)
            val monthKey = "${month}_${year}"
            
            // Initialize from database if first time seeing this month in session
            if (!monthSelections.containsKey(monthKey)) {
                val dbHelper = DatabaseHelper.getInstance(this@EditPeriodActivity)
                val periods = dbHelper.getPeriodsForMonth(month, year)
                if (periods.isNotEmpty()) {
                    val first = periods.first()
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val start = Calendar.getInstance().apply { time = sdf.parse(first.startDate)!! }
                        val end = Calendar.getInstance().apply { time = sdf.parse(first.endDate)!! }
                        monthSelections[monthKey] = MonthSelection(start.get(Calendar.DAY_OF_MONTH), end.get(Calendar.DAY_OF_MONTH))
                    } catch (e: Exception) {
                        monthSelections[monthKey] = MonthSelection(-1, -1)
                    }
                } else {
                    monthSelections[monthKey] = MonthSelection(-1, -1)
                }
            }
            
            setupGrid(holder.binding, monthCal, monthKey)
        }

        private fun setupGrid(itemBinding: ItemEditCalendarMonthBinding, monthCal: Calendar, monthKey: String) {
            val grid = itemBinding.gridDays
            grid.removeAllViews()
            val dayNames = listOf("S", "M", "T", "W", "T", "F", "S")
            
            for (dayName in dayNames) {
                val tv = LayoutInflater.from(this@EditPeriodActivity).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
                tv.text = dayName
                tv.setTextColor(resources.getColor(R.color.gray, null))
                grid.addView(tv)
            }

            val tempCal = monthCal.clone() as Calendar
            tempCal.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOffset = tempCal.get(Calendar.DAY_OF_WEEK) - 1
            val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Empty slots
            for (i in 0 until firstDayOffset) {
                val tv = LayoutInflater.from(this@EditPeriodActivity).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
                tv.text = ""
                grid.addView(tv)
            }

            val selection = monthSelections[monthKey] ?: MonthSelection()

            for (i in 1..daysInMonth) {
                val tv = LayoutInflater.from(this@EditPeriodActivity).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
                tv.text = i.toString()
                
                // Highlight logic for the current month's selection
                if (selection.startDay != -1 && selection.endDay != -1 && i in selection.startDay..selection.endDay) {
                    tv.setTextColor(resources.getColor(R.color.white, null))
                    when (i) {
                        selection.startDay -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_start)
                        selection.endDay -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_end)
                        else -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_mid)
                    }
                } else if (selection.startDay != -1 && i == selection.startDay) {
                    // Single day selected
                    tv.setTextColor(resources.getColor(R.color.white, null))
                    tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_start)
                }

                tv.setOnClickListener {
                    if (selection.startDay == -1 || (selection.startDay != -1 && selection.endDay != -1)) {
                        // First click: Start a new selection
                        selection.startDay = i
                        selection.endDay = -1
                    } else if (i < selection.startDay) {
                        // Clicked day is before current start: Update start
                        selection.startDay = i
                    } else {
                        // Clicked day is after start: Set end day
                        selection.endDay = i
                        
                        // AUTO SAVE when range is complete
                        saveMonthToDb(monthCal, selection)
                    }
                    notifyDataSetChanged()
                }
                
                grid.addView(tv)
            }
        }

        private fun saveMonthToDb(monthCal: Calendar, selection: MonthSelection) {
            val dbHelper = DatabaseHelper.getInstance(this@EditPeriodActivity)
            val month = monthCal.get(Calendar.MONTH)
            val year = monthCal.get(Calendar.YEAR)
            
            dbHelper.deletePeriodsForMonth(month, year)
            
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startCal = monthCal.clone() as Calendar
            startCal.set(Calendar.DAY_OF_MONTH, selection.startDay)
            val endCal = monthCal.clone() as Calendar
            endCal.set(Calendar.DAY_OF_MONTH, selection.endDay)
            
            dbHelper.savePeriod(sdf.format(startCal.time), sdf.format(endCal.time))
            Toast.makeText(this@EditPeriodActivity, "Tersimpan otomatis", Toast.LENGTH_SHORT).show()
        }

        override fun getItemCount(): Int = 12 // Showing 1 year of months
    }
}
