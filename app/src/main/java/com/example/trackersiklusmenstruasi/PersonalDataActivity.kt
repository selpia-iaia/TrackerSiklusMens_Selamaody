package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityPersonalDataBinding

class PersonalDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        loadUserData()
    }

    private fun loadUserData() {
        val dbHelper = DatabaseHelper.getInstance(this)
        val profile = dbHelper.getUserProfile()
        
        profile?.let {
            binding.tvUsername.text = it.name
            binding.tvValName.text = it.name
            binding.tvValWeight.text = "${it.weight.toInt()} Kg"
            binding.tvValHeight.text = "${it.height.toInt()} Cm"
            binding.tvValBirthday.text = it.birthday // Format in DB is yyyy-MM-dd
            
            // For phone and email, since they are not in our dummy DB yet, 
            // we could either add them to DB or leave them as placeholders.
            // Let's use name-based dummy email for now
            binding.tvValEmail.text = "${it.name.lowercase().replace(" ", "")}@gmail.com"
        }
    }
}
