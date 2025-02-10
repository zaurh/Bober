package com.zaurh.bober.screen.pager

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zaurh.bober.R
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.account.AccountScreen
import com.zaurh.bober.screen.account.AccountViewModel
import com.zaurh.bober.screen.account.PremiumAlert
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.home.HomeScreen
import com.zaurh.bober.screen.home.HomeViewModel
import com.zaurh.bober.screen.match.MatchScreen
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.components.LikeTokenItem
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagerScreen(
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel,
    matchViewModel: MatchViewModel,
    accountViewModel: AccountViewModel,
    profileViewModel: ProfileViewModel,
    deepLink: String,
    homeViewModel: HomeViewModel,
) {

    val currentUser = chatScreenViewModel.userDataState.collectAsState()
    val scope = rememberCoroutineScope()

    val chatState = chatScreenViewModel.chatState.value
    val newLikesState = chatScreenViewModel.newLikesState.collectAsState()
    val unreadMessagesState = chatScreenViewModel.unreadMessagesState.value

    PremiumAlert(
        homeViewModel = homeViewModel
    )


    val newWhoLikesCount =
        newLikesState.value.newLikes.count { it.new }

    val tabItems = listOf(
        TabItem(
            title = "Bobers",
            selectedIcon = painterResource(id = R.drawable.bober_ic),
            unselectedIcon = painterResource(id = R.drawable.bober_ic)
        ),
        TabItem(
            title = "Chat",
            selectedIcon = painterResource(id = R.drawable.chat_ic),
            unselectedIcon = painterResource(id = R.drawable.chat_ic),
            indicator = if(unreadMessagesState.size < 0) 0 else unreadMessagesState.size
        ),
        TabItem(
            title = "Profile",
            selectedIcon = painterResource(id = R.drawable.profile_ic),
            unselectedIcon = painterResource(id = R.drawable.profile_ic),
            indicator = newWhoLikesCount
        )
    )
    val pagerState = rememberPagerState(initialPage = 1) {
        tabItems.size
    }

    var selectedTabIndex by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(true) {
        if (chatScreenViewModel.chatListState.value.chatList.isEmpty()){
            chatScreenViewModel.getChatList()
        }
        chatScreenViewModel.getUnreadMessagesSize()
        if (newLikesState.value.newLikes.isEmpty()) {
            chatScreenViewModel.getWhoLikes()
        }

        Log.d("sdaslkdj", "${currentUser.value}")
    }


    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        homeViewModel.getUserData()
        if (!chatState.socketIsObserving && !chatState.internetClosed && !chatState.initialized) {
            chatScreenViewModel.connectToChat()
            chatScreenViewModel.webSocketInitialized()
            Log.d("websocketbober", "11111")
        }
    }


    LaunchedEffect(deepLink) {

        if (chatState.recipientMessages?.isNotEmpty() == true && chatState.socketIsObserving){
            when {
                deepLink.startsWith("sendMessage:") -> {
                    val userId = deepLink.removePrefix("sendMessage:")
                    chatScreenViewModel.switchRecipient(userId)
                    chatScreenViewModel.switchRecipientInState(userId)
                    navController.navigate(Screen.ChatScreen.createRoute(userId))
                }
                deepLink.startsWith("gotLiked:") -> {
                    val userId = deepLink.removePrefix("gotLiked:")
                    navController.navigate(Screen.WhoLikesScreen.route)
                }
                deepLink.startsWith("match:") -> {
                    val userId = deepLink.removePrefix("match:")
                    chatScreenViewModel.switchRecipient(userId)
                    chatScreenViewModel.switchRecipientInState(userId)
                    navController.navigate(Screen.ChatScreen.createRoute(userId))
                }
                else -> {


                }
            }
        }
    }


    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }


    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = homeViewModel.isRefreshing.value
    )


    SwipeRefresh(state = swipeRefreshState, onRefresh = {
        chatScreenViewModel.getChatList()
    }) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chatState.recipientMessagesLoading) {
                            Text(
                                "Loading...", color = MaterialTheme.colorScheme.onSecondary, fontFamily = outfitFamily
                            )
                            Spacer(Modifier.size(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = MaterialTheme.colorScheme.onSecondary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.bober_ic),
                                modifier = Modifier.size(26.dp),
                                contentDescription = "bober",
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                text = "Bober",
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.bober_font)),
                                color = MaterialTheme.colorScheme.onSecondary
                            )


                        }

                    }

                }, actions = {
                    AnimatedVisibility(selectedTabIndex == 0) {
                        LikeTokenItem(
                            tokenCount = currentUser.value?.likeToken ?: 0,
                        )
                        Spacer(Modifier.size(8.dp))
                    }

                    IconButton(onClick = {
                        navController.navigate(Screen.SettingsScreen.route)
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                    }
                })
            },
            content = {

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { index ->
                        when (index) {
                            0 -> MatchScreen(
                                navController = navController,
                                matchViewModel = matchViewModel,
                                profileViewModel = profileViewModel
                            )

                            1 -> HomeScreen(
                                navController = navController,
                                chatScreenViewModel = chatScreenViewModel,
                                deepLink = deepLink,
                                homeViewModel = homeViewModel
                            )

                            2 -> AccountScreen(
                                navController = navController,
                                chatScreenViewModel = chatScreenViewModel,
                                accountViewModel = accountViewModel,
                                homeViewModel = homeViewModel
                            )
                        }
                    }


                }
            },
            bottomBar = {
                BottomAppBar {

                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabItems.onEachIndexed { index, tabItem ->
                            Tab(
                                selected = index == selectedTabIndex,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                    selectedTabIndex = index
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tabItem.title,
                                            color = if (tabItem.indicator != 0) colorResource(
                                                R.color.backgroundTop
                                            ) else MaterialTheme.colorScheme.primary,
                                            fontFamily = outfitFamily,
                                            fontSize = 14.sp
                                        )
                                        Spacer(Modifier.size(4.dp))
                                        if (tabItem.indicator != 0) {
                                            Text(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(colorResource(R.color.backgroundTop)),
                                                text = " ${tabItem.indicator} ",
                                                color = Color.White,
                                                fontFamily = outfitFamily
                                            )
                                        }
                                    }
                                },
                                icon = {
                                    Box {
                                        Icon(
                                            modifier = Modifier.size(24.dp),
                                            painter = if (index == selectedTabIndex) {
                                                tabItem.selectedIcon
                                            } else tabItem.unselectedIcon,
                                            contentDescription = tabItem.title,
                                            tint = if (index == selectedTabIndex && tabItem.indicator == 0) MaterialTheme.colorScheme.primary else if (tabItem.indicator != 0) colorResource(
                                                R.color.backgroundTop
                                            ) else Color.Gray
                                        )

                                    }


                                }
                            )
                        }
                    }

                }
            }
        )

    }

}

data class TabItem(
    val title: String,
    val unselectedIcon: Painter,
    val selectedIcon: Painter,
    val indicator: Int = 0
)