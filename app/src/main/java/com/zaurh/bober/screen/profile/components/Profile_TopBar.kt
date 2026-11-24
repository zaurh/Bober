package com.zaurh.bober.screen.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import com.zaurh.bober.util.calculateAge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile_TopBar(
    navController: NavController,
    profileViewModel: ProfileViewModel,
) {
    val profileData = profileViewModel.profileDataState.collectAsState()

    TopAppBar(title = {
        profileData.value?.let {
            val age = calculateAge(profileData.value?.birthDate ?: "").toString()
            val ageText = if (age != "0") ", $age" else ""
            Text(text = "${it.username}$ageText", fontFamily = outfitFamily)
        }

    }, navigationIcon = {
        IconButton(onClick = {
            navController.popBackStack()
            profileViewModel.clearProfileData()
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
    })
}