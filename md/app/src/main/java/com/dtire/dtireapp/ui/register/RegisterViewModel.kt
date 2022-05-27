package com.dtire.dtireapp.ui.register

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class RegisterViewModel: ViewModel() {
    private val repository = Repository()

    fun registerUser(
        name: String,
        email: String,
        password: String) = repository.registerUser(name, email, password)
}
