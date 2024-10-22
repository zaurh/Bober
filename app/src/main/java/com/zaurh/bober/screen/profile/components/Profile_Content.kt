package com.zaurh.bober.screen.profile.components

import android.content.Intent
import android.util.Log
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
import com.zaurh.bober.screen.profile.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    profileViewModel: ProfileViewModel,
    user: UserData,
    navController: NavController
) {
    val currentUser = profileViewModel.currentUserDataState.collectAsState()
    val profileData = profileViewModel.profileDataState.collectAsState()
    val imageIndex by profileViewModel.imageIndex
    val context = LocalContext.current

    Profile_ReportAlert(profileViewModel = profileViewModel)
    BlockAlert(profileViewModel = profileViewModel, navController = navController)
    UnMatchAlert(profileViewModel = profileViewModel, navController = navController)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 48.dp)
        ) {
            val pagerState = rememberPagerState(
                pageCount = { profileData.value?.imageUrl?.size ?: 10 },
                initialPage = imageIndex ?: 0
            )
            val scope = rememberCoroutineScope()
            var screenWidth by remember { mutableIntStateOf(0) }

            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp

            LaunchedEffect(key1 = pagerState.currentPage) {
                profileViewModel.updateIndex(pagerState.currentPage)
            }


            Box {
                HorizontalPager(state = pagerState, key = {
                    profileData.value?.imageUrl?.get(it) ?: ""
                }) { index ->
                    val imageUrl = profileData.value?.imageUrl?.get(index)
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
                            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)),
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
                    repeat(profileData.value?.imageUrl?.size ?: 1) { iteration ->
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
                val userId = profileData.value?.id ?: ""

                val inBlockList =
                    currentUser.value?.blockList?.contains(userId) == true
                val inLikedList =
                    currentUser.value?.likedUsers?.contains(userId) == true

                val inMatchList =
                    currentUser.value?.matchList?.any { it.id == userId } == true

                val currentUserItself = currentUser.value?.id == userId

                val jobTitleExist = user.jobTitle?.isNotEmpty() == true
                val heightExist =
                    user.height?.toString()?.isNotEmpty() == true && user.height.toString() != "0"
                val nameExist = user.name?.isNotEmpty() == true
                val aboutExist = user.aboutMe?.isNotEmpty() == true
                val languagesExist = user.languages?.isNotEmpty() == true
                val interestsExist = user.interests?.isNotEmpty() == true

                if (nameExist || jobTitleExist) {
                    Profile_CardItem {
                        if (nameExist) {
                            Text(
                                text = user.name ?: "",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (jobTitleExist) {
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = user.jobTitle ?: "",
                                color = Color.Gray
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
                            text = "${user.gender?.displayName}",
                            color = MaterialTheme.colorScheme.primary
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
                                text = "${user.height}cm",
                                color = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {

                            Text(
                                text = user.aboutMe ?: "",
                                color = Color.Gray,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                if (languagesExist) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Profile_CardItem {
                        val languages = user.languages

                        Text(
                            text = "• Languages",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            languages?.forEachIndexed { index, language ->
                                Text(
                                    text = language.displayName,
                                    color = Color.Gray
                                )
                                if (index < languages.size - 1) {
                                    Text(
                                        text = ",",
                                        modifier = Modifier.padding(end = 4.dp),
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                if (interestsExist) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Profile_CardItem {
                        val interests = user.interests

                        Text(
                            text = "• Interests",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            interests?.forEachIndexed { index, interest ->
                                Text(
                                    text = interest.displayName,
                                    color = Color.Gray
                                )
                                if (index < interests.size - 1) {
                                    Text(
                                        text = ",",
                                        modifier = Modifier.padding(end = 4.dp),
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.size(32.dp))

                val nameText = if (user.name != "") "${user.name}" else "${user.username}"

                Log.d("fsdljdfsljksd", "$currentUser")

                if (inLikedList && !inMatchList && !currentUserItself) {
                    ProfileScreenButtonItem(
                        title = "Unlike $nameText",
                        isLoading = profileViewModel.unLikeLoading.value
                    ) {
                        profileViewModel.unLike(
                            recipientId = profileData.value?.id ?: ""
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
                        "https://zaurh.github.io/bobersite/?username=${user.username}"
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
                            profileViewModel.unblock(
                                recipientId = profileData.value?.id ?: ""
                            ) {

                            }
                        } else {
                            profileViewModel.onBlockStateChange()
                        }

                    }
                }


            }

        }

        AnimatedVisibility(
            visible = currentUser.value?.blockList?.contains(profileData.value?.id) == false &&
                    currentUser.value?.likedUsers?.contains(profileData.value?.id) == false &&
            currentUser.value?.passedUsers?.any { it.userId == profileData.value?.id } == false &&
            currentUser.value?.id != profileData.value?.id,
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
                        profileViewModel.pass(recipientId = user.id ?: "", onSuccess = {
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
                            recipientId = user.id ?: "",
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