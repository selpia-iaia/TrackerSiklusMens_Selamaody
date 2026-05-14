package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentHomeBinding

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
    }

    private fun setupClickListeners() {
        binding.cycleContainer.setOnClickListener {
            openPhaseDetail("Period Phase")
        }

        binding.btnMenu.setOnClickListener {
            val popup = android.widget.PopupMenu(requireContext(), it)
            popup.menu.add("Pengaturan")
            popup.menu.add("Bantuan & Masukan")
            popup.menu.add("Tentang Aplikasi")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Pengaturan" -> {
                        // For now, let's open the profile as settings placeholder
                        val intent = Intent(requireContext(), ProfileSetupActivity::class.java)
                        startActivity(intent)
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

    private fun openPhaseDetail(phaseName: String) {
        val intent = Intent(requireContext(), PhaseDetailActivity::class.java)
        intent.putExtra("PHASE", phaseName)
        startActivity(intent)
    }

    private fun displayUserInfo() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val userProfile = dbHelper.getUserProfile()
        
        userProfile?.let {
            binding.tvWelcome.text = "${it.name} 👋"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
