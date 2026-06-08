package com.example.trackersiklusmenstruasi

import android.util.Log
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
                // Mengambil data dari XAMPP
                val response = repository.getArticles()
                
                // Langsung tampilkan data dari database XAMPP
                if (response.isNotEmpty()) {
                    _articles.postValue(response)
                } else {
                    // Jika database kosong, beri pesan atau data default
                    _articles.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Gagal ambil tips: ${e.message}")
                _articles.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
