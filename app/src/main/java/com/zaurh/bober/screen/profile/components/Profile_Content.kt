package com.zaurh.bober.screen.profile.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.screen.blocked_users.BlockedUsersViewModel
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.ui.theme.outfitFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    profileViewModel: ProfileViewModel,
    chatScreenViewModel: ChatScreenViewModel,
    blockedUsersViewModel: BlockedUsersViewModel,
    profileData: UserData?,
    navController: NavController
) {
    val currentUser = profileViewModel.currentUserDataState.collectAsState()
    val matchListState = chatScreenViewModel.matchListState.value
    val blockListState = blockedUsersViewModel.blockedUsersState.value
//    val profileData = profileViewModel.profileDataState.collectAsState()
    val profileLoading = profileViewModel.profileLoading.collectAsState()
    val imageIndex by profileViewModel.imageIndex
    val context = LocalContext.current

    Profile_ReportAlert(profileViewModel = profileViewModel)
    BlockAlert(
        profileViewModel = profileViewModel,
        navController = navController,
        blockedUsersViewModel = blockedUsersViewModel
    )
    UnMatchAlert(
        profileViewModel = profileViewModel,
        navController = navController,
    )


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(paddingValues)
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp

        if (profileLoading.value) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * 2 / 3)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 30.dp,
                                bottomEnd = 30.dp
                            )
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.LightGray,
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary.copy(0.3f))
                }


            }
        }
        profileData?.let { profile ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 48.dp)
            ) {
                val pagerState = rememberPagerState(
                    pageCount = { profile.imageUrl?.size ?: 10 },
                    initialPage = imageIndex ?: 0
                )
                val scope = rememberCoroutineScope()
                var screenWidth by remember { mutableIntStateOf(0) }


                LaunchedEffect(key1 = pagerState.currentPage) {
                    profileViewModel.updateIndex(pagerState.currentPage)
                }


                Box {
                    HorizontalPager(state = pagerState, key = {
                        profile.imageUrl?.get(it) ?: ""
                    }) { index ->
                        val imageUrl = profile.imageUrl?.get(index)
                        AsyncImage(
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(screenHeight * 2 / 3)
                                .onGloballyPositioned { layoutCoordinates ->
                                    screenWidth = layoutCoordinates.size.width
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures { offset: Offset ->
                                        if (offset.x < screenWidth / 2) {
                                            scope.launch {
                                                pagerState.animateScrollToPage(
                                                    pagerState.currentPage - 1
                                                )
                                            }
                                        } else {
                                            scope.launch {
                                                pagerState.animateScrollToPage(
                                                    pagerState.currentPage + 1
                                                )
                                            }
                                        }
                                    }
                                }
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = 30.dp,
                                        bottomEnd = 30.dp
                                    )
                                ),
                            model = imageUrl,
                            contentDescription = "",
                            placeholder = painterResource(id = R.drawable.placeholder_image)
                        )
                    }

                    Row(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(profile.imageUrl?.size ?: 1) { iteration ->
                            val color =
                                if (pagerState.currentPage == iteration) Color.White else Color.DarkGray
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(color)
                                    .width(40.dp)
                                    .height(5.dp)
                            )
                        }
                    }
                }


                Column(Modifier.padding(16.dp)) {
                    val userId = profile.id ?: ""

                    val inBlockList = blockListState.blockList.any { it.id == userId }


                    val inLikedList =
                        currentUser.value?.likedUsers?.contains(userId) == true

                    val inMatchList =
                        matchListState.matchList.any { it.id == userId }

                    val currentUserItself = currentUser.value?.id == userId

                    val jobTitleExist = profile.jobTitle?.isNotEmpty() == true
                    val heightExist =
                        profile.height?.toString()
                            ?.isNotEmpty() == true && profile.height.toString() != "0"
                    val nameExist = profile.name?.isNotEmpty() == true
                    val aboutExist = profile.aboutMe?.isNotEmpty() == true
                    val languagesExist = profile.languages?.isNotEmpty() == true
                    val interestsExist = profile.interests?.isNotEmpty() == true

                    if (nameExist || jobTitleExist) {
                        Profile_CardItem {
                            if (nameExist) {
                                Text(
                                    text = profile.name ?: "",
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = outfitFamily
                                )
                            }

                            if (jobTitleExist) {
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = profile.jobTitle ?: "",
                                    color = Color.Gray,
                                    fontFamily = outfitFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                    Profile_CardItem {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(id = R.drawable.gender),
                                contentDescription = "",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "${profile.gender?.displayName}",
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = outfitFamily
                            )
                        }
                        if (heightExist) {
                            Spacer(modifier = Modifier.size(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    painter = painterResource(id = R.drawable.height),
                                    contentDescription = "",
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = "${profile.height}cm",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = outfitFamily
                                )
                            }
                        }

                    }
                    if (aboutExist) {
                        Spacer(modifier = Modifier.size(16.dp))
                        Profile_CardItem(modifier = Modifier.height(160.dp)) {
                            Text(
                                text = "• About",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = outfitFamily
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {

                                Text(
                                    text = profile.aboutMe ?: "",
                                    color = Color.Gray,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = outfitFamily
                                )
                            }
                        }
                    }
                    if (languagesExist) {
                        Spacer(modifier = Modifier.size(16.dp))
                        Profile_CardItem {
                            val languages = profile.languages

                            Text(
                                text = "• Languages",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = outfitFamily
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                languages?.forEachIndexed { index, language ->
                                    Text(
                                        text = language.displayName,
                                        color = Color.Gray,
                                        fontFamily = outfitFamily
                                    )
                                    if (index < languages.size - 1) {
                                        Text(
                                            text = ",",
                                            modifier = Modifier.padding(end = 4.dp),
                                            color = Color.Gray,
                                            fontFamily = outfitFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (interestsExist) {
                        Spacer(modifier = Modifier.size(16.dp))
                        Profile_CardItem {
                            val interests = profile.interests

                            Text(
                                text = "• Interests",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = outfitFamily
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                interests?.forEachIndexed { index, interest ->
                                    Text(
                                        text = interest.displayName,
                                        color = Color.Gray,
                                        fontFamily = outfitFamily
                                    )
                                    if (index < interests.size - 1) {
                                        Text(
                                            text = ",",
                                            modifier = Modifier.padding(end = 4.dp),
                                            color = Color.Gray,
                                            fontFamily = outfitFamily
                                        )
                                    }
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.size(32.dp))

                    val nameText =
                        if (profile.name != "") "${profile.name}" else "${profile.username}"


                    if (inLikedList && !inMatchList && !currentUserItself) {
                        ProfileScreenButtonItem(
                            title = "Unlike $nameText",
                            isLoading = profileViewModel.unLikeLoading.value
                        ) {
                            profileViewModel.unLike(
                                recipientId = profile.id ?: ""
                            ) {
                                navController.popBackStack()
                            }
                        }
                    } else if (inMatchList && !currentUserItself) {
                        ProfileScreenButtonItem(
                            title = "Unmatch $nameText",
                            isLoading = profileViewModel.unMatchLoading.value
                        ) {
                            profileViewModel.onUnMatchStateChange()
                        }
                    }
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "https://zaurh.github.io/bobersite/?username=${profile.username}"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)


                    ProfileScreenButtonItem(title = "Share $nameText's profile") {
                        context.startActivity(shareIntent)
                    }
                    if (!currentUserItself) {
                        ProfileScreenButtonItem(title = "Report $nameText") {
                            profileViewModel.onReportStateChange()
                        }
                    }

                    if (!currentUserItself) {
                        ProfileScreenButtonItem(
                            title = if (inBlockList) "Unblock $nameText" else "Block $nameText"
                        ) {
                            if (inBlockList) {
                                blockedUsersViewModel.unBlockUser(
                                    recipientId = profile.id ?: ""
                                )
                            } else {
                                blockedUsersViewModel.onBlockStateChange()
                            }

                        }
                    }


                }


            }


            AnimatedVisibility(
                visible =
                (profile.id !in blockListState.blockList.map { it.id }) &&
                        currentUser.value?.likedUsers?.contains(profile.id) == false &&
                        currentUser.value?.id != profile.id,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it })
            )
            {
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
                        gradient = Brush.sweepGradient(
                            listOf(
                                colorResource(R.color.likeColor1),
                                Color.Red
                            )
                        ),
                        onCardClick = {
                            profileViewModel.pass(recipientId = profile.id ?: "", onSuccess = {
                                navController.popBackStack()
                            })
                        }
                    )
                    AnimatedBorderCard(
                        imageVector = Icons.Default.Favorite,
                        imageColor = colorResource(R.color.green),
                        gradient = Brush.sweepGradient(
                            listOf(
                                colorResource(R.color.likeColor2),
                                colorResource(R.color.green)
                            )
                        ),
                        onCardClick = {
                            profileViewModel.like(
                                recipientId = profile.id ?: "",
                                onSuccess = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    )
                }

            }


        }


    }

}


@Composable
fun AnimatedBorderCard(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    borderWidth: Dp = 2.dp,
    gradient: Brush = Brush.sweepGradient(
        listOf(
            colorResource(R.color.likeColor1),
            colorResource(R.color.green)
        )
    ),
    animationDuration: Int = 10000,
    onCardClick: () -> Unit = {},
    imageVector: ImageVector,
    imageColor: Color,
    imageSize: Dp = 70.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Color Animation")
    val degrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Infinite Colors"
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .clickable { onCardClick() },
        shape = shape
    ) {
        Surface(
            modifier = Modifier
                .padding(borderWidth)
                .drawWithContent {
                    rotate(degrees = degrees) {
                        drawCircle(
                            brush = gradient,
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    drawContent()
                },
            color = Color.White,
            shape = shape
        ) {
            Icon(
                modifier = Modifier
                    .size(imageSize)
                    .padding(8.dp),
                imageVector = imageVector,
                contentDescription = "",
                tint = imageColor
            )
        }
    }
}