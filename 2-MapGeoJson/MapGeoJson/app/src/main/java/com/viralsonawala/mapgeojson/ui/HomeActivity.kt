package com.viralsonawala.mapgeojson.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPoint
import com.livinglifetechway.k4kotlin.dpToPx
import com.livinglifetechway.k4kotlin.isNetworkAvailable
import com.livinglifetechway.k4kotlin.setBindingView
import com.livinglifetechway.k4kotlin.toastNow
import com.livinglifetechway.k4kotlin_retrofit.RetrofitCallback
import com.livinglifetechway.k4kotlin_retrofit.enqueue
import com.viralsonawala.articles.network.ApiClient
import com.viralsonawala.mapgeojson.R
import com.viralsonawala.mapgeojson.databinding.ActivityHomeBinding
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mBinding: ActivityHomeBinding

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)

        // load google map
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // store google map instance
        mMap = googleMap

        // load the data
        fetchData()
    }

    /**
     * Fetches data for the map and shows in the UI
     */
    private fun fetchData() {
        ApiClient.service.getFarms("https://s3.eu-west-2.amazonaws.com/interview-question-data/farm/farms.json")
                .enqueue(this, RetrofitCallback {
                    progressView = mBinding.progressBar
                    on2xxSuccess { _, response ->
                        val responseBody = JSONArray(response?.body()?.string().orEmpty()).getJSONObject(0)

                        // parse farms array
                        val farms = responseBody.getJSONArray("farms")
                        val featureCollection1 = createFeatureCollectionFromGeoJsonArray(farms)
                        val layer = GeoJsonLayer(mMap, featureCollection1)
                        layer.addLayerToMap()

                        // on click show farm name as toast
                        layer.setOnFeatureClickListener {
                            toastNow(it.getProperty("farm_name").orEmpty())
                        }

                        // parse fields array
                        val fields = responseBody.getJSONArray("fields")
                        val featureCollection2 = createFeatureCollectionFromGeoJsonArray(fields)
                        val layer2 = GeoJsonLayer(mMap, featureCollection2)
                        layer2.addLayerToMap()

                        // on click show field name
                        layer2.setOnFeatureClickListener {
                            toastNow(it.getProperty("field_name").orEmpty())
                        }

                        // include in lat lng bounds
                        val bounds = LatLngBounds.builder()
                        layer.features.forEach {
                            if (it.geometry.geometryType == "Point" && it.geometry is GeoJsonPoint) {
                                bounds.include((it.geometry as GeoJsonPoint).coordinates)
                            }
                        }

                        // animate to show all markers
                        try {
                            mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), dpToPx(100f).toInt()))
                        } catch (e: Exception) {
                            // Bounds are not available, ignore
                        }
                    }
                    onUnsuccessfulResponseOrFailureNotCancelled { _, _, _ ->
                        handleError()
                    }
                })
    }

    /**
     * Converts the JSONArray to GeoJson's FeatureCollection
     * @param inputJsonArray Takes a JSONArray which should be the part of FeatureCollection
     * @return JSONObject which representing FeatureCollection
     */
    private fun createFeatureCollectionFromGeoJsonArray(inputJsonArray: JSONArray): JSONObject {
        val featureCollection = JSONObject()
        featureCollection.put("type", "FeatureCollection")
        val jsonArray = JSONArray()
        for (i in 0 until inputJsonArray.length()) {
            jsonArray.put(inputJsonArray.getJSONObject(i))
        }
        featureCollection.put("features", jsonArray)
        return featureCollection
    }

    /**
     * This method handles error from the API by showing a dialog for retrying
     */
    private fun handleError() {
        alert {
            message = getString(if (!isNetworkAvailable()) R.string.network_error else R.string.default_error)
            positiveButton(R.string.btn_try_again) { fetchData() }
            negativeButton(R.string.btn_cancel) {}
        }.show()
    }
}
