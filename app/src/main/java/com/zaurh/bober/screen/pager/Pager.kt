package com.zaurh.bober.screen.pager

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.zaurh.bober.R
import com.zaurh.bober.data.message.MessageStatus
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.account.AccountScreen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.home.HomeScreen
import com.zaurh.bober.screen.home.HomeViewModel
import com.zaurh.bober.screen.match.MatchScreen
import com.zaurh.bober.screen.match.MatchViewModel
import com.zaurh.bober.screen.pager.components.LocationPermission
import com.zaurh.bober.services.RunningService
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagerScreen(
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel,
    matchViewModel: MatchViewModel,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val currentUser = chatScreenViewModel.userDataState.collectAsState()

    val chatState = chatScreenViewModel.state.value

    val newMessageCount =
        chatState.messages.count { it.status != MessageStatus.READ && it.recipientUserId == currentUser.value?.id }

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
            indicator = newMessageCount
        ),
        TabItem(
            title = "Profile",
            selectedIcon = painterResource(id = R.drawable.profile_ic),
            unselectedIcon = painterResource(id = R.drawable.profile_ic),
        )
    )
    val pagerState = rememberPagerState {
        tabItems.size
    }

    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(1)
    }


    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        if (currentUser.value == null){
            homeViewModel.getAllUsers()
            homeViewModel.getUserData()
        }

    }



    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }


    fun stopForegroundService(context: Context) {
        val intent = Intent(context, RunningService::class.java)
        context.stopService(intent)
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        if (!chatState.socketIsObserving){
            stopForegroundService(context)
            chatScreenViewModel.connectToChat(context)
        }

    }



    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chatState.isLoading){
                        Text("Loading...")
                        Spacer(Modifier.size(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(14.dp),color = MaterialTheme.colorScheme.secondary, strokeWidth = 2.dp)
                    }else{
                        Text(text = "Bober")
                    }

                }

            }, actions = {
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
                            chatScreenViewModel = chatScreenViewModel
                        )

                        1 -> HomeScreen(
                            navController = navController,
                            chatScreenViewModel = chatScreenViewModel,
                            homeViewModel = homeViewModel
                        )

                        2 -> AccountScreen(navController = navController)
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
                                selectedTabIndex = index
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = tabItem.title, color = if (tabItem.indicator != 0) colorResource(
                                        R.color.backgroundTop
                                    ) else colorResource(R.color.darkBlue)
                                    )
                                    Spacer(Modifier.size(4.dp))
                                    if (tabItem.indicator != 0) {
                                        Text(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(colorResource(R.color.backgroundTop)),
                                            text = " ${tabItem.indicator} ",
                                            color = Color.White
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

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LocationPermission()
    }

}

data class TabItem(
    val title: String,
    val unselectedIcon: Painter,
    val selectedIcon: Painter,
    val indicator: Int = 0
)