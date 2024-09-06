package com.zaurh.bober.domain.repository

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}