package com.viralsonawala.dataparsing.ui

import android.arch.persistence.room.Room
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.github.nitrico.lastadapter.LastAdapter
import com.livinglifetechway.k4kotlin.setBindingView
import com.livinglifetechway.k4kotlin.setItems
import com.viralsonawala.dataparsing.BR
import com.viralsonawala.dataparsing.R
import com.viralsonawala.dataparsing.databinding.ActivityHomeBinding
import com.viralsonawala.dataparsing.databinding.ItemTemperatureBinding
import com.viralsonawala.dataparsing.model.AppDatabase
import com.viralsonawala.dataparsing.model.WeatherData
import com.viralsonawala.dataparsing.pref.Constants

class HomeActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityHomeBinding

    private val mDb by lazy {
        Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "weatherInfo").allowMainThreadQueries().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)

        mBinding.countrySpinner.setItems(Constants.COUNTRIES)
        mBinding.timeSpinner.setItems(ArrayList(Constants.TEMPERATURES.keys))

        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                performFilter()
            }
        }

        mBinding.countrySpinner.onItemSelectedListener = itemSelectedListener
        mBinding.timeSpinner.onItemSelectedListener = itemSelectedListener

        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Performs filtering based on current selected items in the spinner
     */
    fun performFilter() {
        val country = mBinding.countrySpinner.selectedItem?.toString().orEmpty()
        val temperature = Constants.TEMPERATURES[mBinding.timeSpinner.selectedItem?.toString().orEmpty()].orEmpty()

        val weatherData = mDb.WeatherDao().getWeatherData(country, temperature)

        LastAdapter(weatherData, BR.item)
                .map<WeatherData, ItemTemperatureBinding>(R.layout.item_temperature)
                .into(mBinding.recyclerView)

    }
}
