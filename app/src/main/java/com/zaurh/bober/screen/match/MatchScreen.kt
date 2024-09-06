package com.zaurh.bober.screen.match

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.zaurh.bober.R
import com.zaurh.bober.data.user.Gender
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.match.components.EmptyAlert
import com.zaurh.bober.screen.match.components.GotMatchAlert
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.util.calculateAge
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSwipeableCardApi::class)
@Composable
fun MatchScreen(
    matchViewModel: MatchViewModel,
    chatScreenViewModel: ChatScreenViewModel,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val currentUser = matchViewModel.userDataState.collectAsState()
    val userDataList = matchViewModel.userListDataState.collectAsState()
    val scope = rememberCoroutineScope()
    val gotMatchState = chatScreenViewModel.gotMatch.value

    AnimatedVisibility(
        visible = gotMatchState.gotMatch,
        enter = fadeIn(animationSpec = tween(2000)),
        exit = fadeOut(animationSpec = tween(2000))
    ) {
        val recipientUser = userDataList.value.find { it?.id == gotMatchState.recipientId}
        val recipientImage = recipientUser?.imageUrl?.first().orEmpty()
        GotMatchAlert(recipientImage = recipientImage)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface
            )
    ) {
        Box {
            val users = userDataList.value
                .filter {

                    val distance = matchViewModel.distance.value
                    val blockList = currentUser.value?.blockList ?: listOf()
                    val likedUsers = currentUser.value?.likedUsers ?: listOf()
                    val passedUsers = currentUser.value?.passedUsers?.map { it.userId } ?: listOf()
                    val ageRangeStart = currentUser.value?.ageRangeStart ?: 18f
                    val ageRangeEnd = currentUser.value?.ageRangeEnd ?: 100f
                    val maximumDistance = currentUser.value?.maximumDistance ?: 200f
                    val showFullDistance = currentUser.value?.showFullDistance ?: true

                    val showDistanceFilter =
                        if (!showFullDistance) distance < maximumDistance else true

                    val showMe = currentUser.value?.showMe?.displayName ?: Gender.PREFER_NOT_TO_SAY

                    val userAge = calculateAge(it?.birthDate ?: "")
                    val userGender = it?.gender?.displayName ?: Gender.PREFER_NOT_TO_SAY


                    fun compareShowMeAndGender(): Boolean {
                        return if (showMe != "Everyone") {
                            showMe == userGender
                        } else {
                            true
                        }
                    }

                    it?.id != currentUser.value?.id
                            && !blockList.contains(it?.id)
                            && !likedUsers.contains(it?.id)
                            && userAge > ageRangeStart
                            && userAge < ageRangeEnd
                            && compareShowMeAndGender()
                            && showDistanceFilter
                            && !passedUsers.contains(it?.id)
                }

            val states = users.reversed()
                .map { it to rememberSwipeableCardState() }

            Box(
                Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {

                states.forEach { (matchProfile, state) ->
                    if (state.swipedDirection == null) {

                        val alphaOffset = state.offset.value.x

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
                            profileData = matchProfile ?: UserData(),
                            profileViewModel = profileViewModel,
                            matchViewModel = matchViewModel,
                            onClick = {
                                navController.navigate(
                                    Screen.ProfileScreen.passUsername(
                                        username = matchProfile?.username,
                                    )
                                ) {
                                    popUpTo(route = Screen.MatchScreen.route)
                                }
                            },
                            alphaOffSet = alphaOffset
                        )


                    }

                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        IconButton(colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.pink)
                        ), modifier = Modifier
                            .size(60.dp), onClick = {

                            scope.launch {
                                val last = states.reversed()
                                    .firstOrNull {
                                        it.second.offset.value == Offset(0f, 0f)
                                    }?.second

                                last?.swipe(Direction.Left)
                            }

                        }) {
                            Icon(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(8.dp),
                                imageVector = Icons.Default.Clear,
                                contentDescription = "",
                                tint = Color.Red
                            )
                        }
                        IconButton(modifier = Modifier
                            .size(60.dp), colors = IconButtonDefaults.iconButtonColors(
                            containerColor = colorResource(id = R.color.pink)
                        ), onClick = {
                            scope.launch {
                                val last = states.reversed()
                                    .firstOrNull {
                                        it.second.offset.value == Offset(0f, 0f)
                                    }?.second

                                last?.swipe(Direction.Right)
                            }
                        }) {
                            Icon(
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(8.dp),
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "",
                                tint = colorResource(id = R.color.green)
                            )
                        }
                    }

                    LaunchedEffect(matchProfile, state.swipedDirection) {
                        chatScreenViewModel.switchRecipient(
                            recipientUserId = matchProfile?.id ?: ""
                        )
                        if (state.swipedDirection != null) {
                            when (state.swipedDirection) {
                                Direction.Right -> {
                                    matchViewModel.sendLike(matchProfile?.id ?: "")
                                }

                                Direction.Left -> {
                                    matchViewModel.pass(matchProfile?.id ?: "")
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



}







