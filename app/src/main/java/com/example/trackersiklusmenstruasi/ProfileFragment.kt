package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadUserProfile()
        
        binding.btnLogout.setOnClickListener {
            SessionManager(requireContext()).logout()
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile()
        
        profile?.let {
            binding.tvProfileName.text = it.name
            binding.tvWeight.text = "${it.weight.toInt()} Kg"
            binding.tvHeight.text = "${it.height.toInt()} Cm"
            binding.tvPeriodLen.text = "${it.periodLength} Hari"
            binding.tvCycleLen.text = "${it.cycleLength} Hari"
            
            // Calculate age from birthday (yyyy-MM-dd)
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val birthDate = sdf.parse(it.birthday)
                if (birthDate != null) {
                    val today = Calendar.getInstance()
                    val birth = Calendar.getInstance()
                    birth.time = birthDate
                    var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
                    if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    binding.tvAge.text = age.toString()
                }
            } catch (e: Exception) {
                binding.tvAge.text = "-"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
