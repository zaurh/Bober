package com.zaurh.bober.screen.match

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.zaurh.bober.R
import com.zaurh.bober.data.location.isLocationEnabled
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.match.components.EmptyAlert
import com.zaurh.bober.screen.pager.components.AskForDeniedLocation
import com.zaurh.bober.screen.pager.components.AskForEnableGPS
import com.zaurh.bober.screen.pager.components.AskForLocationPermission
import com.zaurh.bober.screen.pager.components.GetCurrentLocation
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.screen.profile.components.AnimatedBorderCard
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSwipeableCardApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MatchScreen(
    matchViewModel: MatchViewModel,
    chatScreenViewModel: ChatScreenViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val currentUser = matchViewModel.userDataState.collectAsState()
    val matchListState = matchViewModel.matchListState.collectAsState()
    val scope = rememberCoroutineScope()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        matchViewModel.getBobers()
    }
    GetCurrentLocation()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface
            )
    ) {
        if (!isLocationEnabled(context)) {
            AskForEnableGPS()
        } else {
            when {
                locationPermissionState.status.isGranted && isLocationEnabled(context) -> {
                    Box {
                        val states = matchListState.value.reversed()
                            .map { it to rememberSwipeableCardState() }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        ) {

                            states.forEach { (matchProfile, state) ->
                                if (state.swipedDirection == null) {
                                    val alphaOffset = state.offset.value.x

                                    matchProfile?.let {
                                        MatchCardItem(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .swipableCard(
                                                    state = state,
                                                    blockedDirections = listOf(Direction.Down),
                                                    onSwiped = {

                                                    },
                                                    onSwipeCancel = {
                                                        Log.d("Swipeable-Card", "Cancelled swipe")
                                                    },
                                                ),
                                            boberData = matchProfile,
                                            profileViewModel = profileViewModel,
                                            matchViewModel = matchViewModel,
                                            onClick = {
                                                navController.navigate(
                                                    Screen.ProfileScreen.passUsername(
                                                        username = matchProfile.username,
                                                    )
                                                ) {
                                                    popUpTo(route = Screen.MatchScreen.route)
                                                }
                                            },
                                            alphaOffSet = alphaOffset
                                        )
                                    }


                                }

                                Row(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    AnimatedBorderCard(
                                        imageVector = Icons.Default.Clear,
                                        imageColor = Color.Red,
                                        imageSize = 60.dp,
                                        gradient = Brush.sweepGradient(
                                            listOf(
                                                colorResource(R.color.likeColor1),
                                                colorResource(R.color.likeColor1),
                                            )
                                        ),
                                        onCardClick = {
                                            scope.launch {
                                                val last = states.reversed()
                                                    .firstOrNull {
                                                        it.second.offset.value == Offset(0f, 0f)
                                                    }?.second

                                                last?.swipe(Direction.Left)
                                            }
                                        }
                                    )
                                    AnimatedBorderCard(
                                        imageVector = Icons.Default.Favorite,
                                        imageColor = colorResource(R.color.green),
                                        imageSize = 60.dp,
                                        gradient = Brush.sweepGradient(
                                            listOf(
                                                colorResource(R.color.likeColor2),
                                                colorResource(R.color.likeColor2)
                                            )
                                        ),
                                        onCardClick = {
                                            scope.launch {
                                                val last = states.reversed()
                                                    .firstOrNull {
                                                        it.second.offset.value == Offset(0f, 0f)
                                                    }?.second

                                                last?.swipe(Direction.Right)
                                            }
                                        }
                                    )

                                }

                                LaunchedEffect(matchProfile, state.swipedDirection) {
//                        chatScreenViewModel.switchRecipient(
//                            recipientUserId = matchProfile?.userId ?: ""
//                        )
                                    if (state.swipedDirection != null) {
                                        when (state.swipedDirection) {
                                            Direction.Right -> {
                                                matchViewModel.sendLike(matchProfile?.userId ?: "")
                                            }

                                            Direction.Left -> {
                                                matchViewModel.pass(matchProfile?.userId ?: "")
                                            }

                                            Direction.Up -> {
                                                navController.navigate(
                                                    Screen.ProfileScreen.passUsername(
                                                        username = matchProfile?.username,
                                                    )
                                                )
                                            }

                                            Direction.Down -> {}
                                            else -> {

                                            }
                                        }
                                    }
                                }
                            }


                        }
                        if (states.isEmpty()) {
                            EmptyAlert()
                        }


                    }
                }

                locationPermissionState.status.shouldShowRationale -> {
                    AskForDeniedLocation()
                }

                !locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale -> {
                    AskForLocationPermission()
                }
            }

        }

    }


}








