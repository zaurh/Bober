package com.zaurh.bober.screen.profile

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.zaurh.bober.screen.profile.components.ProfileContent
import com.zaurh.bober.screen.profile.components.Profile_TopBar
import com.zaurh.bober.util.calculateAge

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    username: String,
    profileViewModel: ProfileViewModel,
    navController: NavController
) {
    val profileData = profileViewModel.profileDataState.collectAsState()

    LaunchedEffect(key1 = true) {
        profileViewModel.getProfileID(username)
    }

    BackHandler(onBack = {
        profileViewModel.clearProfileData()
        navController.popBackStack()
    })

    profileData.value.let { user ->
        if (user != null) {
            Scaffold(
                topBar = {
                    val age = calculateAge(profileData.value?.birthDate ?: "").toString()

                    Profile_TopBar(
                        navController = navController,
                        username = username,
                        age = age,
                        profileViewModel = profileViewModel
                    )
                },
                content = { paddingValues ->
                    ProfileContent(
                        paddingValues = paddingValues,
                        profileViewModel = profileViewModel,
                        user = user,
                        navController = navController
                    )
                }
            )

        }

    }

}


