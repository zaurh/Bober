package com.zaurh.bober.screen.blocked_users

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.zaurh.bober.screen.blocked_users.components.BU_Content
import com.zaurh.bober.screen.blocked_users.components.BU_TopBar

@Composable
fun BlockedUsersScreen(
    navController: NavController,
    blockedUsersViewModel: BlockedUsersViewModel,
) {

    val blockedUsersState = blockedUsersViewModel.blockedUsersState.value

    LaunchedEffect(true) {
        blockedUsersViewModel.getBlockList()
    }


    Scaffold(
        topBar = {
            BU_TopBar(navController = navController, loading = blockedUsersState.loading)
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

