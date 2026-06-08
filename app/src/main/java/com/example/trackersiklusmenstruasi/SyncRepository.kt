package com.example.trackersiklusmenstruasi

import android.util.Log

class SyncRepository(private val apiService: ApiService) {

    suspend fun syncUserProfile(profile: UserProfileModel): Boolean {
        val response = apiService.saveUserProfile(profile)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (Profile): ${response.message}")
        }
        return response.success
    }

    suspend fun syncPersonalData(data: PersonalDataModel): Boolean {
        val response = apiService.savePersonalData(data)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (PersonalData): ${response.message}")
        }
        return response.success
    }

    suspend fun syncAppSettings(settings: AppSettingsModel): Boolean {
        val response = apiService.saveAppSettings(settings)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (Settings): ${response.message}")
        }
        return response.success
    }

    suspend fun syncDailyLog(log: DailyLogModel): Boolean {
        val response = apiService.saveDailyLog(log)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (DailyLog): ${response.message}")
        }
        return response.success
    }

    suspend fun syncPeriod(period: PeriodModel): Boolean {
        val response = apiService.savePeriod(period)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (Period): ${response.message}")
        }
        return response.success
    }

    suspend fun syncReminder(reminder: ReminderModel): Boolean {
        val response = apiService.saveReminder(reminder)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (Reminder): ${response.message}")
        }
        return response.success
    }

    suspend fun syncPaymentMethod(payment: PaymentMethodModel): Boolean {
        val response = apiService.savePaymentMethod(payment)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (Payment): ${response.message}")
        }
        return response.success
    }

    suspend fun syncConnectedAccount(account: ConnectedAccountModel): Boolean {
        val response = apiService.saveConnectedAccount(account)
        if (!response.success) {
            Log.e("SyncRepository", "Server Error (ConnectedAccount): ${response.message}")
        }
        return response.success
    }
}
