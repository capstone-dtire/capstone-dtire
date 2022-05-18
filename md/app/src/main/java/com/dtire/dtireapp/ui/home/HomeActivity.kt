package com.dtire.dtireapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.dtire.dtireapp.databinding.ActivityHomeBinding
import com.dtire.dtireapp.ui.login.LoginActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.tbHome
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        binding.layoutHomeProfile.setOnClickListener {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}