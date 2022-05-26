package com.dtire.dtireapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.databinding.ActivityLoginBinding
import com.dtire.dtireapp.ui.home.HomeActivity
import com.dtire.dtireapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preference: UserPreference
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = UserPreference(this)

        binding.btnLogin.setOnClickListener {
            binding.tvEmailError.visibility = View.INVISIBLE
            binding.tvPasswordError.visibility = View.INVISIBLE
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.tvEmailError.visibility = View.VISIBLE
                    binding.tvEmailError.text = "Email cannot empty"
                    binding.etLoginEmail.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.tvEmailError.visibility = View.VISIBLE
                    binding.tvEmailError.text = "Please use email format"
                    binding.etLoginEmail.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.tvPasswordError.visibility = View.VISIBLE
                    binding.tvPasswordError.text = "Password cannot empty"
                    binding.etLoginPassword.requestFocus()
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.tvPasswordError.visibility = View.VISIBLE
                    binding.tvPasswordError.text = "Password minimum contain 8 characters"
                    binding.etLoginPassword.requestFocus()
                    return@setOnClickListener
                }
            }

            viewModel.loginUser(email, password).observe(this@LoginActivity) {
                if (it.status == 200) {
                    preference.saveUserId(it)
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finish()
                    startActivity(intent)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (preference.isLoggedIn()) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
    }

}