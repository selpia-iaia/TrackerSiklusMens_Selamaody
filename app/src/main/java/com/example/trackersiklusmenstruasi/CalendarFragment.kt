package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

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
        setupGrid()

        binding.btnEditPeriod.setOnClickListener {
            startActivity(Intent(requireContext(), EditPeriodActivity::class.java))
        }

        binding.btnSuntingSiklus.setOnClickListener {
            startActivity(Intent(requireContext(), WaterIntakeActivity::class.java))
        }
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

        val daysInMonth = 30
        for (i in 1..daysInMonth) {
            val tv = LayoutInflater.from(requireContext()).inflate(R.layout.item_calendar_grid_day, grid, false) as TextView
            tv.text = i.toString()
            
            // Match screenshot 37: Highlight for March 11
            if (i == 11) {
                tv.setBackgroundResource(R.drawable.bg_button_pink)
                tv.setTextColor(resources.getColor(R.color.white, null))
                
                // Add a small white dot inside the pink circle
                // We could use a specific drawable for this if needed
            }
            
            grid.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
