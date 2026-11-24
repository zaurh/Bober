package com.zaurh.bober.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.zaurh.bober.domain.repository.LocationTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await

class DefaultLocationTracker(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    /**
     * One location fix, or null without permission / location services. Uses a fresh fix when the
     * cached one is missing; the request ends by itself (no lingering updates draining the battery).
     */
    @OptIn(ExperimentalCoroutinesApi::class) // Task.await(CancellationTokenSource)
    @SuppressLint("MissingPermission") // checked by hasPermission()
    override suspend fun getCurrentLocation(): Location? {
        if (!hasPermission() || !isLocationEnabled(application)) return null
        return try {
            fusedLocationProviderClient.lastLocation.await()
                ?: CancellationTokenSource().let { cancellation ->
                    fusedLocationProviderClient
                        .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token)
                        .await(cancellation)
                }
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            Log.w(TAG, "Couldn't get location", e)
            null
        }
    }

    private fun hasPermission(): Boolean =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any {
            ContextCompat.checkSelfPermission(application, it) == PackageManager.PERMISSION_GRANTED
        }

    private companion object {
        const val TAG = "LocationTracker"
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
