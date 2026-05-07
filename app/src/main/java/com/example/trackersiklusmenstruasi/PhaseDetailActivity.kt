package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityPhaseDetailBinding

class PhaseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhaseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }

        val phase = intent.getStringExtra("PHASE") ?: "Period Phase"
        setupPhaseContent(phase)
    }

    private fun setupPhaseContent(phase: String) {
        binding.tvPhaseName.text = phase
        
        when (phase) {
            "Period Phase" -> {
                binding.tvPhaseStatus.text = "Current Phase"
                binding.tvGoingOnDesc.text = "Your cycle begins with shedding the uterine lining."
                binding.tvSignalsDesc.text = "Common symptoms include cramps, fatigue, mood swings, and back pain."
                binding.tvLikelihoodDesc.text = "Fertility is at its lowest as your body sheds the egg."
            }
            "Growth Phase" -> {
                binding.tvPhaseStatus.text = "Future phase"
                binding.tvGoingOnDesc.text = "Follicles mature, producing one dominant egg."
                binding.tvSignalsDesc.text = "You may notice higher energy, clearer skin, and an uplifted mood."
                binding.tvLikelihoodDesc.text = "Increasing as your body prepares for ovulation."
            }
            "Release Phase" -> {
                binding.tvPhaseStatus.text = "Future phase"
                binding.tvGoingOnDesc.text = "Ovulation releases a mature egg, ready for fertilization."
                binding.tvSignalsDesc.text = "Slight temp rise, more mucus, and increased desire."
                binding.tvLikelihoodDesc.text = "Highest you're in your most fertile days."
            }
            "Progesterone Phase" -> {
                binding.tvPhaseStatus.text = "Future phase"
                binding.tvGoingOnDesc.text = "After ovulation, the body prepares for pregnancy or starts a new cycle."
                binding.tvSignalsDesc.text = "PMS may bring bloating, breast soreness, mood swings, and cravings."
                binding.tvLikelihoodDesc.text = "Low fertility. Your fertile window has ended for this cycle."
            }
        }
    }
}
