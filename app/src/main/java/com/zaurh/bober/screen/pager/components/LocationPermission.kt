package com.zaurh.bober.screen.pager.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.zaurh.bober.screen.account.components.LocationAllowDialog
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.viewmodel.LocationViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(
    locationViewModel: LocationViewModel = hiltViewModel(),
    matchViewModel: MatchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentLocation = locationViewModel.currentLocation

    val locationPermissions = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    var alertState by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = currentLocation) {
        locationViewModel.getCurrentLocation()
        val jsonData = Json.encodeToString(currentLocation)
        currentLocation?.let {
            matchViewModel.encryptLocation(jsonData)
        }
    }

    LaunchedEffect(locationPermissions.status.isGranted) {
        when (locationPermissions.status.isGranted) {
            true -> {
                if (!notificationPermission.status.isGranted){
                    notificationPermission.launchPermissionRequest()
                }

                alertState = false
                locationViewModel.getCurrentLocation()
            }
            false -> {
                alertState = true
            }
        }
    }

    LocationAllowDialog(dialogOpen = alertState, onClick = {
        if (locationPermissions.status.shouldShowRationale){
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }else{
            locationPermissions.launchPermissionRequest()
        }
    }, onDismiss = {
        alertState = false
    })

}