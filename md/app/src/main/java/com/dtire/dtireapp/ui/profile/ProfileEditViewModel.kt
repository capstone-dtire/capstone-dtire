package com.dtire.dtireapp.ui.profile

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository
import com.dtire.dtireapp.data.response.UserItem

class ProfileEditViewModel: ViewModel() {
    private val repository = Repository()

    fun updateUser(id: String, userData: UserItem) = repository.updateUser(id, userData)
}