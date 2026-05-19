package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
                R.drawable.ic_onboarding_flower,
                R.drawable.ic_onboarding_flower
            ),
            OnboardingItem(
                R.drawable.ic_onboarding_drop,
                "Pelacakan Tanpa Ribet, Semua dalam Satu Tempat",
                "S'maody, melacak siklus menstruasi Anda tidak pernah semudah ini. Catat periode menstruasi dan gejalanya dengan mudah.",
                R.drawable.ic_onboarding_small_drop,
                R.drawable.ic_onboarding_calendar_icon
            ),
            OnboardingItem(
                R.drawable.ic_onboarding_info,
                "Tetap Terinformasi, Tetap Berdaya",
                "Jelajahi beragam artikel dan tips tentang kesehatan menstruasi, gaya hidup, dan kesejahteraan."
            )
        )

        binding.viewPager.adapter = OnboardingAdapter(list)

        binding.btnSkip.setOnClickListener {
            SessionManager(this).setOnboardingComplete(true)
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < list.size - 1) {
                binding.viewPager.currentItem += 1
            } else {
                SessionManager(this).setOnboardingComplete(true)
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })
    }

    private fun updateDots(position: Int) {
        for (i in 0 until binding.dotContainer.childCount) {
            val dot = binding.dotContainer.getChildAt(i)
            val params = dot.layoutParams as android.widget.LinearLayout.LayoutParams
            if (i == position) {
                params.width = (24 * resources.displayMetrics.density).toInt()
                dot.setBackgroundResource(R.drawable.bg_button_pink)
            } else {
                params.width = (8 * resources.displayMetrics.density).toInt()
                dot.setBackgroundColor(android.graphics.Color.parseColor("#D0D0D0"))
            }
            dot.layoutParams = params
        }
    }
}