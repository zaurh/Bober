package com.zaurh.bober.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zaurh.bober.R

@Composable
fun EmptyChatAlert() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bober_empty_chat))

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You don't have any match yet.\nSwipe right to see bobers!", textAlign = TextAlign.Center)
        LottieAnimation(
            modifier = Modifier
                .size(200.dp),
            speed = 0.5f,
            composition = composition,
            iterations = 100
        )
    }


}