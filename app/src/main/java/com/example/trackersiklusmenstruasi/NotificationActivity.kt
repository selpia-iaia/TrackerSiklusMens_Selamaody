package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        rvNotifications = findViewById(R.id.rvNotifications)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnBack = findViewById(R.id.btnBack)

        rvNotifications.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener { finish() }

        fetchNotifications()
    }

    private fun fetchNotifications() {
        lifecycleScope.launch {
            try {
                val api = ApiService.create()
                val list = api.getAnnouncements()
                
                if (list.isNotEmpty()) {
                    rvNotifications.adapter = NotificationAdapter(list)
                    tvEmpty.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Menampilkan pesan error yang lebih detail
                tvEmpty.text = "Gagal memuat: ${e.message}\nPeriksa Koneksi Ke Laptop"
                tvEmpty.visibility = View.VISIBLE
                
                android.widget.Toast.makeText(this@NotificationActivity, "Gagal koneksi server", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
