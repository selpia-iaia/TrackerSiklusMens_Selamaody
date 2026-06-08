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
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
        "Beritahu Kami Nama Anda",
        "Beritahu Kami\nUlang Tahun Anda",
        "Beritahu Kami\nBerat Badan Anda",
        "Beritahu Kami\nTinggi Badan Anda",
        "Lamanya Haid Anda",
        "Masukkan Panjang Siklus Anda",
        "Masukkan Tanggal Mulai dan\nTanggal Terakhir Menstruasi\nAnda?"
    )

    // Data to be saved
    private var userName = ""
    private var birthday = "2006-04-15"
    private var weight = 57.0
    private var height = 167.0
    private var periodLength = 3
    private var cycleLength = 27
    private var lastPeriod = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    private var lastPeriodStart: Calendar? = null
    private var lastPeriodEnd: Calendar? = null

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

        lastPeriodStart?.let { start ->
            lastPeriodEnd?.let { end ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dbHelper.savePeriod(sdf.format(start.time), sdf.format(end.time))
            }
        }
    }

    private fun updateProgress(position: Int) {
        binding.tvStepCount.text = "${position + 1}/${steps.size}"
        val progressContainer = binding.progressContainer
        for (i in 0 until progressContainer.childCount) {
            val dot = progressContainer.getChildAt(i)
            if (i == position) {
                dot.alpha = 1.0f
                dot.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FF5C7A")) // Pink Main
            } else {
                dot.alpha = 0.15f
                dot.backgroundTintList = null // Use default
            }
        }
    }

    inner class MonthAdapter(private val months: List<Calendar>) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setup_calendar_month, parent, false)
            return MonthViewHolder(view)
        }

        override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
            val monthCalendar = months[position]
            val tvMonthYear = holder.itemView.findViewById<TextView>(R.id.tvMonthYear)
            val localeID = Locale("id", "ID")
            val sdf = SimpleDateFormat("MMMM yyyy", localeID)
            tvMonthYear.text = sdf.format(monthCalendar.time)

            val llMonthPicker = holder.itemView.findViewById<View>(R.id.llMonthPicker)
            llMonthPicker.setOnClickListener {
                showMonthYearPickerDialog(monthCalendar) { newDate ->
                    // Logic untuk scroll ke bulan yang dipilih bisa ditambahkan di sini
                }
            }

            val gridDays = holder.itemView.findViewById<android.widget.GridLayout>(R.id.gridDays)
            setupMonthGrid(gridDays, monthCalendar)
        }

        private fun showMonthYearPickerDialog(current: Calendar, onDatePicked: (Calendar) -> Unit) {
            val dialog = android.app.AlertDialog.Builder(this@ProfileSetupActivity).create()
            val view = layoutInflater.inflate(R.layout.dialog_month_year_picker, null)
            val pickerMonth = view.findViewById<NumberPicker>(R.id.pickerMonth)
            val pickerYear = view.findViewById<NumberPicker>(R.id.pickerYear)

            val months = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")
            pickerMonth.minValue = 0
            pickerMonth.maxValue = 11
            pickerMonth.displayedValues = months
            pickerMonth.value = current.get(Calendar.MONTH)

            pickerYear.minValue = 2000
            pickerYear.maxValue = 2030
            pickerYear.value = current.get(Calendar.YEAR)

            view.findViewById<TextView>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
            view.findViewById<TextView>(R.id.btnOk).setOnClickListener {
                val result = Calendar.getInstance()
                result.set(Calendar.YEAR, pickerYear.value)
                result.set(Calendar.MONTH, pickerMonth.value)
                onDatePicked(result)
                dialog.dismiss()
            }

            dialog.setView(view)
            dialog.show()
        }

        override fun getItemCount() = months.size

        private fun setupMonthGrid(grid: android.widget.GridLayout, monthCal: Calendar) {
            grid.removeAllViews()
            grid.columnCount = 7
            val dayNames = listOf("S", "M", "T", "W", "T", "F", "S")
            for (name in dayNames) {
                val tv = TextView(grid.context).apply {
                    text = name
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setTextColor(Color.parseColor("#9E9E9E")) // gray
                    textSize = 12f
                    setPadding(0, 0, 0, 16)
                    layoutParams = android.widget.GridLayout.LayoutParams().apply {
                        width = 0
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                        columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                    }
                }
                grid.addView(tv)
            }

            val tempCal = monthCal.clone() as Calendar
            tempCal.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday
            val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Previous month padding
            val prevMonthCal = tempCal.clone() as Calendar
            prevMonthCal.add(Calendar.MONTH, -1)
            val daysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in (daysInPrevMonth - firstDayOfWeek + 1)..daysInPrevMonth) {
                val dayView = LayoutInflater.from(grid.context).inflate(R.layout.item_calendar_setup_day, grid, false)
                val tvDay = dayView.findViewById<TextView>(R.id.tvDay)
                tvDay.text = i.toString()
                tvDay.setTextColor(Color.parseColor("#E0E0E0")) // very light gray
                tvDay.setTypeface(null, Typeface.NORMAL)
                
                dayView.layoutParams = android.widget.GridLayout.LayoutParams().apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                }
                grid.addView(dayView)
            }

            for (i in 1..daysInMonth) {
                val dayView = LayoutInflater.from(grid.context).inflate(R.layout.item_calendar_setup_day, grid, false)
                val tvDay = dayView.findViewById<TextView>(R.id.tvDay)
                val viewRangeMiddle = dayView.findViewById<View>(R.id.viewRangeMiddle)
                val viewRangeStart = dayView.findViewById<View>(R.id.viewRangeStart)
                val viewRangeEnd = dayView.findViewById<View>(R.id.viewRangeEnd)
                
                tvDay.text = i.toString()
                tvDay.setTypeface(null, Typeface.BOLD)

                val currentDayCal = tempCal.clone() as Calendar
                currentDayCal.set(Calendar.DAY_OF_MONTH, i)

                updateDayHighlight(tvDay, viewRangeStart, viewRangeEnd, viewRangeMiddle, currentDayCal)

                dayView.setOnClickListener {
                    handleDateSelection(currentDayCal)
                    notifyDataSetChanged()
                }

                dayView.layoutParams = android.widget.GridLayout.LayoutParams().apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                }
                grid.addView(dayView)
            }
            
            // Next month padding
            val remainingCells = 42 - (firstDayOfWeek + daysInMonth)
            if (remainingCells > 0) {
                for (i in 1..remainingCells) {
                    val dayView = LayoutInflater.from(grid.context).inflate(R.layout.item_calendar_setup_day, grid, false)
                    val tvDay = dayView.findViewById<TextView>(R.id.tvDay)
                    tvDay.text = i.toString()
                    tvDay.setTextColor(Color.parseColor("#E0E0E0"))
                    tvDay.setTypeface(null, Typeface.NORMAL)

                    dayView.layoutParams = android.widget.GridLayout.LayoutParams().apply {
                        width = 0
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                        columnSpec = android.widget.GridLayout.spec(android.widget.GridLayout.UNDEFINED, 1f)
                    }
                    grid.addView(dayView)
                }
            }
        }

        private fun updateDayHighlight(tvDay: TextView, viewStart: View, viewEnd: View, viewMiddle: View, dayCal: Calendar) {
            val isStart = isSameDay(dayCal, lastPeriodStart)
            val isEnd = isSameDay(dayCal, lastPeriodEnd)
            val isInBetween = isBetween(dayCal, lastPeriodStart, lastPeriodEnd)

            viewStart.visibility = View.GONE
            viewEnd.visibility = View.GONE
            viewMiddle.visibility = View.GONE

            if (isStart && isEnd) {
                tvDay.setBackgroundResource(R.drawable.bg_button_pink)
                tvDay.setTextColor(Color.WHITE)
            } else if (isStart) {
                tvDay.setBackgroundResource(R.drawable.bg_button_pink)
                tvDay.setTextColor(Color.WHITE)
                viewStart.visibility = View.VISIBLE
            } else if (isEnd) {
                tvDay.setBackgroundResource(R.drawable.bg_button_pink)
                tvDay.setTextColor(Color.WHITE)
                viewEnd.visibility = View.VISIBLE
            } else if (isInBetween) {
                tvDay.setBackgroundResource(0)
                tvDay.setTextColor(Color.BLACK)
                viewMiddle.visibility = View.VISIBLE
            } else {
                tvDay.setBackgroundResource(0)
                tvDay.setTextColor(Color.BLACK)
            }
        }

        private fun handleDateSelection(dayCal: Calendar) {
            if (lastPeriodStart == null || (lastPeriodStart != null && lastPeriodEnd != null)) {
                lastPeriodStart = dayCal
                lastPeriodEnd = null
                lastPeriod = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCal.time)
            } else if (dayCal.before(lastPeriodStart)) {
                // If new date is before current start, make it the new start
                lastPeriodStart = dayCal
                lastPeriod = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dayCal.time)
            } else if (isSameDay(dayCal, lastPeriodStart)) {
                // Deselect if same day
                lastPeriodStart = null
                lastPeriodEnd = null
            } else {
                lastPeriodEnd = dayCal
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar?): Boolean {
            if (cal2 == null) return false
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }

        private fun isBetween(dayCal: Calendar, start: Calendar?, end: Calendar?): Boolean {
            if (start == null || end == null) return false
            return dayCal.after(start) && dayCal.before(end)
        }

        inner class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view)
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
                    val months = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")
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
                    val rvCalendar = view.findViewById<RecyclerView>(R.id.rvCalendar)
                    val months = mutableListOf<Calendar>()
                    val current = Calendar.getInstance()
                    // Show 12 months (6 back, 6 forward)
                    val start = current.clone() as Calendar
                    start.add(Calendar.MONTH, -6)
                    for (i in 0..12) {
                        months.add(start.clone() as Calendar)
                        start.add(Calendar.MONTH, 1)
                    }

                    rvCalendar.layoutManager = LinearLayoutManager(holder.itemView.context)
                    val adapter = MonthAdapter(months)
                    rvCalendar.adapter = adapter
                    
                    // Scroll to current month (index 6 since we added 6 months back)
                    rvCalendar.scrollToPosition(6)
                }
            }
        }

        override fun getItemCount(): Int = titles.size
    }
}
