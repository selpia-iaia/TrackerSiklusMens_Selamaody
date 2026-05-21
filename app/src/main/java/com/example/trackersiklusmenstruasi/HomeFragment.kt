package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayUserInfo()
        setupClickListeners()
        updateCycleProgress()
    }

    private fun setupClickListeners() {
        binding.cycleContainer.setOnClickListener {
            openPhaseDetail("Period Phase")
        }

        binding.btnMenu.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), it)
            popup.menu.add("Data Pribadi")
            popup.menu.add("Pengaturan")
            popup.menu.add("Bantuan & Masalah")
            popup.menu.add("Tentang Aplikasi")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Data Pribadi" -> {
                        startActivity(Intent(requireContext(), PersonalDataActivity::class.java))
                        true
                    }
                    "Pengaturan" -> {
                        startActivity(Intent(requireContext(), AccountSecurityActivity::class.java))
                        true
                    }
                    "Bantuan & Masalah" -> {
                        Toast.makeText(requireContext(), "Pusat Bantuan: Hubungi sela@support.com", Toast.LENGTH_LONG).show()
                        true
                    }
                    "Tentang Aplikasi" -> {
                        showAboutDialog()
                        true
                    }
                    else -> false
                }
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
            startActivity(Intent(requireContext(), LogEntryActivity::class.java))
        }

        binding.cardGrowth.setOnClickListener {
            openPhaseDetail("Growth Phase")
        }
    }

    private fun showAboutDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Tentang Aplikasi")
            .setMessage("Tracker Siklus Menstruasi v1.2\nDirancang khusus untuk membantu Anda melacak kesehatan reproduksi dengan mudah.\n\n© 2026 Selpia App Studio")
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun openPhaseDetail(phaseName: String) {
        val intent = Intent(requireContext(), PhaseDetailActivity::class.java)
        intent.putExtra("PHASE", phaseName)
        startActivity(intent)
    }

    private fun displayUserInfo() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val userProfile = dbHelper.getUserProfile()
        
        userProfile?.let {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            
            val greetingPrefix = when {
                hour in 0..11 -> "Hi, Selamat Pagi"
                hour in 12..15 -> "Hi, Selamat Siang"
                hour in 16..18 -> "Hi, Selamat Sore"
                else -> "Hi, Selamat Malam"
            }
            
            binding.tvGreeting.text = greetingPrefix
            binding.tvWelcome.text = "${it.name} Cantik 👋"
        }
    }

    private fun updateCycleProgress() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val user = dbHelper.getUserProfile() ?: return
        
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val lastPeriodDate = sdf.parse(user.lastPeriod) ?: Date()
            val today = Date()
            
            // Calculate total days passed since last period
            val diff = today.time - lastPeriodDate.time
            val daysPassed = (diff / (1000 * 60 * 60 * 24)).toInt()
            
            val cycleLength = user.cycleLength
            val currentDayInCycle = (daysPassed % cycleLength) + 1
            
            // 1. Update Ovulation Circle (Progress)
            val progress = currentDayInCycle.toFloat() / cycleLength.toFloat()
            binding.cycleProgress.setProgress(progress)
            
            // 2. Calculate Ovulation Day (typically cycleLength - 14)
            val ovulationDay = cycleLength - 14
            val daysToOvulation = ovulationDay - currentDayInCycle
            
            val ovulationText = when {
                daysToOvulation > 0 -> "$daysToOvulation"
                daysToOvulation == 0 -> "Hari Ini"
                else -> "${daysToOvulation + cycleLength}"
            }
            
            binding.tvOvulationDays.text = ovulationText
            
            // 3. Update Days Left message
            val daysUntilNext = cycleLength - currentDayInCycle
            binding.tvDaysLeft.text = "Tersisa $daysUntilNext hari lagi"
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
