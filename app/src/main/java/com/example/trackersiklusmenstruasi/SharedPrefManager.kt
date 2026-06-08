package com.example.trackersiklusmenstruasi

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "TrackerSiklusPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_ONBOARDING_FINISHED = "onboardingFinished"
        private const val KEY_PREFERRED_PAYMENT = "preferredPayment"
    }

    fun setPreferredPayment(method: String) {
        editor.putString(KEY_PREFERRED_PAYMENT, method)
        editor.apply()
    }

    fun getPreferredPayment(): String {
        return sharedPreferences.getString(KEY_PREFERRED_PAYMENT, "Google Play Billing") ?: "Google Play Billing"
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setUserId(userId: Int) {
        editor.putInt(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun setOnboardingFinished(isFinished: Boolean) {
        editor.putBoolean(KEY_ONBOARDING_FINISHED, isFinished)
        editor.apply()
    }

    fun isOnboardingFinished(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_FINISHED, false)
    }

    fun clear() {
        editor.clear()
        editor.apply()
    }
}
