package com.example.trackersiklusmenstruasi

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trackersiklusmenstruasi.databinding.ActivityReminderAlertBinding
import java.util.*

class ReminderAlertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderAlertBinding
    private lateinit var audioManager: AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private var selectedHour = 2
    private var selectedMinute = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Ensure status bar is transparent and icons are dark
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding = ActivityReminderAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.header.setPadding(binding.header.paddingLeft, systemBars.top, binding.header.paddingRight, binding.header.paddingBottom)
            insets
        }
        
        setupVolumeControl()
        setupListeners()
    }
    
    private fun setupVolumeControl() {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        
        binding.sliderVolume.valueFrom = 0f
        binding.sliderVolume.valueTo = maxVolume.toFloat()
        binding.sliderVolume.value = currentVolume.toFloat()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scheduleReminder()
                showToast("Pengingat Harian Aktif")
            } else {
                cancelReminder()
                showToast("Pengingat Harian Nonaktif")
            }
        }
        
        binding.rowSchedule.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
                val timeFormat = String.format(Locale.getDefault(), "%02d:%02d%s", displayHour, minute, amPm)
                binding.tvReminderTime.text = timeFormat
                
                if (binding.switchDailyReminder.isChecked) {
                    scheduleReminder()
                }
                showToast("Jadwal diatur ke $timeFormat")
            }
            TimePickerDialog(this, timeSetListener, selectedHour, selectedMinute, false).show()
        }
        
        binding.rowRingtone.setOnClickListener {
            val options = arrayOf("Light Rain", "Morning Dew", "Soft Bell", "Nature Birds", "Default")
            AlertDialog.Builder(this)
                .setTitle("Pilih Suara Dering")
                .setItems(options) { _, which ->
                    binding.tvRingtoneName.text = options[which]
                    playPreviewSound()
                    showToast("Suara diubah ke ${options[which]}")
                }
                .show()
        }
        
        binding.sliderVolume.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, value.toInt(), 0)
            }
        }

        // Play preview sound only when the user stops touching the slider to avoid stuttering
        binding.sliderVolume.addOnSliderTouchListener(object : com.google.android.material.slider.Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: com.google.android.material.slider.Slider) {}
            override fun onStopTrackingTouch(slider: com.google.android.material.slider.Slider) {
                playPreviewSound()
            }
        })
        
        binding.switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            val prefs = getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("vibrate", isChecked).apply()
            showToast("Mode Getar: ${if(isChecked) "Aktif" else "Nonaktif"}")
        }
    }

    private fun scheduleReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 1001, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 1001, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun playPreviewSound() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        if (currentVolume <= 0) return // Jangan bersuara jika volume nol

        try {
            mediaPlayer?.release()
            
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@ReminderAlertActivity, notificationUri)
                setAudioStreamType(AudioManager.STREAM_NOTIFICATION) // Ikuti volume notifikasi
                prepare()
                start()
                setOnCompletionListener { release() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
