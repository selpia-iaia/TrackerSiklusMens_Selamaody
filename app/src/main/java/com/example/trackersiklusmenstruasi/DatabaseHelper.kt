package com.example.trackersiklusmenstruasi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TrackerSiklus.db"
        private const val DATABASE_VERSION = 6

        @Volatile
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }
        }

        const val TABLE_USER = "user_profile"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_BIRTHDAY = "birthday"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_PERIOD_LENGTH = "period_length"
        const val COLUMN_CYCLE_LENGTH = "cycle_length"
        const val COLUMN_LAST_PERIOD = "last_period"

        const val TABLE_PERIODS = "periods"
        const val COLUMN_PERIOD_ID = "period_id"
        const val COLUMN_START_DATE = "start_date"
        const val COLUMN_END_DATE = "end_date"

        const val TABLE_DAILY_LOGS = "daily_logs"
        const val COLUMN_LOG_ID = "log_id"
        const val COLUMN_LOG_DATE = "log_date"
        const val COLUMN_FLOW = "flow"
        const val COLUMN_SYMPTOMS = "symptoms"
        const val COLUMN_MOODS = "moods"
        const val COLUMN_MEDICINE = "medicine"
        const val COLUMN_NOTE = "note"
        const val COLUMN_WEIGHT_LOG = "weight_log"
        const val COLUMN_WATER_LOG = "water_log"
        const val COLUMN_TEMP_LOG = "temp_log"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_USER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_BIRTHDAY TEXT, $COLUMN_WEIGHT REAL, $COLUMN_HEIGHT REAL, $COLUMN_PERIOD_LENGTH INTEGER, $COLUMN_CYCLE_LENGTH INTEGER, $COLUMN_LAST_PERIOD TEXT)")
        db?.execSQL("CREATE TABLE $TABLE_PERIODS ($COLUMN_PERIOD_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_START_DATE TEXT, $COLUMN_END_DATE TEXT)")
        db?.execSQL("CREATE TABLE $TABLE_DAILY_LOGS ($COLUMN_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LOG_DATE TEXT UNIQUE, $COLUMN_FLOW TEXT, $COLUMN_SYMPTOMS TEXT, $COLUMN_MOODS TEXT, $COLUMN_MEDICINE TEXT, $COLUMN_NOTE TEXT, $COLUMN_WEIGHT_LOG REAL, $COLUMN_WATER_LOG INTEGER, $COLUMN_TEMP_LOG REAL)")

        // Data Dummy
        db?.execSQL("INSERT INTO $TABLE_USER ($COLUMN_NAME, $COLUMN_BIRTHDAY, $COLUMN_WEIGHT, $COLUMN_HEIGHT, $COLUMN_PERIOD_LENGTH, $COLUMN_CYCLE_LENGTH, $COLUMN_LAST_PERIOD) VALUES ('Selpia Cantik', '2006-04-15', 57.0, 167.0, 5, 28, '2026-03-07')")
        db?.execSQL("INSERT INTO $TABLE_PERIODS ($COLUMN_START_DATE, $COLUMN_END_DATE) VALUES ('2026-02-10', '2026-02-14')")
        db?.execSQL("INSERT INTO $TABLE_PERIODS ($COLUMN_START_DATE, $COLUMN_END_DATE) VALUES ('2026-03-07', '2026-03-11')")
        
        // Data Dummy Daily Logs
        db?.execSQL("INSERT INTO $TABLE_DAILY_LOGS ($COLUMN_LOG_DATE, $COLUMN_FLOW, $COLUMN_SYMPTOMS, $COLUMN_MOODS, $COLUMN_MEDICINE, $COLUMN_NOTE) VALUES ('2026-03-07', 'Heavy', 'Cramps', 'Mood Swings', 'Ibuprofen', 'Hari pertama cukup berat')")
        db?.execSQL("INSERT INTO $TABLE_DAILY_LOGS ($COLUMN_LOG_DATE, $COLUMN_FLOW, $COLUMN_SYMPTOMS, $COLUMN_MOODS, $COLUMN_MEDICINE, $COLUMN_NOTE) VALUES ('2026-03-08', 'Medium', 'Headache', 'Tired', 'None', 'Sudah mendingan')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PERIODS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DAILY_LOGS")
        onCreate(db)
    }

    fun saveDailyLog(date: String, flow: String?, symptoms: String?, moods: String?, medicine: String?, note: String?, weight: Double? = null, water: Int? = null, temp: Double? = null): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOG_DATE, date)
            put(COLUMN_FLOW, flow)
            put(COLUMN_SYMPTOMS, symptoms)
            put(COLUMN_MOODS, moods)
            put(COLUMN_MEDICINE, medicine)
            put(COLUMN_NOTE, note)
            weight?.let { put(COLUMN_WEIGHT_LOG, it) }
            water?.let { put(COLUMN_WATER_LOG, it) }
            temp?.let { put(COLUMN_TEMP_LOG, it) }
        }
        return db.insertWithOnConflict(TABLE_DAILY_LOGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getUserProfile(): UserProfile? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER LIMIT 1", null)
        var user: UserProfile? = null
        if (cursor.moveToFirst()) {
            user = UserProfile(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDAY)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERIOD_LENGTH)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CYCLE_LENGTH)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_PERIOD))
            )
        }
        cursor.close()
        return user
    }

    fun saveUserProfile(name: String, birthday: String, weight: Double, height: Double, periodLength: Int, cycleLength: Int, lastPeriod: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_BIRTHDAY, birthday)
            put(COLUMN_WEIGHT, weight)
            put(COLUMN_HEIGHT, height)
            put(COLUMN_PERIOD_LENGTH, periodLength)
            put(COLUMN_CYCLE_LENGTH, cycleLength)
            put(COLUMN_LAST_PERIOD, lastPeriod)
        }
        return db.insert(TABLE_USER, null, values)
    }

    fun savePeriod(startDate: String, endDate: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_START_DATE, startDate)
            put(COLUMN_END_DATE, endDate)
        }
        return db.insert(TABLE_PERIODS, null, values)
    }

    fun deletePeriodsForMonth(month: Int, year: Int) {
        val db = this.writableDatabase
        // Month in DB is stored as YYYY-MM-DD
        val monthStr = String.format("%04d-%02d", year, month + 1)
        db.delete(TABLE_PERIODS, "$COLUMN_START_DATE LIKE ?", arrayOf("$monthStr%"))
    }

    fun getPeriodsForMonth(month: Int, year: Int): List<PeriodRecord> {
        val db = this.readableDatabase
        val monthStr = String.format("%04d-%02d", year, month + 1)
        val cursor = db.query(TABLE_PERIODS, null, "$COLUMN_START_DATE LIKE ?", arrayOf("$monthStr%"), null, null, null)
        val list = mutableListOf<PeriodRecord>()
        while (cursor.moveToNext()) {
            list.add(PeriodRecord(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERIOD_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE))
            ))
        }
        cursor.close()
        return list
    }

    fun getAllPeriods(): List<PeriodRecord> {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_PERIODS, null, null, null, null, null, "$COLUMN_START_DATE DESC")
        val list = mutableListOf<PeriodRecord>()
        while (cursor.moveToNext()) {
            list.add(PeriodRecord(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERIOD_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE))
            ))
        }
        cursor.close()
        return list
    }

    fun getWaterIntakeForDate(date: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_DAILY_LOGS, arrayOf(COLUMN_WATER_LOG), "$COLUMN_LOG_DATE = ?", arrayOf(date), null, null, null)
        var intake = 0
        if (cursor.moveToFirst()) {
            intake = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WATER_LOG))
        }
        cursor.close()
        return intake
    }

    fun updateWaterIntake(date: String, water: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WATER_LOG, water)
        }
        val rows = db.update(TABLE_DAILY_LOGS, values, "$COLUMN_LOG_DATE = ?", arrayOf(date))
        if (rows == 0) {
            values.put(COLUMN_LOG_DATE, date)
            db.insert(TABLE_DAILY_LOGS, null, values)
        }
    }
}

data class UserProfile(val id: Int, val name: String, val birthday: String, val weight: Double, val height: Double, val periodLength: Int, val cycleLength: Int, val lastPeriod: String)
data class DailyLog(val id: Int, val date: String, val flow: String?, val symptoms: String?, val moods: String?, val medicine: String?, val note: String?)
data class PeriodRecord(val id: Int, val startDate: String, val endDate: String)
