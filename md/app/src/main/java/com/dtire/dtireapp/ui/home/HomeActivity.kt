package com.dtire.dtireapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.databinding.ActivityHomeBinding
import com.dtire.dtireapp.ui.history.HistoryActivity
import com.dtire.dtireapp.ui.login.LoginActivity
import com.dtire.dtireapp.ui.map.MapsActivity
import com.dtire.dtireapp.ui.profile.ProfileActivity
import com.dtire.dtireapp.ui.result.ResultActivity
import com.dtire.dtireapp.utils.createTempFile
import com.dtire.dtireapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.util.*


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var currentPhotoPath: String
    private lateinit var preferences: UserPreference
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbHome
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        preferences = UserPreference(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        binding.apply {
            layoutHomeProfile.setOnClickListener {
                val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(intent)
            }
            layoutHomeToCamera.setOnClickListener { showDialog() }
            layoutHomeToMap.setOnClickListener {
                val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                startActivity(intent)
            }
            layoutHomeToHistory.setOnClickListener {
                val intent = Intent(this@HomeActivity, HistoryActivity::class.java)
                startActivity(intent)
            }
            btnHomeRefresh.setOnClickListener { getMyLastLocation() }
        }
    }

    override fun onResume() {
        super.onResume()
        getMyLastLocation()
    }

    override fun onStart() {
        super.onStart()

        if (!preferences.isLoggedIn()) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
    }

    private val locationRequestPermissionLauncher =
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

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val city = getCityName(location.latitude, location.longitude)
                    binding.tvHomeLocation.text = city
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            locationRequestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getCityName(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocation(lat, lng, 1)

        return "${address[0].locality}, ${address[0].countryName}"
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.dialogsheetlayout)
            window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                navigationBarColor = ContextCompat.getColor(this@HomeActivity, R.color.white)
                setGravity(Gravity.CENTER)
            }
            show()
        }

        val toCamera = dialog.findViewById(R.id.bs_home_to_camera) as LinearLayout
        val toGallery = dialog.findViewById(R.id.bs_home_to_gallery) as LinearLayout

        toCamera.setOnClickListener {
            startCamera()
            dialog.dismiss()
        }
        toGallery.setOnClickListener {
            startGallery()
            dialog.dismiss()
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@HomeActivity,
                "com.dtire.dtireapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val intent = Intent(this@HomeActivity, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE, myFile.path)
            startActivity(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@HomeActivity)
            getFile = myFile
            val intent = Intent(this@HomeActivity, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE, myFile.path)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}