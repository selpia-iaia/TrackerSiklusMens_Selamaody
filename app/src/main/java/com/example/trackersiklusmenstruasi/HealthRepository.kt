package com.example.trackersiklusmenstruasi

class HealthRepository(private val apiService: ApiService) {
    suspend fun getArticles() = apiService.getHealthArticles()
}
