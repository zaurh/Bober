package com.zaurh.bober.screen.who_likes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.zaurh.bober.screen.who_likes.components.WL_Content
import com.zaurh.bober.screen.who_likes.components.WL_TopBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WhoLikesScreen(
    navController: NavController,
    whoLikesViewModel: WhoLikesViewModel
) {
    Scaffold(
        topBar = {
            WL_TopBar(navController = navController)
        },
        content = { paddingValues ->
            WL_Content(
                whoLikesViewModel = whoLikesViewModel,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    )

}