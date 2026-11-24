package com.zaurh.bober.screen.pager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaurh.bober.data.location.LocationData
import com.zaurh.bober.data.remote.NetworkJson
import com.zaurh.bober.domain.repository.LocationTracker
import com.zaurh.bober.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Keeps the backend's copy of the device location fresh; it's used for distances on the discover deck. */
@HiltViewModel
class PagerViewModel @Inject constructor(
    private val locationTracker: LocationTracker,
    private val userRepository: UserRepository,
) : ViewModel() {

    private var job: Job? = null
    private var lastUploadAt = 0L

    /** Reads the location once and uploads it, at most every [MIN_INTERVAL_MS]. */
    fun refreshLocation(force: Boolean = false) {
        if (job?.isActive == true) return
        if (!force && System.currentTimeMillis() - lastUploadAt < MIN_INTERVAL_MS) return
        job = viewModelScope.launch {
            val location = locationTracker.getCurrentLocation() ?: return@launch
            val json = NetworkJson.encodeToString(LocationData.serializer(), LocationData(location.latitude, location.longitude))
            if (userRepository.encryptLocation(json).success) lastUploadAt = System.currentTimeMillis()
        }
    }

    private companion object {
        const val MIN_INTERVAL_MS = 15 * 60 * 1000L
    }
}
