package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityPersonalDataBinding

class PersonalDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadUserData()
        setupClickListeners()
    }

    private fun setupUI() {
        // Set labels for included layouts
        binding.itemNama.tvLabel.text = "Nama"
        binding.itemEmail.tvLabel.text = "Email"
        binding.itemTelepon.tvLabel.text = "No Telepon"
        
        binding.itemNama.ivIcon.setImageResource(R.drawable.ic_edit)
        binding.itemEmail.ivIcon.setImageResource(R.drawable.ic_edit)
        binding.itemTelepon.ivIcon.setImageResource(R.drawable.ic_edit)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnEditPhoto.setOnClickListener { showToast("Edit Foto Profil") }
        
        val rows = listOf(
            binding.itemNama.rootView to "Edit Nama",
            binding.itemEmail.rootView to "Edit Email",
            binding.itemTelepon.rootView to "Edit No Telepon",
            binding.rowGender to "Pilih Jenis Kelamin",
            binding.rowBirthday to "Pilih Tanggal Lahir",
            binding.rowLocation to "Ubah Lokasi",
            binding.rowWeight to "Ubah Berat Badan",
            binding.rowHeight to "Ubah Tinggi Badan",
            binding.rowWeekStart to "Atur Awal Pekan",
            binding.rowTimeFormat to "Format Waktu",
            binding.rowDayStart to "Waktu Mulai Hari",
            binding.rowReset to "Atur Ulang Data",
            binding.rowClearCache to "Hapus Cache",
            binding.rowWaterTarget to "Target Air",
            binding.rowCupVolume to "Volume Cup"
        )

        rows.forEach { (view, message) ->
            view.setOnClickListener { showToast(message) }
        }

        binding.switchBMI.setOnCheckedChangeListener { _, isChecked ->
            showToast("Tampilkan BMI: $isChecked")
        }
    }

    private fun loadUserData() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val profile = dbHelper.getUserProfile()
        
        profile?.let {
            binding.tvUsername.text = it.name
            binding.itemNama.tvValue.text = it.name
            binding.tvValWeight.text = "${it.weight.toInt()} Kg"
            binding.tvValHeight.text = "${it.height.toInt()} Cm"
            binding.tvValBirthday.text = it.birthday
            
            // Dummy data for missing fields
            binding.itemEmail.tvValue.text = "${it.name.lowercase().replace(" ", "")}@gmail.com"
            binding.itemTelepon.tvValue.text = "(+62) 859-5134-0773"
            binding.tvValLocation.text = "Cicaheum"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
