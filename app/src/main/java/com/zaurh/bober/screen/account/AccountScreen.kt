package com.zaurh.bober.screen.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.account.components.AccountScreenItem
import com.zaurh.bober.screen.account.components.ImageCard

@Composable
fun AccountScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val state = rememberMessageBarState()
    val currentUser = accountViewModel.userDataState.collectAsState()
    val whoLikesState = accountViewModel.whoLikesState.collectAsState()

    LaunchedEffect(true) {
        accountViewModel.getWhoLikes()
        if (currentUser.value?.emailIsVerified == false){
            state.addError(exception = Exception("Your email address has not been verified"))
        }
    }

    ContentWithMessageBar(
        verticalPadding = 6.dp,
        messageBarState = state,
        errorMaxLines = 3,
        successMaxLines = 3,
        showCopyButton = false,
        content = {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                currentUser.value?.let { user ->
                    Box {
                        val profilePic = user.imageUrl?.firstOrNull() ?: ""
                        AsyncImage(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape)
                                .clickable {
                                    navController.navigate(Screen.ProfileScreen.passUsername(user.username))
                                },
                            model = profilePic,
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                        IconButton(colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ), modifier = Modifier.align(Alignment.BottomEnd), onClick = {
                            navController.navigate(Screen.EditProfileScreen.route)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "@${user.username}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = user.name ?: "", color = MaterialTheme.colorScheme.primary)



                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

                        val whoLikesNew = whoLikesState.value.filter { it.new }
                        val whoLikesCount =
                            if (whoLikesNew.count() < 100) whoLikesNew.count() else "99+"

                        AccountScreenItem(
                            icon = R.drawable.who_likes_me_ic,
                            title = "Who likes me?",
                            count = "$whoLikesCount"
                        ) {
                            navController.navigate(Screen.WhoLikesScreen.route)
                        }
                        AccountScreenItem(icon = R.drawable.liked_users_ic, title = "Liked users") {
                            navController.navigate(Screen.LikedUsersScreen.route)
                        }
                        AccountScreenItem(
                            icon = R.drawable.blocked_users_ic,
                            title = "Blocked users"
                        ) {
                            navController.navigate(Screen.BlockedUsersScreen.route)
                        }
                    }
                    Spacer(Modifier.size(16.dp))
                    if (user.isPremium == false) {
                        ImageCard(onClick = {
                            accountViewModel.boberium()
                        })
                    }

                }


            }
        })

}




