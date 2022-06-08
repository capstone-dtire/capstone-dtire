package com.dtire.dtireapp.ui.history

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class HistoryViewModel: ViewModel() {
    private val repository = Repository()

    fun getHistory(id: String) = repository.getHistory(id)
}