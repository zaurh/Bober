package com.zaurh.bober.screen.account

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import com.zaurh.bober.R
import com.zaurh.bober.data.requests.PremiumCardRequest
import com.zaurh.bober.data.user.PremiumCards
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.account.components.AccountScreenItem
import com.zaurh.bober.screen.account.components.BoberiumCard
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.home.HomeViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun AccountScreen(
    navController: NavController,
    chatScreenViewModel: ChatScreenViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel
) {
    val state = rememberMessageBarState()
    val currentUser = accountViewModel.userDataState.collectAsState()
    val whoLikesState = chatScreenViewModel.newLikesState.collectAsState()

    LaunchedEffect(true) {
        if (currentUser.value?.emailIsVerified == false) {
            state.addError(exception = Exception("Your email address has not been verified"))
        }
        Log.d("dsajlkdsakl", "${whoLikesState.value.newLikes.filter { it.new }}")
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
                                    navController.navigate(
                                        Screen.ProfileScreen.createRoute(
                                            recipientId = user.id ?: ""
                                        )
                                    )
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
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = outfitFamily,
                        fontSize = 16.sp
                    )
                    Text(
                        text = user.name ?: "",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = outfitFamily,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.size(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

                        val whoLikesNew = whoLikesState.value.newLikes.filter { it.new }
                        val whoLikesCount =
                            if (whoLikesNew.count() < 100) whoLikesNew.count() else "99+"

                        AccountScreenItem(
                            icon = R.drawable.who_likes_me_ic,
                            title = "New likes",
                            count = "$whoLikesCount"
                        ) {
                            navController.navigate(Screen.WhoLikesScreen.route)
                        }
                        AccountScreenItem(icon = R.drawable.liked_users_ic, title = "Liked users") {
                            navController.navigate(Screen.LikedUsersScreen.route)
                        }
                        AccountScreenItem(
                            icon = R.drawable.blocked_users_ic,
                            title = "Blocked"
                        ) {
                            navController.navigate(Screen.BlockedUsersScreen.route)
                        }
                    }
                    Spacer(Modifier.size(16.dp))

                    val boberiumCard = user.premiumCards?.find { it.name == PremiumCards.BOBERIUM }

                    boberiumCard?.let {
                        val currentTime = System.currentTimeMillis()
                        val expiresDate = it.expiryDate

                        // Calculate the difference in milliseconds
                        val differenceInMillis = expiresDate - currentTime

                        val daysBetween = ceil(differenceInMillis / (1000.0 * 60 * 60 * 24)).toInt()

                        if (it.taken) {
                            BoberiumCard(frontSide = true, expiresDate = daysBetween, onClick = {
                                homeViewModel.openPremiumAlert(
                                    expiryDate = daysBetween
                                )
                            })
                        }

                    }


                }
            }
        })

}


@Composable
fun PremiumAlert(
    homeViewModel: HomeViewModel
) {
    val premiumCardState = homeViewModel.premiumCardState.value
    if (premiumCardState.alertState) {
        AlertDialog(onDismissRequest = { homeViewModel.closePremiumAlert() }, confirmButton = {
            if (premiumCardState.congsText) {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                ), onClick = {
                    homeViewModel.takePremiumCard(
                        premiumCardRequest = PremiumCardRequest(premiumCardName = PremiumCards.BOBERIUM)
                    )
                }) {
                    Text(if (premiumCardState.loading) "Loading..." else "Take it", fontFamily = outfitFamily, fontSize = 16.sp)
                }
            }

        }, text = {
            RotatableCard(homeViewModel)
        }
        )
    }
}


@Composable
fun RotatableCard(
    homeViewModel: HomeViewModel
) {
    val premiumCardState = homeViewModel.premiumCardState.value
    // States to track rotation angles
    var rotationX by remember { mutableStateOf(0f) }
    var rotationY by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()
    // Animatables for smooth rotation animations
    val animatableRotationX = remember { Animatable(0f) }
    val animatableRotationY = remember { Animatable(0f) }

    LaunchedEffect(true) {
        animatableRotationY.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 5000)
        )
    }


    // Gesture detection for drag and rotation
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        if (rotationX > 360 || rotationX < -360) {
                            rotationX = 0f
                        }
                        if (rotationY > 360 || rotationY < -360) {
                            rotationY = 0f
                        }
                        change.consume() // Consume the gesture
                        rotationX += dragAmount.y / 5f
                        rotationY -= dragAmount.x / 5f
                        // Launch a coroutine to update the Animatable values
                        scope.launch {
                            animatableRotationX.snapTo(rotationX)
                            animatableRotationY.snapTo(rotationY)
                        }
                    },
                    onDragEnd = {
                        // Launch a coroutine to animate the rotation back to 0
                        scope.launch {
                            rotationX = 0f
                            rotationY = 0f
                            animatableRotationX.animateTo(0f)
                            animatableRotationY.animateTo(0f)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (premiumCardState.congsText) {
                Text(
                    " \uD83C\uDF8A Congratulations! \uD83C\uDF8A\nYou got a new card!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        lineHeight = 30.sp // Increase this value for more space between lines
                    ),
                    fontWeight = FontWeight.Bold,
                    fontFamily = outfitFamily
                )
                Spacer(Modifier.size(4.dp))
            }

            val frontSide =
                ((animatableRotationX.value < -270 || animatableRotationX.value > -90) &&
                        (animatableRotationX.value < 90 || animatableRotationX.value > 270) &&
                        (animatableRotationY.value < 90 || animatableRotationY.value > 270) &&
                        (animatableRotationY.value < -270 || animatableRotationY.value > -90) ||
                        ((animatableRotationX.value > 90 && animatableRotationX.value < 270) && (animatableRotationY.value > 90 && animatableRotationY.value < 270)) ||
                        ((animatableRotationX.value > 90 && animatableRotationX.value < 270) && (animatableRotationY.value < -90 && animatableRotationY.value > -270)) ||
                        ((animatableRotationX.value < -90 && animatableRotationX.value > -270) && (animatableRotationY.value < -90 && animatableRotationY.value > -270)) ||
                        ((animatableRotationX.value < -90 && animatableRotationX.value > -270) && (animatableRotationY.value > 90 && animatableRotationY.value < 270))
                        )

            BoberiumCard(
                frontSide = frontSide,
                expiresDate = premiumCardState.expiryDate,
                modifier = Modifier.graphicsLayer {
                    // Apply rotation transformations
                    this.rotationX = animatableRotationX.value
                    this.rotationY = animatableRotationY.value
                    cameraDistance = 16 * density // Simulate perspective
                }) {

            }
        }

    }
}
