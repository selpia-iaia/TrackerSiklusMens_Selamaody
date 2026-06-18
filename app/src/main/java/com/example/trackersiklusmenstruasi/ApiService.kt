package com.example.trackersiklusmenstruasi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("get_health_tips.php")
    suspend fun getHealthArticles(): List<HealthArticle>

    @GET("get_user_profile.php")
    suspend fun getUserProfile(@retrofit2.http.Query("user_id") userId: Int): UserProfileModel?

    @GET("get_personal_data.php")
    suspend fun getPersonalData(@retrofit2.http.Query("user_id") userId: Int): PersonalDataModel?

    @POST("save_user_profile.php")
    suspend fun saveUserProfile(@Body profile: UserProfileModel): SimpleResponse

    @POST("save_personal_data.php")
    suspend fun savePersonalData(@Body data: PersonalDataModel): SimpleResponse

    @POST("save_app_settings.php")
    suspend fun saveAppSettings(@Body settings: AppSettingsModel): SimpleResponse

    @POST("save_daily_log.php")
    suspend fun saveDailyLog(@Body log: DailyLogModel): SimpleResponse

    @POST("save_period.php")
    suspend fun savePeriod(@Body period: PeriodModel): SimpleResponse

    @POST("save_reminder.php")
    suspend fun saveReminder(@Body reminder: ReminderModel): SimpleResponse

    @POST("save_payment_method.php")
    suspend fun savePaymentMethod(@Body payment: PaymentMethodModel): SimpleResponse

    @POST("save_connected_account.php")
    suspend fun saveConnectedAccount(@Body account: ConnectedAccountModel): SimpleResponse

    @POST("register_user.php")
    suspend fun registerUser(@Body user: UserModel): SimpleResponse

    @GET("get_announcements.php")
    suspend fun getAnnouncements(): List<AnnouncementModel>

    companion object {
        private const val PC_IP = "172.20.10.13"
        private const val BASE_URL = "http://$PC_IP/db_menstruasi/"

       fun create(): ApiService {
            val gson = com.google.gson.GsonBuilder()
                .setLenient() // Mengizinkan format JSON yang agak kotor
                .create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService::class.java)
        }
    }
}

// Data Models
data class UserProfileModel(
    val user_id: Int,
    val name: String,
    val birthday: String,
    val weight: Double,
    val height: Double,
    val period_length: Int,
    val cycle_length: Int,
    val last_period: String
)

data class PersonalDataModel(
    val user_id: Int,
    val period_length: Int,
    val cycle_length: Int,
    val last_period: String
)

data class AppSettingsModel(
    val user_id: Int,
    val week_start: String,
    val time_format: String,
    val water_target: Int,
    val cup_volume: Int,
    val is_bmi_enabled: Boolean
)

data class DailyLogModel(
    val user_id: Int,
    val log_date: String,
    val flow: String?,
    val symptoms: String?,
    val moods: String?,
    val medicine: String?,
    val note: String?,
    val weight_log: Double?,
    val water_log: Int?,
    val temp_log: Double?
)

data class PeriodModel(
    val user_id: Int,
    val start_date: String,
    val end_date: String
)

data class ReminderModel(
    val user_id: Int,
    val reminder_type: String,
    val reminder_time: String,
    val is_enabled: Boolean
)

data class PaymentMethodModel(
    val user_id: Int,
    val provider: String,
    val account_number: String,
    val holder_name: String
)

data class ConnectedAccountModel(
    val user_id: Int,
    val provider: String,
    val email: String
)

data class UserModel(
    val username: String,
    val email: String,
    val password_hash: String
)

data class SimpleResponse(
    val success: Boolean,
    val message: String,
    val user_id: Int? = null
)

data class AnnouncementModel(
    val id: Int,
    val title: String,
    val message: String,
    val created_at: String? = null
)
