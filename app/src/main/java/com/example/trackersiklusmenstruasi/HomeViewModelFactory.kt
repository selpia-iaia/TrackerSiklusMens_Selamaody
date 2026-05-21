package com.example.trackersiklusmenstruasi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(
    private val repository: HealthRepository,
    private val dbHelper: DatabaseHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
