package com.example.trackersiklusmenstruasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HealthViewModel(private val repository: HealthRepository) : ViewModel() {

    private val _articles = MutableLiveData<List<HealthArticle>>()
    val articles: LiveData<List<HealthArticle>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchArticles() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getArticles()
                _articles.postValue(result)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue("Gagal memuat data: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
