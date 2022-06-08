package com.dtire.dtireapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.response.MapsResponse
import com.dtire.dtireapp.data.response.PlaceDetailResponse
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, StateCallback<Any> {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googlePlaceList: ArrayList<ResultsItem>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheet)
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

        mMap.setOnMapClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
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

    @SuppressLint("PotentialBehaviorOverride")
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
        val prop = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName, PackageManager.GET_META_DATA
        )
        val bundle = Bundle(prop.metaData)
        val api = bundle.getString("com.google.android.geo.API_KEY")
        mMap.setOnMarkerClickListener { p0 ->
            val result = googlePlaceList[p0.tag as Int]
            val currentLocation = LatLng(lat, lng)
            val placeLocation = LatLng(result.geometry?.location?.lat!!, result.geometry.location.lng!!)
            val distance = (SphericalUtil.computeDistanceBetween(currentLocation, placeLocation) / 1000)
            val formattedDistance = String.format("%.2f", distance)
            val placeDetail = "https://maps.googleapis.com/maps/api/place/details/json?placeid=${result.placeId}&key=${api}"

            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getPlaceDetail(placeDetail).collect {
                    when (it) {
                        is State.Success -> it.data?.let { response ->
                            val data = response as PlaceDetailResponse
                            if (data.result?.formattedPhoneNumber != null) {
                                binding.apply {
                                    tvRepairShopPhone.visibility = visible
                                    tvRepairShopPhone.text = data.result.formattedPhoneNumber
                                    btnMarkerCall.apply {
                                        isEnabled = true
                                        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primary_green))
                                        setOnClickListener {
                                            Log.d("TAG", "callClick: Halo")
                                        }
                                    }
                                }
                            }
                            else {
                                binding.apply {
                                    binding.tvRepairShopPhone.visibility = gone
                                    btnMarkerCall.apply {
                                        isEnabled = false
                                        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.secondary_green))
                                    }

                                }
                            }
                        }
                        is State.Loading -> {}
                        is State.Error -> {}
                    }
                }
            }
            binding.tvRepairShopDistance.text = getString(R.string.map_distance, formattedDistance)
            binding.tvRepairShopName.text = result.name.toString()

            if (result.photos != null) {
                val photoUrl =
                    "https://maps.googleapis.com/maps/api/place/photo?photoreference=${result.photos[0]?.photoReference}&sensor=false&maxheight=500&maxwidth=500&key=${api}"
                Glide.with(applicationContext)
                    .load(photoUrl)
                    .into(binding.ivRepairShop)
            } else {
                binding.ivRepairShop.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MapsActivity,
                        R.drawable.ic_baseline_image_not_supported_24
                    )
                )
            }

            if (result.openingHours != null) {
                when (result.openingHours.openNow) {
                    true -> {
                        binding.tvRepairShopStatus.apply {
                            visibility = visible
                            text = context.getString(R.string.open)
                            setTextColor(resources.getColor(R.color.primary_green))
                        }
                    }
                    false -> {
                        binding.tvRepairShopStatus.apply {
                            visibility = visible
                            text = context.getString(R.string.closed)
                            setTextColor(resources.getColor(R.color.red))
                        }
                    }
                    else -> {
                        binding.tvRepairShopStatus.apply {
                            visibility = gone
                            text = ""
                        }
                    }
                }
            } else {
                binding.tvRepairShopStatus.apply {
                    visibility = gone
                    text = ""
                }
            }

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            true
        }

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
