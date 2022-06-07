package com.dtire.dtireapp.ui.home

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.data.response.ImageResultResponse
import com.dtire.dtireapp.data.response.UploadPhotoResponse
import com.dtire.dtireapp.data.response.UserItem
import com.dtire.dtireapp.data.retrofit.ApiConfig
import com.dtire.dtireapp.databinding.ActivityHomeBinding
import com.dtire.dtireapp.ui.history.HistoryActivity
import com.dtire.dtireapp.ui.login.LoginActivity
import com.dtire.dtireapp.ui.map.MapsActivity
import com.dtire.dtireapp.ui.profile.ProfileActivity
import com.dtire.dtireapp.ui.result.ResultActivity
import com.dtire.dtireapp.utils.StateCallback
import com.dtire.dtireapp.utils.createTempFile
import com.dtire.dtireapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class HomeActivity : AppCompatActivity(), StateCallback<UserItem> {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var currentPhotoPath: String
    private lateinit var preferences: UserPreference
    private var getFile: File? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: HomeViewModel by viewModels()

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

        isUploadLoading(false)

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
                if (isOnline(this@HomeActivity)) {
                    val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.apply {
                        layoutHome.visibility = invisible
                        layoutNoConnection.visibility = visible
                    }
                }
            }
            layoutHomeToHistory.setOnClickListener {
                val intent = Intent(this@HomeActivity, HistoryActivity::class.java)
                startActivity(intent)
            }
            btnHomeRefresh.setOnClickListener { getMyLastLocation() }
            btnRefreshInternet.setOnClickListener {
                val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                finish()
                startActivity(intent)
            }
        }

        val userId = preferences.getUserId()
        viewModel.getUser(userId).observe(this) {
            when(it) {
                is State.Success -> it.data?.let { data -> onSuccess(data) }
                is State.Error -> onFailed(it.message)
                is State.Loading -> onLoading()
            }
        }
    }

    override fun onSuccess(data: UserItem) {
        preferences.saveUserData(data)
        binding.tvHomeGreeting.text = getString(R.string.user_greeting, data.name)
    }

    override fun onLoading() {
        binding.tvHomeGreeting.text = getString(R.string.loading)
    }


    override fun onFailed(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.tvHomeGreeting.text = getString(R.string.user_greeting, preferences.getUserData().name)
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

    private fun isUploadLoading(status: Boolean) {
        if (status) {
            val progressBar = ObjectAnimator.ofFloat(binding.homeLoading, View.ALPHA, 1f).setDuration(300)
            AnimatorSet().apply {
                play(progressBar)
                start()
            }
            binding.layoutHome.visibility = invisible
        } else {
            val progressBar = ObjectAnimator.ofFloat(binding.homeLoading, View.ALPHA, 0f).setDuration(300)
            AnimatorSet().apply {
                play(progressBar)
                start()
            }
            binding.layoutHome.visibility = visible
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
        if (isOnline(this@HomeActivity)) {
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
        } else {
            binding.apply {
                layoutHome.visibility = invisible
                layoutNoConnection.visibility = visible
            }
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

    @SuppressLint("MissingPermission")
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        Log.d("Internet", "NetworkCapabilities.NO_CONNECTION")
        return false
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

    private fun uploadPhoto() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestImageFile
            )
//            imageToCloudFunc("https://images.simpletire.com/image/upload/v1600461522/learn-blog/Cracked_and_Dangerous_Tires.jpg")
            isUploadLoading(true)
            ApiConfig.getApiService().uploadPhoto(imageMultipart)
                .enqueue(object : Callback<UploadPhotoResponse> {
                    override fun onResponse(
                        call: Call<UploadPhotoResponse>,
                        response: Response<UploadPhotoResponse>
                    ) {
                        if (response.isSuccessful) {
                            imageToCloudFunc(response.body()?.url.toString())
                        } else {
                            Log.d("TAGG", "onResponseGagal1: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UploadPhotoResponse>, t: Throwable) {
                        Log.d("TAGG", "onResponseGagal2: ${t.message}")
                    }

                })
            }

    }

    private fun imageToCloudFunc(url: String) {
        ApiConfig.getUploadImageApiService().uploadImageToCloudFunc(url)
            .enqueue(object : Callback<ImageResultResponse> {
                override fun onResponse(
                    call: Call<ImageResultResponse>,
                    response: Response<ImageResultResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("TAGG", "onResponseBerhasil: ${response.body()?.predictions?.get(0)}")
                        val intent = Intent(this@HomeActivity, ResultActivity::class.java)
                        intent.putExtra(ResultActivity.EXTRA_IMAGE_RESULT,
                            response.body()?.predictions?.get(0)?.get(0)
                        )
                        intent.putExtra(ResultActivity.EXTRA_ORIGIN, "home")
                        intent.putExtra(ResultActivity.EXTRA_IMAGE_URL, url)
                        finish()
                        startActivity(intent)
                    } else {
                        Log.d("TAGG", "onResponseFailed3: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ImageResultResponse>, t: Throwable) {
                    Log.d("TAGG", "onFailed4: ${t.message}")
                }

            })
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
//            val intent = Intent(this@HomeActivity, ResultActivity::class.java)
//            intent.putExtra(ResultActivity.EXTRA_IMAGE, myFile.path)
            uploadPhoto()
//            startActivity(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@HomeActivity)
            getFile = myFile
//            val intent = Intent(this@HomeActivity, ResultActivity::class.java)
//            intent.putExtra(ResultActivity.EXTRA_IMAGE, myFile.path)
//            startActivity(intent)
            uploadPhoto()
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
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}