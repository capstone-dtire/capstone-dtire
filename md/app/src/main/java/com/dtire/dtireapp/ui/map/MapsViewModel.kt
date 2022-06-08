package com.dtire.dtireapp.ui.map

import androidx.lifecycle.ViewModel
import com.dtire.dtireapp.data.Repository

class MapsViewModel: ViewModel() {
    private val repository = Repository()

    fun getNearbyPlace(url: String) = repository.getNearbyPlaces(url)
}