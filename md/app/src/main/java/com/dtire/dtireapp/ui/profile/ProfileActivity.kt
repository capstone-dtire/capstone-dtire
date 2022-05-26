package com.dtire.dtireapp.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.databinding.ActivityProfileBinding
import com.dtire.dtireapp.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var preference: UserPreference

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