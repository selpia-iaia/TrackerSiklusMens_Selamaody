package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentHomeBinding
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupCalendarDummy()
        
        // Setup Progress Siklus (Dummy untuk visualitas)
        binding.cycleProgress.setProgress(0.65f)
        binding.tvOvulationDays.text = "3"
        binding.tvDaysLeft.text = "Tersisa 10 hari lagi"
    }

    override fun onResume() {
        super.onResume()
        updateGreeting()
    }

    private fun updateGreeting() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile()
        val name = profile?.name ?: "Sela"

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when (hour) {
            in 4..10 -> "Hi, Selamat Pagi"
            in 11..14 -> "Hi, Selamat Siang"
            in 15..18 -> "Hi, Selamat Sore"
            else -> "Hi, Selamat Malam"
        }
        
        binding.tvGreeting.text = greeting
        binding.tvWelcome.text = "$name 👋"
    }

    private fun setupClickListeners() {
        binding.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menu.add("Tips Kesehatan").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), HealthTipsActivity::class.java))
                true
            }
            popup.menu.add("Data Pribadi").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), PersonalDataActivity::class.java))
                true
            }
            popup.show()
        }
        binding.btnAddPeriod.setOnClickListener {
            startActivity(Intent(requireContext(), EditPeriodActivity::class.java))
        }
        binding.btnAddSymptoms.setOnClickListener {
            startActivity(Intent(requireContext(), LogEntryActivity::class.java))
        }
        binding.cardFeeling.setOnClickListener {
            startActivity(Intent(requireContext(), MoodSelectionActivity::class.java))
        }
        binding.cardGrowth.setOnClickListener {
            startActivity(Intent(requireContext(), PhaseDetailActivity::class.java))
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
                    tvNumber.setTextColor(resources.getColor(R.color.white, null))
                    tvLetter.setTextColor(resources.getColor(R.color.pink_main, null))
                } else {
                    tvNumber.setBackgroundResource(R.drawable.bg_calendar_day_unselected)
                    tvNumber.setTextColor(resources.getColor(R.color.black, null))
                    tvLetter.setTextColor(resources.getColor(R.color.gray, null))
                }
            } else {
                calendarLayout.getChildAt(i).visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
