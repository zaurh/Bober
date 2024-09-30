package com.zaurh.bober.screen.pager.viewmodel

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.location.LocationData
import com.zaurh.bober.domain.repository.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagerViewModel @Inject constructor(
    private val locationTracker: LocationTracker
) : ViewModel() {

    var currentLocation by mutableStateOf<LocationData?>(null)

    private val _likeTokenExpanded = mutableStateOf(false)
    val likeTokenExpanded: State<Boolean> = _likeTokenExpanded


    fun expandLikeToken(){
        viewModelScope.launch {
            Log.d("sdfsdfdfs", likeTokenExpanded.value.toString())
            _likeTokenExpanded.value = true
            Log.d("sdfsdfdfs", likeTokenExpanded.value.toString())
            delay(2000)
            Log.d("sdfsdfdfs", likeTokenExpanded.value.toString())

            _likeTokenExpanded.value = false
            Log.d("sdfsdfdfs", likeTokenExpanded.value.toString())

        }
    }

    private fun convertToLocationData(location: Location): LocationData {
        return LocationData(
            lat = location.latitude,
            long = location.longitude
        )
    }

    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            locationTracker.getCurrentLocation()?.let {
                Log.d("locationBober", "locationviewmodel: $it")
                val locationData = convertToLocationData(it)
                currentLocation = locationData
            }
        }
    }

}