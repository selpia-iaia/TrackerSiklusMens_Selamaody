package com.example.trackersiklusmenstruasi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCustomNavigation()

        // Load default fragment
        val target = intent.getStringExtra("TARGET_FRAGMENT")
        when (target) {
            "CALENDAR" -> {
                loadFragment(CalendarFragment())
                setActiveNavItem(1)
            }
            "STATS" -> {
                loadFragment(StatsFragment())
                setActiveNavItem(2)
            }
            "PROFILE" -> {
                loadFragment(ProfileFragment())
                setActiveNavItem(3)
            }
            else -> {
                loadFragment(HomeFragment())
                setActiveNavItem(0)
            }
        }

        // Simpan data user dummy jika database kosong
        val dbHelper = DatabaseHelper.getInstance(this)
        if (dbHelper.getUserProfile() == null) {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val lastPeriod = java.util.Calendar.getInstance()
            lastPeriod.add(java.util.Calendar.DAY_OF_YEAR, -5) // 5 days ago
            
            dbHelper.saveUserProfile("Sela", "2006-04-15", 57.0, 167.0, 5, 28, sdf.format(lastPeriod.time))
        }
    }

    private fun setupCustomNavigation() {
        val nav = binding.customBottomNav.root
        nav.findViewById<View>(R.id.btnNavHome).setOnClickListener {
            loadFragment(HomeFragment())
            setActiveNavItem(0)
        }
        nav.findViewById<View>(R.id.btnNavCalendar).setOnClickListener {
            loadFragment(CalendarFragment())
            setActiveNavItem(1)
        }
        nav.findViewById<View>(R.id.btnNavStats).setOnClickListener {
            loadFragment(StatsFragment())
            setActiveNavItem(2)
        }
        nav.findViewById<View>(R.id.btnNavProfile).setOnClickListener {
            loadFragment(ProfileFragment())
            setActiveNavItem(3)
        }
    }

    private fun setActiveNavItem(index: Int) {
        val nav = binding.customBottomNav.root
        val items = listOf(
            R.id.btnNavHome to R.id.ivNavHome,
            R.id.btnNavCalendar to R.id.ivNavCalendar,
            R.id.btnNavStats to R.id.ivNavStats,
            R.id.btnNavProfile to R.id.ivNavProfile
        )

        items.forEachIndexed { i, (btnId, ivId) ->
            val iv = nav.findViewById<ImageView>(ivId)
            
            // Get the background view ID
            val bgViewId = when(btnId) {
                R.id.btnNavHome -> R.id.vNavBgHome
                R.id.btnNavCalendar -> R.id.vNavBgCalendar
                R.id.btnNavStats -> R.id.vNavBgStats
                R.id.btnNavProfile -> R.id.vNavBgProfile
                else -> -1
            }
            val bgView = nav.findViewById<View>(bgViewId)
            
            if (i == index) {
                bgView?.setBackgroundResource(R.drawable.bg_circle_pink)
                iv.setColorFilter(Color.WHITE)
            } else {
                bgView?.setBackgroundResource(R.drawable.bg_circle_white)
                iv.setColorFilter(Color.BLACK)
            }
            bgView?.elevation = 0f
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
