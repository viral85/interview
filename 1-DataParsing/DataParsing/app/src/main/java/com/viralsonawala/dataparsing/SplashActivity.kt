package com.viralsonawala.dataparsing

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.livinglifetechway.k4kotlin.isNetworkAvailable
import com.livinglifetechway.k4kotlin.setBindingView
import com.livinglifetechway.k4kotlin_retrofit.RetrofitCallback
import com.livinglifetechway.k4kotlin_retrofit.enqueueAwait
import com.viralsonawala.dataparsing.databinding.ActivitySplashBinding
import com.viralsonawala.dataparsing.model.AppDatabase
import com.viralsonawala.dataparsing.model.WeatherData
import com.viralsonawala.dataparsing.network.ApiClient
import com.viralsonawala.dataparsing.pref.AppPrefs
import com.viralsonawala.dataparsing.pref.Constants
import com.viralsonawala.dataparsing.ui.HomeActivity
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity


class SplashActivity : AppCompatActivity() {

    companion object {
        var TAG = SplashActivity::class.java.simpleName.orEmpty()
    }

    private lateinit var mBinding: ActivitySplashBinding

    private var mPopupShown = false
    private var mError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_splash)

        // If data is downloaded start home activity else fetch data first
        if (AppPrefs.dataDownloaded) {
            startActivity<HomeActivity>()
            finish()
        } else {
            fetchData()
        }
    }

    /**
     * Fetches all data and stores in the local storage for further access
     */
    private fun fetchData() = async(UI) {
        mError = false

        val jobs = mutableListOf<Job>()

        val db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "weatherInfo").allowMainThreadQueries().build()

        val weatherList = mutableListOf<WeatherData>()

        Constants.COUNTRIES.forEach { country ->
            Constants.TEMPERATURES.values.forEach { temperature ->
                jobs += launch {
                    try {
                        // replaces waitGroup.add(), no need for waitGroup.done()
                        ApiClient.service.getTempratureData(temperature, country).enqueueAwait(null, RetrofitCallback {
                            onResponseCallback { call, response ->
                                val body = response?.body()?.string()
                                val lines = body?.split(System.getProperty("line.separator")).orEmpty()

                                // find the blank line as the data divider
                                var dataIndex = 0
                                for (i in 1..lines.size) {
                                    if (lines[i].isBlank()) {
                                        dataIndex = i + 1 // skipping current empty line
                                        break
                                    }
                                }

                                // now start from the data index (skipping header)
                                for (i in (dataIndex + 1) until lines.size) {
                                    val line = lines[i]
                                    if (line.isNotBlank()) {

                                        Log.d(TAG, "fetchData: parsing line: $line")

                                        line.run {
                                            val weatherData = WeatherData(
                                                    country = country,
                                                    temperature = temperature,
                                                    year = substring(0, 4),
                                                    jan = safeSubstringToDoubleOrNull(4, 11),
                                                    feb = safeSubstringToDoubleOrNull(11, 18),
                                                    mar = safeSubstringToDoubleOrNull(18, 25),
                                                    apr = safeSubstringToDoubleOrNull(25, 32),
                                                    may = safeSubstringToDoubleOrNull(32, 39),
                                                    jun = safeSubstringToDoubleOrNull(39, 46),
                                                    jul = safeSubstringToDoubleOrNull(46, 53),
                                                    aug = safeSubstringToDoubleOrNull(53, 60),
                                                    sep = safeSubstringToDoubleOrNull(60, 67),
                                                    oct = safeSubstringToDoubleOrNull(67, 74),
                                                    nov = safeSubstringToDoubleOrNull(74, 81),
                                                    dec = safeSubstringToDoubleOrNull(81, 88),
                                                    win = safeSubstringToDoubleOrNull(89, 96),
                                                    spr = safeSubstringToDoubleOrNull(96, 103),
                                                    sum = safeSubstringToDoubleOrNull(103, 110),
                                                    aut = safeSubstringToDoubleOrNull(110, 117),
                                                    ann = safeSubstringToDoubleOrNull(118, 125)
                                            )
                                            weatherList.add(weatherData)
                                        }
                                    }
                                }
                                Log.d(TAG, "fetchData: data fetched for $country / $temperature")
                            }
                        })
                    } catch (e: Exception) {
                        // handle Error
                        runOnUiThread {
                            mError = true
                            handleError()
                        }
                    }
                }
            }
        }

        // let's wait for all apis to complete
        jobs.forEach { it.join() }

        if (!mError) {
            // insert records to database
            db.WeatherDao().insertAll(*weatherList.toTypedArray())

            // mark data as downloaded
            AppPrefs.dataDownloaded = true

            Log.d(TAG, "fetchData: all data fetched")

            // continue to next screen
            startActivity<HomeActivity>()
            finish()
        }
    }

    /**
     * Gets the substring from start index to end index
     * Trims the substring and convert it to double
     * This is the safe operation.
     * If it is able to perform the above mentioned operation successfully, it returns the double
     * Else it will simply run null
     * @param startIndex starting index of the substring
     * @param endIndex end index of the substring (until). End index will not be included
     * @return Returns the double value of successfully converted else null
     */
    private fun String.safeSubstringToDoubleOrNull(startIndex: Int, endIndex: Int) = try {
        substring(startIndex, endIndex).trim().toDoubleOrNull()
    } catch (e: IndexOutOfBoundsException) {
        null
    }

    /**
     * This method handles error from the API by showing a dialog for retrying
     */
    private fun handleError() {
        if (!mPopupShown) {
            alert {
                message = getString(if (!isNetworkAvailable()) R.string.network_error else R.string.default_error)
                positiveButton(R.string.btn_try_again) { fetchData(); mPopupShown = false }
                negativeButton(R.string.btn_cancel) { mPopupShown = false; finish() }
            }.apply { isCancelable = false }.show()
            mPopupShown = true
        }
    }

}
