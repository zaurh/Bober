package com.zaurh.bober.screen.pager.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.zaurh.bober.R
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.viewmodel.PagerViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForLocationPermission() {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You need to allow us using your location to show you people around",
            textAlign = TextAlign.Center
        )
        Image(
            painterResource(id = R.drawable.location_ic),
            contentDescription = "",
            modifier = Modifier
                .size(200.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            "Don't worry!",
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.size(8.dp))
        Text(
            "Your exact location will remain private, encrypted and will not be shared.",
            textAlign = TextAlign.Center

        )
        Spacer(Modifier.size(12.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ), modifier = Modifier
            .fillMaxWidth(), onClick = {
            if (locationPermissionState.status.shouldShowRationale) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            } else {
                locationPermissionState.launchPermissionRequest()
            }
        }) {
            Text(text = "Allow", color = Color.White)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForDeniedLocation() {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "We see that you denied location sharing.\nIf you want to see people around, you need to enable location from application settings.",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "1.Click Go to settings\n2.Select App permissions\n3.Select Location\n4.Allow",
            textAlign = TextAlign.Center
        )
        Image(
            painterResource(id = R.drawable.location_ic),
            contentDescription = "",
            modifier = Modifier
                .size(200.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            "Don't worry!",
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.size(8.dp))
        Text(
            "Your exact location will remain private, encrypted and will not be shared.",
            textAlign = TextAlign.Center

        )
        Spacer(Modifier.size(12.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.backgroundBottom)
        ), modifier = Modifier
            .fillMaxWidth(), onClick = {
            if (locationPermissionState.status.shouldShowRationale) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            } else {
                locationPermissionState.launchPermissionRequest()
            }
        }) {
            Text(text = "Go to settings", color = Color.White)
        }
    }
}

@Composable
fun AskForEnableGPS() {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "location_enable",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSecondary
        )
        Spacer(Modifier.size(12.dp))
        Text("Location Services", fontSize = 22.sp)
        Spacer(Modifier.size(12.dp))
        Text("You need to turn on location services to see people.")
        Spacer(Modifier.size(12.dp))
        Button(colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ), modifier = Modifier
            .fillMaxWidth(), onClick = {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }) {
            Text(text = "Turn on", color = Color.White)
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetCurrentLocation(
    pagerViewModel: PagerViewModel = hiltViewModel(),
    matchViewModel: MatchViewModel = hiltViewModel()
) {
    Log.d("sdjlkfjklsd", "called")
    val currentLocation = pagerViewModel.currentLocation
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    Log.d("locationBober", locationPermissionState.status.toString())
    Log.d("locationBober", notificationPermission.status.toString())
    Log.d("locationBober", currentLocation.toString())


    LaunchedEffect(key1 = currentLocation) {
        pagerViewModel.getCurrentLocation()
        val jsonData = Json.encodeToString(currentLocation)
        currentLocation?.let {
            matchViewModel.encryptLocation(jsonData)
        }
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        when (locationPermissionState.status.isGranted) {
            true -> {
                if (!notificationPermission.status.isGranted) {
                    notificationPermission.launchPermissionRequest()
                }
                pagerViewModel.getCurrentLocation()
            }

            false -> {
            }
        }
    }
}