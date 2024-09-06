package com.zaurh.bober.screen.blocked_users

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.zaurh.bober.screen.blocked_users.components.BU_Content
import com.zaurh.bober.screen.blocked_users.components.BU_TopBar
import com.zaurh.bober.screen.liked_users.components.LU_Content

@Composable
fun BlockedUsersScreen(
    navController: NavController,
    blockedUsersViewModel: BlockedUsersViewModel,
) {

    Scaffold(
        topBar = {
            BU_TopBar(navController = navController)
        },
        content = { paddingValues ->
            BU_Content(
                blockedUsersViewModel = blockedUsersViewModel,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    )

}

