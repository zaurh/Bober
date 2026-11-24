package com.zaurh.bober.screen.blocked_users

import com.zaurh.bober.ui.components.BackTopBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.screen.blocked_users.components.BU_Content

@Composable
fun BlockedUsersScreen(
    navController: NavController,
    blockedUsersViewModel: BlockedUsersViewModel,
) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        blockedUsersViewModel.getBlockList()
    }


    Scaffold(
        topBar = {
            BackTopBar(title = "Blocked users", onBack = { navController.popBackStack() })
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

