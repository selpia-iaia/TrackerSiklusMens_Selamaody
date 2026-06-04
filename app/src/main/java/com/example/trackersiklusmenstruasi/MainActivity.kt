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
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize DB Singleton and open it at startup
        DatabaseHelper.getInstance(this).writableDatabase

        // Trigger Sync to XAMPP
        syncDataToServer()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Add minimal bottom padding so content is not hidden by floating nav
        binding.fragmentContainer.setPadding(0, 0, 0, 120)

        setupCustomNavigation()
        
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            setActiveNavItem(0)
        }
    }

    private fun syncDataToServer() {
        val sessionManager = SessionManager(this)
        var userId = sessionManager.getUserId()
        
        // Jika userId belum ada (dummy), paksa ke ID 1 untuk keperluan testing XAMPP
        if (userId <= 0) {
            userId = 1
            sessionManager.setUserId(1)
        }

        val apiService = ApiService.create()
        val syncRepository = SyncRepository(apiService)
        val dbHelper = DatabaseHelper.getInstance(this)

        lifecycleScope.launch {
            try {
                var allSuccess = true
                android.util.Log.d("SyncDebug", "Memulai sinkronisasi untuk User ID: $userId")
                
                // 1. Sync Profile
                val profile = dbHelper.getUserProfile()
                profile?.let { p ->
                    android.util.Log.d("SyncDebug", "Mengirim Profile: ${p.name}")
                    val res = syncRepository.syncUserProfile(UserProfileModel(
                        userId, p.name, p.birthday, p.weight, p.height, p.period_length, p.cycle_length, p.last_period
                    ))
                    if (!res) {
                        android.util.Log.e("SyncDebug", "Gagal sinkron Profile")
                        allSuccess = false
                    }
                }

                // 2. Sync Daily Logs
                val logs = dbHelper.getAllDailyLogs()
                android.util.Log.d("SyncDebug", "Mengirim ${logs.size} Daily Logs")
                logs.forEach { log ->
                    if (!syncRepository.syncDailyLog(DailyLogModel(userId, log.date, log.flow, log.symptoms, log.moods, log.medicine, log.note, log.weight, log.water, log.temp))) {
                        android.util.Log.e("SyncDebug", "Gagal sinkron Daily Log tanggal: ${log.date}")
                        allSuccess = false
                    }
                }

                // 3. Sync App Settings
                val settings = dbHelper.getAppSettings()
                settings?.let { s ->
                    android.util.Log.d("SyncDebug", "Mengirim App Settings")
                    val res = syncRepository.syncAppSettings(AppSettingsModel(
                        userId, s.weekStart, s.timeFormat, s.waterTarget, s.cupVolume, s.isBmiEnabled
                    ))
                    if (!res) {
                        android.util.Log.e("SyncDebug", "Gagal sinkron App Settings")
                        allSuccess = false
                    }
                }

                // 4. Sync Periods
                val periods = dbHelper.getAllPeriods()
                android.util.Log.d("SyncDebug", "Mengirim ${periods.size} Data Periode")
                periods.forEach { p ->
                    try {
                        if (!syncRepository.syncPeriod(PeriodModel(userId, p.startDate, p.endDate))) {
                            allSuccess = false
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SyncDebug", "Error Period ${p.startDate}: ${e.message}")
                        allSuccess = false
                    }
                }

                // 5. Sync Reminders
                val reminders = dbHelper.getAllReminders()
                android.util.Log.d("SyncDebug", "Mengirim ${reminders.size} Reminders")
                reminders.forEach { r ->
                    try {
                        if (!syncRepository.syncReminder(ReminderModel(userId, r.type, r.time, r.isEnabled))) {
                            allSuccess = false
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SyncDebug", "Error Reminder: ${e.message}")
                        allSuccess = false
                    }
                }

                // 6. Sync Payment Methods
                val payments = dbHelper.getAllPaymentMethods()
                android.util.Log.d("SyncDebug", "Mengirim ${payments.size} Payment Methods")
                payments.forEach { pm ->
                    try {
                        if (!syncRepository.syncPaymentMethod(PaymentMethodModel(userId, pm.provider, pm.accountNumber, pm.holderName))) {
                            allSuccess = false
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SyncDebug", "Error Payment: ${e.message}")
                        allSuccess = false
                    }
                }

                // 7. Sync Connected Accounts
                val accounts = dbHelper.getAllConnectedAccounts()
                android.util.Log.d("SyncDebug", "Mengirim ${accounts.size} Connected Accounts")
                accounts.forEach { ca ->
                    try {
                        if (!syncRepository.syncConnectedAccount(ConnectedAccountModel(userId, ca.provider, ca.email))) {
                            allSuccess = false
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SyncDebug", "Error Account: ${e.message}")
                        allSuccess = false
                    }
                }

                if (allSuccess) {
                    android.util.Log.d("SyncDebug", "Semua data berhasil disinkronkan ke XAMPP")
                    android.widget.Toast.makeText(this@MainActivity, "Data Berhasil Sinkron ke XAMPP!", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.util.Log.w("SyncDebug", "Beberapa data gagal sinkron. Pastikan database XAMPP sudah sesuai.")
                    android.widget.Toast.makeText(this@MainActivity, "Beberapa data gagal sinkron. Cek Logcat!", android.widget.Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncDebug", "Error Fatal Sync: ${e.message}")
                android.widget.Toast.makeText(this@MainActivity, "Sinkronisasi Terhenti: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
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
