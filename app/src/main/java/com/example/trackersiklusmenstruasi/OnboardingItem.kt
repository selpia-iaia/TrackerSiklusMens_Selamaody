package com.example.trackersiklusmenstruasi

data class OnboardingItem(
    val image: Int,
    val title: String,
    val desc: String,
    val secondaryImage: Int? = null,
    val tertiaryImage: Int? = null
)