package com.example.trackersiklusmenstruasi

import com.google.gson.annotations.SerializedName

data class HealthArticle(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
)
