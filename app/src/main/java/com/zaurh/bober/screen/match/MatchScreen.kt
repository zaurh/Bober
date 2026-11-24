package com.zaurh.bober.screen.match

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.SwipeableCardState
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.zaurh.bober.R
import com.zaurh.bober.data.location.isLocationEnabled
import com.zaurh.bober.data.user.BoberData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.pager.components.AskForDeniedLocation
import com.zaurh.bober.screen.pager.components.AskForEnableGPS
import com.zaurh.bober.screen.pager.components.AskForLocationPermission
import com.zaurh.bober.screen.pager.components.GetCurrentLocation
import com.zaurh.bober.ui.components.CircleActionButton
import com.zaurh.bober.ui.components.EmptyState
import com.zaurh.bober.ui.theme.BoberPalette
import kotlinx.coroutines.launch

/** How many cards are composed at once; the rest wait below. */
private const val VISIBLE_CARDS = 3

@OptIn(ExperimentalSwipeableCardApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MatchScreen(
    matchViewModel: MatchViewModel,
    chatScreenViewModel: ChatScreenViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val permission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    when {
        !isLocationEnabled(context) -> AskForEnableGPS()
        permission.status.isGranted -> {
            GetCurrentLocation()
            Deck(matchViewModel, navController)
        }
        permission.status.shouldShowRationale -> AskForDeniedLocation()
        else -> AskForLocationPermission()
    }
}

@OptIn(ExperimentalSwipeableCardApi::class)
@Composable
private fun Deck(matchViewModel: MatchViewModel, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profiles = matchViewModel.matchListState.collectAsState().value.filterNotNull()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { matchViewModel.getBobers() }
    LaunchedEffect(Unit) {
        matchViewModel.messages.collect { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    // One swipe state per profile, keyed by id so states survive list refreshes.
    val states: Map<String, SwipeableCardState> = profiles.associate { profile ->
        profile.userId to key(profile.userId) { rememberSwipeableCardState() }
    }
    val remaining = profiles.filter { states[it.userId]?.swipedDirection == null }
    val top = remaining.firstOrNull()

    fun openProfile(profile: BoberData) {
        navController.navigate(Screen.ProfileScreen.passUsername(username = profile.username))
    }

    fun swipeTop(direction: Direction) {
        val state = top?.let { states[it.userId] } ?: return
        scope.launch { state.swipe(direction) }
    }

    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                remaining.isEmpty() && matchViewModel.loading.value ->
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

                remaining.isEmpty() -> EmptyState(
                    title = "You've seen everyone nearby",
                    message = "Check back later, or widen your distance and age range in Settings.",
                    animation = R.raw.empty_animation,
                )

                else -> remaining.take(VISIBLE_CARDS).asReversed().forEachIndexed { index, profile ->
                    val state = states.getValue(profile.userId)
                    val depth = remaining.take(VISIBLE_CARDS).size - 1 - index // 0 = top card
                    key(profile.userId) {
                        MatchCardItem(
                            boberData = profile,
                            dragX = state.offset.value.x,
                            onOpenProfile = { openProfile(profile) },
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    // Cards underneath peek out slightly smaller.
                                    val scale = 1f - depth * 0.04f
                                    scaleX = scale
                                    scaleY = scale
                                    translationY = depth * 18f
                                }
                                .then(
                                    if (depth == 0) Modifier.swipableCard(
                                        state = state,
                                        blockedDirections = listOf(Direction.Up, Direction.Down),
                                        onSwiped = {},
                                    ) else Modifier
                                )
                        )
                        LaunchedEffect(state.swipedDirection) {
                            when (state.swipedDirection) {
                                Direction.Right -> matchViewModel.sendLike(profile.userId)
                                Direction.Left -> matchViewModel.pass(profile.userId)
                                else -> Unit
                            }
                        }
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleActionButton(Icons.Default.Close, "Pass", BoberPalette.Pass, onClick = { swipeTop(Direction.Left) }, enabled = top != null)
            CircleActionButton(
                Icons.Outlined.Info, "Open profile", MaterialTheme.colorScheme.secondary,
                onClick = { top?.let(::openProfile) }, size = 48.dp, enabled = top != null
            )
            CircleActionButton(Icons.Default.Favorite, "Like", BoberPalette.Like, onClick = { swipeTop(Direction.Right) }, enabled = top != null)
        }
    }
}
