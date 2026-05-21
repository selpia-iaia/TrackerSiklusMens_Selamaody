package com.example.trackersiklusmenstruasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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
                // Simulasi memanggil API (Karena kita menggunakan mock data agar bahasa Indonesia)
                delay(1000) 
                
                val tipsIndonesia = listOf(
                    HealthArticle(1, "Tips Mengatasi Nyeri Haid", "Gunakan kompres hangat di perut bawah dan lakukan olahraga ringan seperti yoga untuk merelaksasi otot."),
                    HealthArticle(2, "Nutrisi Penting Saat Menstruasi", "Konsumsi makanan kaya zat besi seperti bayam dan daging merah untuk mencegah lemas dan anemia."),
                    HealthArticle(3, "Pentingnya Hidrasi", "Minum air putih minimal 2 liter sehari untuk mengurangi gejala kembung dan menjaga energi tetap stabil."),
                    HealthArticle(4, "Menjaga Kebersihan Diri", "Ganti pembalut atau menstrual cup setiap 4-6 jam sekali untuk mencegah pertumbuhan bakteri dan infeksi."),
                    HealthArticle(5, "Manajemen Stres & Mood", "Lakukan meditasi atau hobi yang menyenangkan untuk menyeimbangkan perubahan hormon saat PMS."),
                    HealthArticle(6, "Kualitas Tidur", "Pastikan tidur cukup 7-8 jam karena tubuh membutuhkan waktu istirahat lebih saat masa menstruasi.")
                )
                
                _articles.postValue(tipsIndonesia)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
