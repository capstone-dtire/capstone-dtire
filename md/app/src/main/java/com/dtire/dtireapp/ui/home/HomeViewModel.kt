package com.dtire.dtireapp.ui.home

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class HomeViewModel: ViewModel() {
    private val repository = Repository()

    fun getUser(id: String) = repository.getUser(id)
}