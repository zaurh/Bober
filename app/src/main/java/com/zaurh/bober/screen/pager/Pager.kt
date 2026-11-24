package com.zaurh.bober.screen.pager

import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.zaurh.bober.ui.components.BrandWordmark

private enum class Tab(val title: String, val icon: Int) {
    Discover("Discover", R.drawable.bober_ic),
    Chats("Chats", R.drawable.chat_ic),
    Profile("Profile", R.drawable.profile_ic),
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PagerScreen(
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel,
    matchViewModel: MatchViewModel,
    homeViewModel: HomeViewModel,
) {
    val context = LocalContext.current
    val currentUser = chatScreenViewModel.userDataState.collectAsState()
    val chatState = chatScreenViewModel.state.value
    val unread = chatState.messages.count {
        it.status != MessageStatus.READ && it.recipientUserId == currentUser.value?.id
    }

    var selected by rememberSaveable { mutableIntStateOf(Tab.Chats.ordinal) }
    val pagerState = rememberPagerState(initialPage = selected) { Tab.entries.size }
    LaunchedEffect(selected) { pagerState.scrollToPage(selected) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (currentUser.value == null) homeViewModel.getUserData()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Back in the foreground: messages are resynced.
        chatScreenViewModel.connectToChat(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandWordmark()
                        if (chatState.isLoading) {
                            Spacer(Modifier.size(10.dp))
                            CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon = {
                    // Likes cost a token; show the balance while swiping.
                    AnimatedVisibility(visible = pagerState.currentPage == Tab.Discover.ordinal && currentUser.value != null) {
                        val user = currentUser.value
                        LikeTokenPill(
                            tokens = user?.likeToken ?: 0,
                            unlimited = user?.isPremium == true,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.SettingsScreen.route) }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab.ordinal,
                        onClick = { selected = tab.ordinal },
                        icon = {
                            BadgedBox(badge = {
                                if (tab == Tab.Chats && unread > 0) Badge { Text(if (unread > 99) "99+" else "$unread") }
                            }) {
                                Icon(painterResource(tab.icon), contentDescription = null, modifier = Modifier.size(24.dp))
                            }
                        },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                }
            }
        }
    ) { padding ->
        // Pull down on a scrolling list (chats, profile) to reload everything.
        val refreshState = rememberPullToRefreshState()
        if (refreshState.isRefreshing) {
            LaunchedEffect(Unit) {
                homeViewModel.refresh()
                chatScreenViewModel.connectToChat(context) // reloads messages
                if (pagerState.currentPage == Tab.Discover.ordinal) matchViewModel.getBobers()
            }
        }
        val refreshing = homeViewModel.refreshing.value
        LaunchedEffect(refreshing) {
            if (!refreshing) refreshState.endRefresh()
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(refreshState.nestedScrollConnection)
        ) {
        // Swiping between tabs is off: horizontal swipes belong to the discover cards.
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (Tab.entries[page]) {
                Tab.Discover -> MatchScreen(
                    navController = navController,
                    matchViewModel = matchViewModel,
                )
                Tab.Chats -> HomeScreen(
                    navController = navController,
                    chatScreenViewModel = chatScreenViewModel,
                    homeViewModel = homeViewModel
                )
                Tab.Profile -> AccountScreen(navController = navController)
            }
        }
        PullToRefreshContainer(state = refreshState, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

/** Remaining like tokens. Premium accounts (and the backend's "unlimited" balances) show ∞. */
@Composable
private fun LikeTokenPill(tokens: Int, unlimited: Boolean, modifier: Modifier = Modifier) {
    val empty = !unlimited && tokens <= 0
    Surface(
        shape = CircleShape,
        color = if (empty) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (empty) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
    ) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(4.dp))
            Text(
                if (unlimited || tokens > 999) "∞" else "$tokens",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
