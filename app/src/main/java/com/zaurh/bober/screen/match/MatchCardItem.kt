package com.zaurh.bober.screen.match

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.screen.profile.ProfileViewModel
import com.zaurh.bober.util.calculateAge
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchCardItem(
    modifier: Modifier,
    profileData: UserData,
    profileViewModel: ProfileViewModel,
    matchViewModel: MatchViewModel,
    onClick: () -> Unit,
    alphaOffSet: Float
) {

    val imageIndex by profileViewModel.imageIndex

    Box(modifier) {
        val pagerState = rememberPagerState(
            pageCount = { profileData.imageUrl?.size ?: 10 },
            initialPage = imageIndex ?: 0
        )
        val scope = rememberCoroutineScope()
        var screenWidth by remember { mutableIntStateOf(0) }

        LaunchedEffect(key1 = pagerState.currentPage) {
            profileViewModel.updateIndex(pagerState.currentPage)
        }

        Box {

            HorizontalPager(userScrollEnabled = false, state = pagerState, key = {
                profileData.imageUrl?.get(it) ?: ""
            }) { index ->
                AsyncImage(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
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
                        },
                    model = profileData.imageUrl?.get(index),
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
                repeat(profileData.imageUrl?.size ?: 10) { iteration ->
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
            Scrim(Modifier.align(Alignment.BottomCenter))

            Column(Modifier.align(Alignment.BottomStart).padding(bottom = 70.dp)) {

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val name = if (profileData.name != "") profileData.name else profileData.username
                    Text(
                        text = "$name, ${calculateAge(profileData.birthDate ?: "")}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                    IconButton(colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(id = R.color.pink)
                    ), onClick = {
                        onClick()
                    }) {
                        Icon(
                            modifier = Modifier.padding(8.dp),
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "",
                            tint = Color.Black
                        )
                    }
                }

                LaunchedEffect(key1 = profileData) {
                    matchViewModel.decryptLocation(profileData.encryptedLocation ?: "")
                }

                val distance = matchViewModel.distance.value
                val distanceText = if (distance < 2) "Less than kilometer away" else "${distance}km away"

                if (distance != 0){
                    Text(
                        text = distanceText,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Text(
                    text = profileData.jobTitle ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )

            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val likeAlphaSet = when {
                    (alphaOffSet > 10f) && (alphaOffSet < 100f) -> {
                        0.3f
                    }

                    (alphaOffSet > 100f) && (alphaOffSet < 200f) -> {
                        0.7f
                    }

                    (alphaOffSet > 200f) -> {
                        1f
                    }

                    else -> {
                        0f
                    }
                }

                val passAlphaTest = when {
                    (alphaOffSet < -10f) && (alphaOffSet > -100f) -> {
                        0.3f
                    }

                    (alphaOffSet < -100f) && (alphaOffSet > -200f) -> {
                        0.7f
                    }

                    (alphaOffSet < -200f) -> {
                        1f
                    }

                    else -> {
                        0f
                    }
                }

                Icon(
                    modifier = Modifier
                        .size(150.dp)
                        .alpha(likeAlphaSet),
                    painter = painterResource(id = R.drawable.like_ic),
                    contentDescription = "",
                    tint = Color.Green
                )
                Icon(
                    modifier = Modifier
                        .size(150.dp)
                        .alpha(passAlphaTest),
                    painter = painterResource(id = R.drawable.pass_ic),
                    contentDescription = "",
                    tint = Color.Red
                )
            }

        }
    }
}


