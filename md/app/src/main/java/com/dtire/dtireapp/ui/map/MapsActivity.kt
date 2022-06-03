package com.dtire.dtireapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.response.MapsResponse
import com.dtire.dtireapp.data.response.ResultsItem
import com.dtire.dtireapp.databinding.ActivityMapsBinding
import com.dtire.dtireapp.utils.StateCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, StateCallback<Any> {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googlePlaceList: ArrayList<ResultsItem>
    private val viewModel: MapsViewModel by viewModels()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        googlePlaceList = ArrayList()
        getMyLastLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

    }

    private fun getNearbyPlace() {
        val prop = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName, PackageManager.GET_META_DATA
        )
        val bundle = Bundle(prop.metaData)
        val api = bundle.getString("com.google.android.geo.API_KEY")

        Log.d("TAG", "showStartMarker: $lat, $lng")
        val url = ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + lat + "," + lng + "&radius=1500" + "&type=" + "car_repair"
                + "&key=" + api)

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getNearbyPlace(url).collect {
                when (it) {
                    is State.Success -> it.data?.let { placeData -> onSuccess(placeData) }
                    is State.Loading -> onLoading()
                    is State.Error -> onFailed(it.message)
                }
            }
        }
    }

    override fun onSuccess(data: Any) {
        val googleResponseModel: MapsResponse = data as MapsResponse
        if (googleResponseModel.results != null && googleResponseModel.results.isNotEmpty()) {
            googlePlaceList.clear()
            mMap.clear()
            for (i in googleResponseModel.results.indices) {
                googleResponseModel.results[i]?.let {
                    googlePlaceList.add(it)
                    addMarker(it, i)
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onFailed(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun addMarker(locationData: ResultsItem, position: Int) {
        val markerOptions = MarkerOptions()
            .position(
                LatLng(
                    locationData.geometry?.location?.lat!!,
                    locationData.geometry.location.lng!!
                )
            )
            .title(locationData.name)

        mMap.addMarker(markerOptions)?.tag = position
    }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lng = location.longitude
                    getNearbyPlace()
                    showStartMarker(location)
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 16f))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}