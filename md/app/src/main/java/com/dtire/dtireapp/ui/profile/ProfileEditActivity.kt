package com.dtire.dtireapp.ui.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.data.response.UserItem
import com.dtire.dtireapp.databinding.ActivityProfileEditBinding
import com.dtire.dtireapp.utils.StateCallback
import com.dtire.dtireapp.utils.uriToFile
import java.io.File

class ProfileEditActivity : AppCompatActivity(), StateCallback<String>{
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var currentPhotoPath: String
    private lateinit var preference: UserPreference
    private var getFile: File? = null
    private val viewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbProfileEdit
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        preference = UserPreference(this)
        val userId = preference.getUserId()
        val userData = preference.getUserData()

        binding.apply {
            layoutEditPhoto.setOnClickListener { showDialog() }
            btnChangePhoto.setOnClickListener { showDialog() }
            btnCancelEdit.setOnClickListener { finish() }
            btnSaveEdit.setOnClickListener {
                if (userId != null) {
                    saveChanges(userId)
                }
            }
        }

        binding.apply {
            etEditEmail.setText(userData.email)
            etEditName.setText(userData.name)
            etEditAddress.setText(userData.address)
            etEditPhone.setText(userData.phone)
        }
    }

    private fun saveChanges(id: String) {
        val email = binding.etEditEmail.text.trim().toString()
        val name = binding.etEditName.text.trim().toString()
        val address = binding.etEditAddress.text.trim().toString()
        val phone = binding.etEditPhone.text.trim().toString()

        val userData = UserItem(
            "",
            address,
            id,
            phone,
            "",
            name,
            email,
        )
        viewModel.updateUser(id, userData).observe(this) {
            when (it) {
                is State.Success -> finish()
                is State.Loading -> onLoading()
                is State.Error -> onFailed(it.message)
            }
        }
    }

    override fun onSuccess(data: String) {
        // no use
    }

    override fun onLoading() {
        binding.apply {
            etEditName.isEnabled = false
            etEditEmail.isEnabled = false
            etEditAddress.isEnabled = false
            etEditPhone.isEnabled = false
        }
        val progressBar = ObjectAnimator.ofFloat(binding.profileEditLoading, View.ALPHA, 1f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }

    override fun onFailed(message: String?) {
        binding.apply {
            etEditName.isEnabled = true
            etEditEmail.isEnabled = true
            etEditAddress.isEnabled = true
            etEditPhone.isEnabled = true
        }
        val progressBar = ObjectAnimator.ofFloat(binding.profileEditLoading, View.ALPHA, 0f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
        Toast.makeText(this, "Email already used", Toast.LENGTH_SHORT).show()
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
                navigationBarColor = ContextCompat.getColor(this@ProfileEditActivity, R.color.white)
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
        com.dtire.dtireapp.utils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@ProfileEditActivity,
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
            val result = BitmapFactory.decodeFile(myFile.path)
            getFile = myFile
            binding.ivEditPhoto.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@ProfileEditActivity)
            getFile = myFile
            binding.ivEditPhoto.setImageURI(selectedImg)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

}