package com.zaurh.bober.screen.who_likes

import com.zaurh.bober.ui.components.BackTopBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.screen.who_likes.components.WL_Content

@Composable
fun WhoLikesScreen(
    navController: NavController,
    whoLikesViewModel: WhoLikesViewModel
) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        whoLikesViewModel.getWhoLikesList()
    }

    Scaffold(
        topBar = {
            BackTopBar(title = "Likes you", onBack = { navController.popBackStack() })
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