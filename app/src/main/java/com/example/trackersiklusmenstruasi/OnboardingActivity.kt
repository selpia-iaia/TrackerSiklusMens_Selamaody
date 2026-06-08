package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.trackersiklusmenstruasi.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = listOf(
            OnboardingItem(
                R.drawable.ic_onboarding_uterus,
                "S'maody Pendamping Menstruasi Anda yang Cerdas",
                "S'maody adalah pelacak siklus menstruasi pribadi cerdas Anda yang dirancang untuk membantu Anda memahaminya.",
                secondaryImage = R.drawable.ic_onboarding_flower,
                tertiaryImage = R.drawable.ic_onboarding_flower
            ),
            OnboardingItem(
                R.drawable.ic_onboarding_drop,
                "Pelacakan Tanpa Ribet, Semua dalam Satu Tempat",
                "S'maody, melacak siklus menstruasi Anda tidak pernah semudah ini. Catat periode menstruasi dan gejalanya dengan mudah.",
                secondaryImage = R.drawable.ic_onboarding_small_drop,
                tertiaryImage = R.drawable.ic_onboarding_calendar_icon
            ),
            OnboardingItem(
                R.drawable.ic_onboarding_info,
                "Tetap Terinformasi, Tetap Berdaya",
                "Jelajahi beragam artikel dan tips tentang kesehatan menstruasi, gaya hidup, dan kesejahteraan."
            )
        )

        binding.viewPager.adapter = OnboardingAdapter(list)

        binding.btnBack.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem -= 1
            }
        }

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < list.size - 1) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position, list.size)
                // Tombol back disembunyikan agar sesuai screenshot
                binding.btnBack.visibility = View.INVISIBLE
            }
        })
    }

    private fun finishOnboarding() {
        SessionManager(this).setOnboardingComplete(true)
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }

    private fun updateDots(position: Int, size: Int) {
        binding.dotContainer.removeAllViews()
        for (i in 0 until size) {
            val dot = View(this)
            // Dot aktif lebih panjang (pill), dot tidak aktif bulat
            val widthDp = if (i == position) 24 else 8
            val heightDp = 6
            
            val params = LinearLayout.LayoutParams(
                (widthDp * resources.displayMetrics.density).toInt(),
                (heightDp * resources.displayMetrics.density).toInt()
            )
            params.setMargins(
                (4 * resources.displayMetrics.density).toInt(),
                0,
                (4 * resources.displayMetrics.density).toInt(),
                0
            )
            dot.layoutParams = params
            dot.setBackgroundResource(R.drawable.bg_dot_active)
            dot.alpha = if (i == position) 1.0f else 0.15f
            binding.dotContainer.addView(dot)
        }
    }
}
