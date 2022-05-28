package com.dtire.dtireapp.ui.login

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class LoginViewModel: ViewModel() {
    private val repository = Repository()

    fun loginUser(email: String, password: String) = repository.loginUser(email, password)
}