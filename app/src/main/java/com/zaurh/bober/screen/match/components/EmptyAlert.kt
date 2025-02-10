package com.zaurh.bober.screen.match.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zaurh.bober.R
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun EmptyAlert(
    loading: Boolean,
    empty: Boolean
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_animation))

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        else {
            if (empty){
                LottieAnimation(
                    modifier = Modifier
                        .size(200.dp),
                    speed = 0.5f,
                    composition = composition,
                    iterations = 100
                )
                Text("No bobers found.\nCome back later...", fontFamily = outfitFamily)
            }


        }
    }



}
