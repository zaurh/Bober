package com.zaurh.bober.screen.match.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zaurh.bober.R

@Composable
fun EmptyAlert() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_animation))

    Box(modifier = Modifier.fillMaxSize()){
        LottieAnimation(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
            speed = 0.5f,
            composition = composition,
            iterations = 100
        )
    }


}
