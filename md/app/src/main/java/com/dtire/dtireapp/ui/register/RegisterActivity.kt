package com.dtire.dtireapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dtire.dtireapp.R
import com.dtire.dtireapp.data.State
import com.dtire.dtireapp.data.response.RegisterResponse
import com.dtire.dtireapp.databinding.ActivityRegisterBinding
import com.dtire.dtireapp.ui.login.LoginActivity
import com.dtire.dtireapp.utils.StateCallback

class RegisterActivity : AppCompatActivity(), StateCallback<RegisterResponse> {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            binding.tvNameError.visibility = invisible
            binding.tvEmailError.visibility = invisible
            binding.tvPasswordError.visibility = invisible
            binding.tvPasswordConfirmError.visibility = invisible

            val name = binding.etRegisterName.text.toString().trim()
            val email = binding.etRegisterEmail.text.toString().trim()
            val password = binding.etRegisterPassword.text.toString().trim()
            val confirmPassword = binding.etRegisterConfirmPassword.text.toString().trim()

            when {
                name.isEmpty() -> {
                    binding.tvNameError.visibility = visible
                    binding.tvNameError.text = getString(R.string.no_empty_name)
                    binding.etRegisterName.requestFocus()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding.tvEmailError.visibility = visible
                    binding.tvEmailError.text = getString(R.string.no_empty_email)
                    binding.etRegisterEmail.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.tvEmailError.visibility = visible
                    binding.tvEmailError.text = getString(R.string.use_email_format)
                    binding.etRegisterEmail.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.tvPasswordError.visibility = visible
                    binding.tvPasswordError.text = getString(R.string.no_empty_password)
                    binding.etRegisterPassword.requestFocus()
                    return@setOnClickListener
                }
                password.length < 8 -> {
                    binding.tvPasswordError.visibility = visible
                    binding.tvPasswordError.text = getString(R.string.minimum_password_length)
                    binding.etRegisterPassword.requestFocus()
                    return@setOnClickListener
                }
                confirmPassword.isEmpty() -> {
                    binding.tvPasswordConfirmError.visibility = visible
                    binding.tvPasswordConfirmError.text = getString(R.string.confirm_your_password)
                    binding.etRegisterConfirmPassword.requestFocus()
                    return@setOnClickListener
                }
                confirmPassword != password -> {
                    binding.tvPasswordConfirmError.visibility = visible
                    binding.tvPasswordConfirmError.text = getString(R.string.password_not_match)
                    binding.etRegisterConfirmPassword.requestFocus()
                    return@setOnClickListener
                }
            }

            viewModel.registerUser(name, email, password).observe(this) {
                when (it) {
                    is State.Error -> onFailed(it.message)
                    is State.Success -> it.data?.let { userData -> onSuccess(userData) }
                    is State.Loading -> onLoading()
                }
            }
        }


        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onSuccess(data: RegisterResponse) {
        Toast.makeText(this@RegisterActivity, data.message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }

    override fun onLoading() {
        val progressBar = ObjectAnimator.ofFloat(binding.registerLoading, View.ALPHA, 1f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }

    override fun onFailed(message: String?) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
        val progressBar = ObjectAnimator.ofFloat(binding.registerLoading, View.ALPHA, 0f).setDuration(300)
        AnimatorSet().apply {
            play(progressBar)
            start()
        }
    }
}