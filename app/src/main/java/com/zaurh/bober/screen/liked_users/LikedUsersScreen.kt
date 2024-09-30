package com.zaurh.bober.screen.liked_users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.screen.liked_users.components.LU_Content
import com.zaurh.bober.screen.liked_users.components.LU_TopBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikedUsersScreen(
    navController: NavController,
    likedUsersViewModel: LikedUsersViewModel = hiltViewModel()
) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        likedUsersViewModel.getLikedUsers()
    }

    Scaffold(
        topBar = {
            LU_TopBar(navController = navController)
        },
        content = { paddingValues ->
            LU_Content(
                likedUsersViewModel = likedUsersViewModel,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    )

}