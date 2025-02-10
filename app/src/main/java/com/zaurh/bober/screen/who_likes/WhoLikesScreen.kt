package com.zaurh.bober.screen.who_likes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.who_likes.components.WL_Content
import com.zaurh.bober.screen.who_likes.components.WL_TopBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WhoLikesScreen(
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel,
    whoLikesViewModel: WhoLikesViewModel
) {
    val newLikesState = chatScreenViewModel.newLikesState.collectAsState()

    LaunchedEffect(true) {
        if (newLikesState.value.newLikes.isEmpty()){
            chatScreenViewModel.getWhoLikes()
        }
    }
//    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
//        whoLikesViewModel.getWhoLikesList()
//    }

    Scaffold(
        topBar = {
            WL_TopBar(navController = navController)
        },
        content = { paddingValues ->
            WL_Content(
                whoLikesViewModel = whoLikesViewModel,
                chatScreenViewModel = chatScreenViewModel,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    )

}