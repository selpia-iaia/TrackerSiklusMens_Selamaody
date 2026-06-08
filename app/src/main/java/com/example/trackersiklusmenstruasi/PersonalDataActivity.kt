package com.example.trackersiklusmenstruasi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityPersonalDataBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PersonalDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalDataBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var syncRepository: SyncRepository

    private val takePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            binding.ivProfile.setImageBitmap(bitmap)
            saveBitmapToInternalStorage(bitmap)
            showToast("Foto profil diperbarui!")
        }
    }

    private val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    binding.ivProfile.setImageBitmap(bitmap)
                    saveBitmapToInternalStorage(bitmap)
                    showToast("Foto profil diperbarui dari galeri!")
                }
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Gagal memproses gambar")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)
        syncRepository = SyncRepository(ApiService.create())

        setupUI()
        loadUserData()
        setupClickListeners()
    }

    private fun setupUI() {
        binding.itemNama.tvLabel.text = "Nama"
        binding.itemEmail.tvLabel.text = "Email"
        binding.itemEmail.tvValue.text = "sela123@gmail.com"
        
        binding.itemTelepon.tvLabel.text = "No Telepon"
        binding.itemTelepon.tvValue.text = "(+62) 859-5134-0773"
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        // Photo
        binding.btnEditPhoto.setOnClickListener { 
            val options = arrayOf("Ambil Foto (Kamera)", "Pilih dari Galeri")
            AlertDialog.Builder(this)
                .setTitle("Ubah Foto Profil")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePicturePreview.launch(null)
                        1 -> pickImageFromGallery.launch("image/*")
                    }
                }
                .show()
        }
        binding.ivProfile.setOnClickListener { binding.btnEditPhoto.performClick() }

        // Field Group 1
        binding.itemNama.rootView.setOnClickListener { showEditDialog("Nama") }
        binding.itemEmail.rootView.setOnClickListener { showEditDialog("Email") }
        binding.itemTelepon.rootView.setOnClickListener { showEditDialog("No Telepon") }
        
        binding.rowGender.setOnClickListener { showGenderPicker() }
        binding.rowBirthday.setOnClickListener { showDatePicker() }
        binding.rowLocation.setOnClickListener { showEditDialog("Lokasi") }

        // Field Group 2 (Physical)
        binding.rowWeight.setOnClickListener { showEditDialog("Berat Badan") }
        binding.rowHeight.setOnClickListener { showEditDialog("Tinggi Badan") }
        
        binding.switchBMI.setOnCheckedChangeListener { _, isChecked ->
            showToast("Tampilan BMI ${if (isChecked) "diaktifkan" else "dinonaktifkan"}")
        }

        // Field Group 3 (Settings)
        binding.rowWeekStart.setOnClickListener { showWeekStartPicker() }
        binding.rowTimeFormat.setOnClickListener { showTimeFormatPicker() }
        binding.rowDayStart.setOnClickListener { showTimePicker() }
        
        // Field Group 4 (Maintenance)
        binding.rowReset.setOnClickListener { showResetConfirmation() }
        binding.rowClearCache.setOnClickListener { 
            showToast("Membersihkan cache...")
            binding.tvValCache.text = "0.0MB"
            showToast("Cache berhasil dikosongkan!")
        }

        // Field Group 5 (App Specific)
        binding.rowWaterTarget.setOnClickListener { showEditDialog("Target Air") }
        binding.rowCupVolume.setOnClickListener { showEditDialog("Volume Cup") }
    }

    private fun showEditDialog(title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ubah $title")

        val input = EditText(this)
        val profile = dbHelper.getUserProfile()
        
        when (title) {
            "Nama" -> input.setText(profile?.name ?: "Sela.maody")
            "Email" -> input.setText(binding.itemEmail.tvValue.text)
            "No Telepon" -> {
                input.setText(binding.itemTelepon.tvValue.text)
                input.inputType = InputType.TYPE_CLASS_PHONE
            }
            "Lokasi" -> input.setText(binding.tvValLocation.text)
            "Berat Badan" -> {
                input.setText(profile?.weight?.toInt()?.toString() ?: "57")
                input.inputType = InputType.TYPE_CLASS_NUMBER
            }
            "Tinggi Badan" -> {
                input.setText(profile?.height?.toInt()?.toString() ?: "167")
                input.inputType = InputType.TYPE_CLASS_NUMBER
            }
            "Target Air" -> {
                input.setText(binding.tvValWaterTarget.text.toString().replace("MB", ""))
                input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            "Volume Cup" -> {
                input.setText(binding.tvValCupVolume.text.toString().replace("Ml", ""))
                input.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        builder.setView(input)
        builder.setPositiveButton("Simpan") { _, _ ->
            val newValue = input.text.toString()
            if (newValue.isNotEmpty()) updateData(title, newValue)
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val formattedDate = String.format("%02d - %02d - %d", month + 1, day, year)
            updateData("Tanggal Lahir", formattedDate)
        }
        DatePickerDialog(this, dateSetListener, 2006, 11, 25).show()
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedTime = String.format("%02d:%02d%s", displayHour, minute, amPm)
            binding.tvValDayStart.text = formattedTime
            showToast("Waktu mulai hari diperbarui")
        }
        TimePickerDialog(this, timeSetListener, 0, 0, false).show()
    }

    private fun showGenderPicker() {
        val items = arrayOf("Perempuan", "Laki-laki")
        AlertDialog.Builder(this).setTitle("Pilih Jenis Kelamin")
            .setItems(items) { _, which -> updateData("Jenis Kelamin", items[which]) }.show()
    }

    private fun showWeekStartPicker() {
        val items = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        AlertDialog.Builder(this).setTitle("Awal Pekan")
            .setItems(items) { _, which -> 
                binding.tvValWeekStart.text = items[which]
                showToast("Awal pekan diatur ke ${items[which]}")
            }.show()
    }

    private fun showTimeFormatPicker() {
        val items = arrayOf("Default (12 Jam)", "24 Jam")
        AlertDialog.Builder(this).setTitle("Format Waktu")
            .setItems(items) { _, which -> 
                binding.tvValTimeFormat.text = items[which]
                showToast("Format waktu diubah")
            }.show()
    }

    private fun showResetConfirmation() {
        AlertDialog.Builder(this).setTitle("Reset Data")
            .setMessage("Apakah Anda yakin ingin menghapus semua data dan mengatur ulang aplikasi?")
            .setPositiveButton("Hapus") { _, _ -> showToast("Aplikasi telah diatur ulang") }
            .setNegativeButton("Batal", null).show()
    }

    private fun updateData(type: String, value: String) {
        val profile = dbHelper.getUserProfile() ?: return
        var name = profile.name
        var birthday = profile.birthday
        var weight = profile.weight
        var height = profile.height

        when (type) {
            "Nama" -> {
                name = value
                binding.tvUsername.text = value
                binding.itemNama.tvValue.text = value
                binding.itemEmail.tvValue.text = "${value.lowercase().replace(" ", "")}@gmail.com"
            }
            "Tanggal Lahir" -> {
                birthday = value
                binding.tvValBirthday.text = value
            }
            "Berat Badan" -> {
                weight = value.toDoubleOrNull() ?: weight
                binding.tvValWeight.text = "${weight.toInt()} Kg"
            }
            "Tinggi Badan" -> {
                height = value.toDoubleOrNull() ?: height
                binding.tvValHeight.text = "${height.toInt()} Cm"
            }
            "Email" -> binding.itemEmail.tvValue.text = value
            "No Telepon" -> binding.itemTelepon.tvValue.text = value
            "Jenis Kelamin" -> binding.tvValGender.text = value
            "Lokasi" -> binding.tvValLocation.text = value
            "Target Air" -> binding.tvValWaterTarget.text = "${value}MB"
            "Volume Cup" -> binding.tvValCupVolume.text = "${value}Ml"
        }

        dbHelper.saveUserProfile(name, birthday, weight, height, profile.period_length, profile.cycle_length, profile.last_period)
        syncToServer()
    }

    private fun syncToServer() {
        val sessionManager = SessionManager(this)
        var userId = sessionManager.getUserId()
        if (userId <= 0) userId = 1

        val profile = dbHelper.getUserProfile() ?: return
        lifecycleScope.launch {
            try {
                syncRepository.syncUserProfile(UserProfileModel(userId, profile.name, profile.birthday, profile.weight, profile.height, profile.period_length, profile.cycle_length, profile.last_period))
                showToast("Data tersinkronisasi!")
            } catch (e: Exception) {
                // Offline fallback
            }
        }
    }

    private fun loadUserData() {
        loadProfileImage()
        val profile = dbHelper.getUserProfile()
        profile?.let {
            binding.tvUsername.text = it.name
            binding.itemNama.tvValue.text = it.name
            binding.itemEmail.tvValue.text = "${it.name.lowercase().replace(" ", "")}@gmail.com"
            binding.tvValWeight.text = "${it.weight.toInt()} Kg"
            binding.tvValHeight.text = "${it.height.toInt()} Cm"
            binding.tvValBirthday.text = it.birthday
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap) {
        try {
            val file = File(filesDir, "profile_pic_final.jpg")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadProfileImage() {
        try {
            val file = File(filesDir, "profile_pic_final.jpg")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.ivProfile.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
