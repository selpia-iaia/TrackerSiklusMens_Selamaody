package com.example.trackersiklusmenstruasi

import android.app.TimePickerDialog
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
            showToast("Pengingat Harian: ${if(isChecked) "Aktif" else "Nonaktif"}")
        }
        
        binding.rowSchedule.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
                val timeFormat = String.format(Locale.getDefault(), "%02d:%02d%s", displayHour, minute, amPm)
                binding.tvReminderTime.text = timeFormat
                showToast("Jadwal diatur ke $timeFormat")
            }
            TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
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
            showToast("Mode Getar: ${if(isChecked) "Aktif" else "Nonaktif"}")
        }
    }

    private fun playPreviewSound() {
        try {
            // Releasing previous media player
            mediaPlayer?.release()
            
            // Using default notification sound for preview
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer.create(this, notificationUri)
            mediaPlayer?.start()
            
            // Auto release after sound finished
            mediaPlayer?.setOnCompletionListener { it.release() }
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
