package com.dtire.dtireapp.ui.result

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class ResultViewModel: ViewModel() {
    private val repository = Repository()

    fun addToHistory(id: String, condition: String, recommendation: String, imageUrl: String)
        = repository.addToHistory(id, condition, recommendation, imageUrl)
}