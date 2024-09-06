package com.zaurh.bober.screen.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.zaurh.bober.screen.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile_TopBar(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    username: String,
    age: String
) {

    TopAppBar(title = {
        val ageText = if (age != "0") ", $age" else ""
        Text(text = "$username$ageText")
    }, navigationIcon = {
        IconButton(onClick = {
            navController.popBackStack()
            profileViewModel.clearProfileData()
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
    })
}