package com.zaurh.bober.data.location

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val lat: Double,
    val long: Double
)