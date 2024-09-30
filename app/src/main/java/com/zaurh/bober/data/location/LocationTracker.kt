package com.zaurh.bober.data.location

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.zaurh.bober.domain.repository.LocationTracker
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class DefaultLocationTracker(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        // Check for FINE location permission
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Check for COARSE location permission
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Access LocationManager to check GPS status
        val locationManager = application.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        // If neither GPS nor network location is enabled, or no permissions are granted, return null
        if (!isGpsEnabled && !isNetworkEnabled) return null

        if (!hasAccessFineLocationPermission && !hasAccessCoarseLocationPermission) return null

        // Start location retrieval using a suspendCancellableCoroutine
        return suspendCancellableCoroutine { cont ->
            try {
                fusedLocationProviderClient.lastLocation.apply {
                    // Check if the location task is already complete
                    if (isComplete) {
                        Log.d("locationBober", "Result: $result")

                        if (isSuccessful && result != null) {
                            cont.resume(result)
                        } else {
                            requestRealTimeLocation(cont) // Request real-time location if lastLocation is null or failed
                        }
                        return@suspendCancellableCoroutine
                    }

                    // Add listener for success case
                    addOnSuccessListener { location ->
                        if (location != null) {
                            Log.d("locationBober", "Location: $location")

                            cont.resume(location)
                        } else {
                            requestRealTimeLocation(cont) // Request real-time location if lastLocation is null
                        }
                    }

                    // Add listener for failure case
                    addOnFailureListener {
                        requestRealTimeLocation(cont) // Request real-time location if failure occurs
                    }

                    // Add listener for cancellation case
                    addOnCanceledListener {
                        cont.cancel()
                    }
                }
            } catch (e: SecurityException) {
                cont.resume(null)
            }
        }
    }

    // Request real-time location updates
    private fun requestRealTimeLocation(cont: CancellableContinuation<Location?>) {
        // Check permissions before calling requestLocationUpdates
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessFineLocationPermission && !hasAccessCoarseLocationPermission) {
            cont.resume(null)
            return
        }

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                },
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        locationResult.locations.let { locations ->
                            Log.d("locationBober", "Location: ${locations.last()}")

                            if (locations.isNotEmpty()) {
                                cont.resume(locations.last()) // Get the most recent location
                            } else {
                                cont.resume(null)
                            }
                        }
                    }
                },
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }
}



fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    return isGpsEnabled || isNetworkEnabled
}