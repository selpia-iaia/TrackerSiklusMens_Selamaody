package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackersiklusmenstruasi.databinding.FragmentHomeBinding
import com.example.trackersiklusmenstruasi.PhaseDetailActivity
import com.example.trackersiklusmenstruasi.EditPeriodActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            HealthRepository(ApiService.create()),
            DatabaseHelper.getInstance(requireContext())
        )
    }

    private val adapter by lazy { HealthAdapter() }

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

        setupRecyclerView()
        setupCalendarDummy()
        setupClickListeners()
        observeViewModel()

        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        binding.rvHealthTipsHome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHealthTipsHome.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                updateGreeting(it.name)
                updateCycleUI(it)
            }
        }

        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            adapter.submitList(articles)
        }
    }

    private fun updateGreeting(name: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greetingPrefix = when {
            hour in 0..11 -> "Hi, Selamat Pagi"
            hour in 12..15 -> "Hi, Selamat Siang"
            hour in 16..18 -> "Hi, Selamat Sore"
            else -> "Hi, Selamat Malam"
        }
        
        binding.tvGreeting.text = greetingPrefix
        binding.tvWelcome.text = "${name.split(" ")[0]} 👋"
    }

    private fun updateCycleUI(user: UserProfile) {
        // Logika "Sama Percis" sesuai permintaan gambar
        binding.cycleProgress.setProgress(0.35f)
        binding.tvOvulationDays.text = "3"
        binding.tvDaysLeft.text = "Tersisa 10 hari lagi"
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

    private fun setupClickListeners() {
        binding.btnMenu.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), it)
            popup.menu.add("Data Pribadi")
            popup.menu.add("Pengaturan")
            popup.menu.add("Tips Kesehatan")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Data Pribadi" -> {
                        startActivity(Intent(requireContext(), PersonalDataActivity::class.java))
                        true
                    }
                    "Pengaturan" -> {
                        startActivity(Intent(requireContext(), SettingsActivity::class.java))
                        true
                    }
                    "Tips Kesehatan" -> {
                        startActivity(Intent(requireContext(), HealthTipsActivity::class.java))
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
            startActivity(Intent(requireContext(), MoodSelectionActivity::class.java))
        }

        binding.cardGrowth.setOnClickListener {
            startActivity(Intent(requireContext(), PhaseDetailActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
