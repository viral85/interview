package com.viralsonawala.dataparsing.model

import android.arch.persistence.room.*


@Entity
data class WeatherData(
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,
        val country: String,
        val temperature: String,
        val year: String,
        val jan: Double?,
        val feb: Double?,
        val mar: Double?,
        val apr: Double?,
        val may: Double?,
        val jun: Double?,
        val jul: Double?,
        val aug: Double?,
        val sep: Double?,
        val oct: Double?,
        val nov: Double?,
        val dec: Double?,
        val win: Double?,
        val spr: Double?,
        val sum: Double?,
        val aut: Double?,
        val ann: Double?
)

@Dao
interface WeatherDao {
    @Insert
    fun add(weatherData: WeatherData)

    @Insert
    fun insertAll(vararg weatherData: WeatherData)

    @Query("SELECT * FROM WeatherData WHERE country = :country AND temperature = :temperature")
    fun getWeatherData(country: String, temperature: String): List<WeatherData>
}

@Database(entities = arrayOf(WeatherData::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun WeatherDao(): WeatherDao
}