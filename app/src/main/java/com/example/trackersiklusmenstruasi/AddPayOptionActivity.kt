package com.example.trackersiklusmenstruasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityAddPayOptionBinding

class AddPayOptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPayOptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPayOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnClose.setOnClickListener { finish() }
    }
}
