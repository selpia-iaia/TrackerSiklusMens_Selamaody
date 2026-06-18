package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        setupCalendarDynamic()
        updateCycleLogic()
        
        // Cek Notifikasi Pop-up otomatis
        checkPopUpAnnouncement()
    }

    private fun checkPopUpAnnouncement() {
        lifecycleScope.launch {
            try {
                delay(2000) // Tunggu 2 detik
                val apiService = ApiService.create()
                val list = apiService.getAnnouncements()
                
                if (list.isNotEmpty()) {
                    val latest = list[0]
                    val dbHelper = DatabaseHelper.getInstance(requireContext())
                    val userName = dbHelper.getUserProfile()?.name ?: "Sela"
                    
                    val fTitle = latest.title.replace("{name}", userName, ignoreCase = true)
                    val fMsg = latest.message.replace("{name}", userName, ignoreCase = true)
                    
                    if (isAdded) { // Pastikan fragment masih aktif
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("📢 $fTitle")
                            .setMessage(fMsg)
                            .setPositiveButton("Tutup", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Error PopUp: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateGreeting()
        updateCycleLogic()
        setupCalendarDynamic()
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

    private fun updateCycleLogic() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile() ?: return

        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val lastPeriodDate = sdf.parse(profile.last_period) ?: return
            val today = Calendar.getInstance()
            
            val diff = today.timeInMillis - lastPeriodDate.time
            val daysSinceLastPeriod = (diff / (24 * 60 * 60 * 1000)).toInt()
            
            val cycleLength = if (profile.cycle_length > 0) profile.cycle_length else 28
            val currentDayInCycle = (daysSinceLastPeriod % cycleLength) + 1
            
            val progress = currentDayInCycle.toFloat() / cycleLength
            binding.cycleProgress.setProgress(progress)

            val ovulationStartDay = (cycleLength / 2) - 3 
            val nextPeriodDay = cycleLength + 1
            
            val statusLabel: String
            val daysValue: String
            val footerLabel: String

            when {
                currentDayInCycle <= profile.period_length -> {
                    statusLabel = "Sedang Haid"
                    daysValue = currentDayInCycle.toString()
                    footerLabel = "Hari ke-$currentDayInCycle"
                }
                currentDayInCycle < ovulationStartDay -> {
                    statusLabel = "Ovulasi masuk"
                    val countdown = ovulationStartDay - currentDayInCycle
                    daysValue = countdown.toString()
                    footerLabel = "Tersisa $countdown hari lagi"
                }
                currentDayInCycle <= ovulationStartDay + 6 -> {
                    statusLabel = "Masa Subur"
                    daysValue = "Puncak"
                    footerLabel = "Fase Ovulasi"
                }
                else -> {
                    statusLabel = "Haid masuk"
                    val countdown = nextPeriodDay - currentDayInCycle
                    daysValue = countdown.toString()
                    footerLabel = "Tersisa $countdown hari lagi"
                }
            }

            binding.tvCycleStatus.text = statusLabel
            binding.tvOvulationDays.text = daysValue
            binding.tvDaysLeft.text = footerLabel

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        binding.btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
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

    private fun setupCalendarDynamic() {
        val calendar = Calendar.getInstance()
        val sdfNumber = java.text.SimpleDateFormat("dd", Locale.getDefault())
        val sdfLetter = java.text.SimpleDateFormat("EEEEE", Locale.getDefault())

        val calendarLayout = binding.calendarStrip
        val tempCal = Calendar.getInstance()
        tempCal.add(Calendar.DATE, -2)

        for (i in 0 until calendarLayout.childCount) {
            val dayView = calendarLayout.getChildAt(i)
            val tvLetter = dayView.findViewById<TextView>(R.id.tvDayLetter)
            val tvNumber = dayView.findViewById<TextView>(R.id.tvDayNumber)
            
            val isToday = (tempCal.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
                           tempCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))

            tvLetter.text = sdfLetter.format(tempCal.time)
            tvNumber.text = sdfNumber.format(tempCal.time)
            
            if (isToday) {
                tvNumber.setBackgroundResource(R.drawable.bg_calendar_day_selected)
                tvNumber.setTextColor(resources.getColor(R.color.white, null))
                tvLetter.setTextColor(resources.getColor(R.color.pink_main, null))
            } else {
                tvNumber.setBackgroundResource(R.drawable.bg_calendar_day_unselected)
                tvNumber.setTextColor(resources.getColor(R.color.black, null))
                tvLetter.setTextColor(resources.getColor(R.color.gray, null))
            }
            
            tempCal.add(Calendar.DATE, 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
