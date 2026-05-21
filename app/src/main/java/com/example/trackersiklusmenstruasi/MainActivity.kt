package com.example.trackersiklusmenstruasi

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
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize DB Singleton and open it at startup
        DatabaseHelper.getInstance(this).writableDatabase

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Add extra bottom padding to container so content is not hidden by floating nav
        binding.fragmentContainer.setPadding(0, 0, 0, 300)

        setupCustomNavigation()
        
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            setActiveNavItem(0)
        }
    }

    private fun setupCustomNavigation() {
        binding.customBottomNav.root.findViewById<View>(R.id.btnNavHome).setOnClickListener {
            loadFragment(HomeFragment())
            setActiveNavItem(0)
        }
        binding.customBottomNav.root.findViewById<View>(R.id.btnNavCalendar).setOnClickListener {
            loadFragment(CalendarFragment())
            setActiveNavItem(1)
        }
        binding.customBottomNav.root.findViewById<View>(R.id.btnNavStats).setOnClickListener {
            loadFragment(StatsFragment())
            setActiveNavItem(2)
        }
        binding.customBottomNav.root.findViewById<View>(R.id.btnNavProfile).setOnClickListener {
            // Membuka SettingsActivity yang baru dibuat daripada ProfileFragment
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
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
            val btn = nav.findViewById<FrameLayout>(btnId)
            val iv = nav.findViewById<ImageView>(ivId)
            
            // Mencari view background lingkaran di dalam FrameLayout
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
                iv.setColorFilter(resources.getColor(R.color.white, null))
            } else {
                bgView?.setBackgroundResource(R.drawable.bg_circle_white)
                iv.setColorFilter(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
