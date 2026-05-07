package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.trackersiklusmenstruasi.databinding.ActivityProfileSetupBinding
import com.example.trackersiklusmenstruasi.databinding.ItemProfileStepBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private val steps = listOf(
        "Beritahu Kami\nNama Anda",
        "Beritahu Kami\nUlang Tahun Anda",
        "Beritahu Kami\nBerat Badan Anda",
        "Beritahu Kami\nTinggi Badan Anda",
        "Lama Periode\nMenstruasi Anda",
        "Masukkan Lama\nSiklus Anda",
        "Kapan Tanggal Mulai\nHaid Terakhir Anda?"
    )

    // Data to be saved
    private var userName = ""
    private var birthday = "2006-04-15"
    private var weight = 57.0
    private var height = 167.0
    private var periodLength = 3
    private var cycleLength = 27
    private var lastPeriod = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = StepAdapter(steps)
        binding.viewPager.isUserInputEnabled = false

        binding.btnContinue.setOnClickListener {
            if (binding.viewPager.currentItem < steps.size - 1) {
                binding.viewPager.currentItem += 1
                if (binding.viewPager.currentItem == steps.size - 1) {
                    binding.btnContinue.text = "Selesai"
                }
            } else {
                saveData()
                startActivity(Intent(this, LoadingActivity::class.java))
                finish()
            }
        }

        binding.btnBack.setOnClickListener {
            if (binding.viewPager.currentItem > 0) {
                binding.viewPager.currentItem -= 1
                binding.btnContinue.text = "Lanjutkan"
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateProgress(position)
            }
        })
    }

    private fun saveData() {
        val dbHelper = DatabaseHelper.getInstance(this)
        dbHelper.saveUserProfile(
            userName,
            birthday,
            weight,
            height,
            periodLength,
            cycleLength,
            lastPeriod
        )
    }

    private fun updateProgress(position: Int) {
        binding.tvStepCount.text = "${position + 1}/${steps.size}"
        val progressContainer = binding.progressContainer
        for (i in 0 until progressContainer.childCount) {
            val dot = progressContainer.getChildAt(i)
            dot.alpha = if (i == position) 1.0f else 0.1f
        }
    }

    inner class StepAdapter(private val titles: List<String>) : RecyclerView.Adapter<StepAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemProfileStepBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProfileStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.tvTitle.text = titles[position]
            holder.binding.stepContentContainer.removeAllViews()
            val inflater = LayoutInflater.from(holder.itemView.context)
            
            val contentLayout = when (position) {
                0 -> R.layout.view_setup_name
                1 -> R.layout.view_setup_birthday
                2 -> R.layout.view_setup_weight
                3 -> R.layout.view_setup_height
                4 -> R.layout.view_setup_period_length
                5 -> R.layout.view_setup_cycle_length
                6 -> R.layout.view_setup_last_period
                else -> R.layout.view_setup_name
            }
            
            val view = inflater.inflate(contentLayout, holder.binding.stepContentContainer, true)

            when (position) {
                0 -> { // Name
                    val etName = view.findViewById<EditText>(R.id.etName)
                    etName.addTextChangedListener(object : android.text.TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            userName = s.toString()
                        }
                        override fun afterTextChanged(s: android.text.Editable?) {}
                    })
                }
                1 -> { // Birthday
                    val months = arrayOf("Jan", "Feb", "March", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    val pickerMonth = view.findViewById<NumberPicker>(R.id.pickerMonth)
                    val pickerDay = view.findViewById<NumberPicker>(R.id.pickerDay)
                    val pickerYear = view.findViewById<NumberPicker>(R.id.pickerYear)

                    fun updateBirthday() {
                        birthday = String.format("%04d-%02d-%02d", pickerYear.value, pickerMonth.value + 1, pickerDay.value)
                    }

                    pickerMonth?.apply {
                        minValue = 0
                        maxValue = months.size - 1
                        displayedValues = months
                        value = 3 // April
                        setOnValueChangedListener { _, _, _ -> updateBirthday() }
                    }
                    pickerDay?.apply {
                        minValue = 1
                        maxValue = 31
                        value = 15
                        setOnValueChangedListener { _, _, _ -> updateBirthday() }
                    }
                    pickerYear?.apply {
                        minValue = 1900
                        maxValue = 2024
                        value = 2006
                        setOnValueChangedListener { _, _, _ -> updateBirthday() }
                    }
                    updateBirthday()
                }
                2 -> { // Weight
                    val tvWeightValue = view.findViewById<TextView>(R.id.tvWeightValue)
                    val weightSeekBar = view.findViewById<SeekBar>(R.id.weightSeekBar)
                    val arcRulerView = view.findViewById<ArcRulerView>(R.id.arcRulerView)

                    weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            weight = progress.toDouble()
                            tvWeightValue.text = "$progress"
                            arcRulerView.setValue(progress)
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })
                    
                    // Initial set
                    arcRulerView.setValue(weight.toInt())
                }
                3 -> { // Height
                    val tvHeightValue = view.findViewById<TextView>(R.id.tvHeightValue)
                    val heightSeekBar = view.findViewById<SeekBar>(R.id.heightSeekBar)
                    val arcRulerView = view.findViewById<ArcRulerView>(R.id.arcRulerView)

                    heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            height = progress.toDouble()
                            tvHeightValue.text = "$progress"
                            arcRulerView.setValue(progress)
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })

                    // Initial set
                    arcRulerView.setValue(height.toInt())
                }
                4 -> { // Period Length
                    view.findViewById<NumberPicker>(R.id.pickerPeriodDays)?.apply {
                        minValue = 1
                        maxValue = 15
                        value = 3
                        setOnValueChangedListener { _, _, newVal ->
                            periodLength = newVal
                        }
                    }
                }
                5 -> { // Cycle Length
                    val tvRegular = view.findViewById<TextView>(R.id.tvRegular)
                    val tvIrregular = view.findViewById<TextView>(R.id.tvIrregular)
                    val pickerEnd = view.findViewById<NumberPicker>(R.id.pickerCycleEnd)
                    val tvSeparator = view.findViewById<TextView>(R.id.tvSeparator)
                    val pickerStart = view.findViewById<NumberPicker>(R.id.pickerCycleStart)

                    pickerStart.minValue = 15
                    pickerStart.maxValue = 45
                    pickerEnd.minValue = 15
                    pickerEnd.maxValue = 45

                    fun setRegularMode() {
                        tvRegular.setBackgroundResource(R.drawable.bg_button_pink)
                        tvRegular.setTextColor(Color.WHITE)
                        tvRegular.setTypeface(null, Typeface.BOLD)
                        tvIrregular.setBackgroundResource(0)
                        tvIrregular.setTextColor(Color.BLACK)
                        tvIrregular.setTypeface(null, Typeface.NORMAL)
                        pickerEnd.visibility = View.GONE
                        tvSeparator.visibility = View.GONE
                        pickerStart.value = 27
                        cycleLength = 27
                    }

                    fun setIrregularMode() {
                        tvRegular.setBackgroundResource(0)
                        tvRegular.setTextColor(Color.BLACK)
                        tvRegular.setTypeface(null, Typeface.NORMAL)
                        tvIrregular.setBackgroundResource(R.drawable.bg_button_pink)
                        tvIrregular.setTextColor(Color.WHITE)
                        tvIrregular.setTypeface(null, Typeface.BOLD)
                        pickerEnd.visibility = View.VISIBLE
                        tvSeparator.visibility = View.VISIBLE
                        pickerStart.value = 23
                        pickerEnd.value = 27
                        cycleLength = 25 // Average
                    }

                    tvRegular.setOnClickListener { setRegularMode() }
                    tvIrregular.setOnClickListener { setIrregularMode() }

                    pickerStart.setOnValueChangedListener { _, _, newVal ->
                        cycleLength = newVal
                    }

                    setRegularMode() // Default
                }
                6 -> { // Last Period
                    val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
                    calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                        lastPeriod = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    }
                }
            }
        }

        override fun getItemCount(): Int = titles.size
    }
}
