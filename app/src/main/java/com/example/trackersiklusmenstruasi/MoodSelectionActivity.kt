package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersiklusmenstruasi.databinding.ActivityMoodSelectionBinding

class MoodSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodSelectionBinding
    private lateinit var adapter: MoodAdapter
    private val moodList = listOf(
        MoodItem("Luar Biasa", "🤩"),
        MoodItem("Ceria", "😁"),
        MoodItem("Senang", "😊"),
        MoodItem("Normal", "😐"),
        MoodItem("Sedih", "😔"),
        MoodItem("Marah", "😡"),
        MoodItem("Sakit", "🤒")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.btnClose.setOnClickListener { finish() }
        binding.btnOk.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MoodAdapter(moodList)
        binding.rvMoods.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMoods.adapter = adapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvMoods)

        // Add padding so top and bottom items can be centered
        binding.rvMoods.post {
            val padding = binding.rvMoods.height / 2 - 50 // 50 is half of item height (100dp)
            binding.rvMoods.setPadding(0, padding, 0, padding)
        }

        binding.rvMoods.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateSelectedMood()
            }
        })

        // Initial selection
        binding.rvMoods.post {
            binding.rvMoods.scrollToPosition(3) // Start at "Normal"
            updateSelectedMood()
        }
    }

    private fun updateSelectedMood() {
        val layoutManager = binding.rvMoods.layoutManager as LinearLayoutManager
        val centerView = findCenterView(layoutManager)
        if (centerView != null) {
            val position = layoutManager.getPosition(centerView)
            adapter.setSelected(position)
        }
    }

    private fun findCenterView(layoutManager: LinearLayoutManager): View? {
        val center = binding.rvMoods.height / 2
        var minDistance = Int.MAX_VALUE
        var closestView: View? = null

        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i)
            val childCenter = (layoutManager.getDecoratedTop(child!!) + layoutManager.getDecoratedBottom(child)) / 2
            val distance = Math.abs(childCenter - center)
            if (distance < minDistance) {
                minDistance = distance
                closestView = child
            }
        }
        return closestView
    }
}
