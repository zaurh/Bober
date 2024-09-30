package com.zaurh.bober.screen.match.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zaurh.bober.R
import com.zaurh.bober.screen.chat.ChatScreenViewModel

@Composable
fun GotMatchAlert(
    chatScreenViewModel: ChatScreenViewModel = hiltViewModel(),
    recipientImage: String
) {
    val userDataState = chatScreenViewModel.userDataState.collectAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bober_match))


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.bober_background),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp), horizontalArrangement = Arrangement.SpaceAround
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                model = recipientImage,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_image),
            )
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                model = userDataState.value?.imageUrl?.firstOrNull() ?: "",
                contentDescription = "",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder_image)
            )
        }

        LottieAnimation(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
            speed = 0.5f,
            composition = composition
        )
    }
}
