package com.dtire.dtireapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.preferences.UserPreference
import com.dtire.dtireapp.data.response.LoginResponse
import com.dtire.dtireapp.databinding.ActivityLoginBinding
import com.dtire.dtireapp.ui.home.HomeActivity
import com.dtire.dtireapp.ui.register.RegisterActivity
import com.dtire.dtireapp.utils.StateCallback

class LoginActivity : AppCompatActivity(), StateCallback<LoginResponse> {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preference: UserPreference
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = UserPreference(this)

        binding.btnLogin.setOnClickListener {
            binding.tvEmailError.visibility = invisible
            binding.tvPasswordError.visibility = invisible

            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.tvEmailError.visibility = visible
                    binding.tvEmailError.text = getString(R.string.no_empty_email)
                    binding.etLoginEmail.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.tvEmailError.visibility = visible
                    binding.tvEmailError.text = getString(R.string.use_email_format)
                    binding.etLoginEmail.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.tvPasswordError.visibility = visible
                    binding.tvPasswordError.text = getString(R.string.no_empty_password)
                    binding.etLoginPassword.requestFocus()
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.tvPasswordError.visibility = visible
                    binding.tvPasswordError.text = getString(R.string.minimum_password_length)
                    binding.etLoginPassword.requestFocus()
                    return@setOnClickListener
                }
            }

            viewModel.loginUser(email, password).observe(this) {
                when (it) {
                    is State.Error -> onFailed(it.message)
                    is State.Success -> it.data?.let { userInfo -> onSuccess(userInfo) }
                    is State.Loading -> onLoading()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSuccess(data: LoginResponse) {
        preference.saveUserId(data)
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        finish()
        startActivity(intent)
    }

    override fun onFailed(message: String?) {
        Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
        val progressBar = ObjectAnimator.ofFloat(binding.loginLoading, View.ALPHA, 0f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }

    override fun onLoading() {
        val progressBar = ObjectAnimator.ofFloat(binding.loginLoading, View.ALPHA, 1f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }

    override fun onStart() {
        super.onStart()
        if (preference.isLoggedIn()) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        }
    }
}