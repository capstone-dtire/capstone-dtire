package com.dtire.dtireapp.ui.profile

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class ProfileViewModel: ViewModel() {
    private val repository = Repository()

    fun getData(id: String) = repository.getUser(id)
}