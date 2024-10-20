package com.zaurh.bober.screen.settings.account_center

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.bober.screen.settings.account_center.components.AC_Content
import com.zaurh.bober.screen.settings.account_center.components.AC_TopBar

@Composable
fun AccountCenterScreen(
    navController: NavController
) {

    Scaffold(
        topBar = {
            AC_TopBar(navController = navController)
        },
        content = { paddingValues ->
            AC_Content(
                paddingValues = paddingValues,
                navController = navController
            )
        }
    )
}