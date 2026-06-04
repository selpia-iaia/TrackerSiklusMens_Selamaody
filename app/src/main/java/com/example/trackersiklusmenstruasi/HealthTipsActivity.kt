package com.example.trackersiklusmenstruasi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackersiklusmenstruasi.databinding.ActivityHealthTipsBinding

class HealthTipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHealthTipsBinding
    private val adapter by lazy { HealthAdapter() }
    
    private val viewModel: HealthViewModel by viewModels {
        HealthViewModelFactory(HealthRepository(ApiService.create()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchArticles()
    }

    private fun setupRecyclerView() {
        binding.rvHealthTips.layoutManager = LinearLayoutManager(this)
        binding.rvHealthTips.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.articles.observe(this) { articles ->
            adapter.submitList(articles)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
