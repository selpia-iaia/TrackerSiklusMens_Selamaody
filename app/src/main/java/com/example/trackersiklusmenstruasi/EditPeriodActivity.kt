package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ActivityEditPeriodBinding
import com.example.trackersiklusmenstruasi.databinding.ItemEditCalendarMonthBinding

class EditPeriodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPeriodBinding

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
            finish()
        }
    }

    inner class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

        private val months = listOf("March 2026", "April 2026", "May 2026")

        inner class ViewHolder(val binding: ItemEditCalendarMonthBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemEditCalendarMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.tvMonthYear.text = months[position]
            setupGrid(holder.binding.gridDays, position)
        }

        private fun setupGrid(grid: GridLayout, monthOffset: Int) {
            grid.removeAllViews()
            val dayNames = listOf("S", "M", "T", "W", "T", "F", "S")
            
            for (dayName in dayNames) {
                val tv = LayoutInflater.from(this@EditPeriodActivity).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
                tv.text = dayName
                tv.setTextColor(resources.getColor(R.color.gray, null))
                grid.addView(tv)
            }

            val daysInMonth = 31
            for (i in 1..daysInMonth) {
                val tv = LayoutInflater.from(this@EditPeriodActivity).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
                tv.text = i.toString()
                
                if (monthOffset == 0 && i in 7..11) {
                    tv.setTextColor(resources.getColor(R.color.white, null))
                    when (i) {
                        7 -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_start)
                        11 -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_end)
                        else -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_mid)
                    }
                }
                
                // Highlight for April (dummy)
                if (monthOffset == 1 && i in 14..18) {
                    tv.setTextColor(resources.getColor(R.color.white, null))
                    when (i) {
                        14 -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_start)
                        18 -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_end)
                        else -> tv.setBackgroundResource(R.drawable.bg_calendar_day_selected_mid)
                    }
                }
                
                grid.addView(tv)
            }
        }

        override fun getItemCount(): Int = months.size
    }
}
