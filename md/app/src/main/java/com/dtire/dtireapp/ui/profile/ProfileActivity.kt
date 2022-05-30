package com.dtire.dtireapp.ui.profile

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.data.response.UserItem
import com.dtire.dtireapp.databinding.ActivityProfileBinding
import com.dtire.dtireapp.ui.login.LoginActivity
import com.dtire.dtireapp.utils.StateCallback

class ProfileActivity : AppCompatActivity(), StateCallback<UserItem> {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var preference: UserPreference
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbProfile
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_green)
        window.decorView.systemUiVisibility = 0

        preference = UserPreference(this)
        binding.btnLogout.setOnClickListener {
            preference.deleteUser()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            finish()
            startActivity(intent)
        }
        val userId = preference.getUserId()
        if (userId != null) {
            viewModel.getData(userId).observe(this) {
                when (it) {
                    is State.Success -> it.data?.let { data -> onSuccess(data) }
                    is State.Loading -> onLoading()
                    is State.Error -> onFailed(it.message)
                }
            }
        }
    }

    override fun onSuccess(data: UserItem) {
        binding.apply {
            tvUserName.text = data.name
            tvProfileEmail.text = data.email
            tvProfileAddress.text = data.address ?: "-"
            tvProfilePhone.text = data.phone ?: "-"
        }
        val progressBar = ObjectAnimator.ofFloat(binding.profileLoading, View.ALPHA, 0f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }

    override fun onLoading() {
        val progressBar = ObjectAnimator.ofFloat(binding.profileLoading, View.ALPHA, 1f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }

    override fun onFailed(message: String?) {
        val progressBar = ObjectAnimator.ofFloat(binding.profileLoading, View.ALPHA, 0f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.to_edit -> {
                val intent = Intent(this@ProfileActivity, ProfileEditActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> true
        }
    }


}