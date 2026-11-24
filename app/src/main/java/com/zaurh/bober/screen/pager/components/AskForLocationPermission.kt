package com.zaurh.bober.screen.pager.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.viewmodel.PagerViewModel
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.theme.BrandGradient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Shared layout for the location prompts on the discover tab. */
@Composable
private fun LocationPrompt(
    title: String,
    message: String,
    button: String,
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.LocationOn,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(BrandGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(6.dp))
            Text(
                "Your exact location stays private and encrypted.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(32.dp))
        BoberButton(text = button, onClick = onClick)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForLocationPermission() {
    val permission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LocationPrompt(
        title = "See people near you",
        message = "Bober uses your location to show you bobers around and how far away they are.",
        button = "Allow location",
        onClick = { permission.launchPermissionRequest() }
    )
}

@Composable
fun AskForDeniedLocation() {
    val context = LocalContext.current
    LocationPrompt(
        title = "Location is off for Bober",
        message = "To see people around you, open Settings → Permissions → Location and choose Allow.",
        button = "Open settings",
        onClick = {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            )
        }
    )
}

@Composable
fun AskForEnableGPS() {
    val context = LocalContext.current
    LocationPrompt(
        title = "Turn on location services",
        message = "Your phone's location is switched off, so we can't find bobers nearby.",
        button = "Turn on",
        onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetCurrentLocation(
    pagerViewModel: PagerViewModel = hiltViewModel(),
    matchViewModel: MatchViewModel = hiltViewModel()
) {
    val currentLocation = pagerViewModel.currentLocation
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)



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