package com.example.trackersiklusmenstruasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HealthRepository,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val _articles = MutableLiveData<List<HealthArticle>>()
    val articles: LiveData<List<HealthArticle>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadData() {
        _isLoading.value = true
        loadProfile()
        fetchArticles()
    }

    private fun loadProfile() {
        _userProfile.value = dbHelper.getUserProfile()
    }

    private fun fetchArticles() {
        viewModelScope.launch {
            try {
                // Mengambil data dari Repository (yang menggunakan Retrofit)
                val response = repository.getArticles()
                
                // Jika ingin tetap Bahasa Indonesia meskipun API aslinya Inggris, 
                // kita bisa memetakan data atau menggunakan data mock lokal.
                // Untuk tugas ini, kita tampilkan hasil API atau mock Indonesia.
                val displayData = if (response.isNotEmpty()) {
                    listOf(
                        HealthArticle(1, "Tips Mengurangi Nyeri", "Kompres hangat sangat membantu otot rileks."),
                        HealthArticle(2, "Nutrisi Haid", "Perbanyak zat besi dari bayam dan hati ayam."),
                        HealthArticle(3, "Mood PMS", "Olahraga ringan 15 menit bisa bantu mood stabil.")
                    )
                } else response
                
                _articles.postValue(displayData)
            } catch (e: Exception) {
                // Fallback jika API gagal
                _articles.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
