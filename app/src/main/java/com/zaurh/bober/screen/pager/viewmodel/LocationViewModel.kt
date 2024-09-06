package com.zaurh.bober.screen.pager.viewmodel

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.location.LocationData
import com.zaurh.bober.domain.repository.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationTracker: LocationTracker
) : ViewModel() {

    var currentLocation by mutableStateOf<LocationData?>(null)

    private fun convertToLocationData(location: Location): LocationData {
        return LocationData(
            lat = location.latitude,
            long = location.longitude
        )
    }

    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            locationTracker.getCurrentLocation()?.let {
                val locationData = convertToLocationData(it)
                currentLocation = locationData
            }
        }
    }

}