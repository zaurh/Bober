package com.zaurh.bober.screen.match.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zaurh.bober.R
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.ui.components.Avatar
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.BoberButtonStyle
import com.zaurh.bober.ui.components.GradientBackground
import com.zaurh.bober.ui.theme.BrandFont

/** Full-screen celebration after a like turns into a match. */
@Composable
fun GotMatchAlert(
    recipientImage: String,
    recipientName: String,
    onSendMessage: () -> Unit,
    onDismiss: () -> Unit,
    chatScreenViewModel: ChatScreenViewModel = hiltViewModel(),
) {
    val me = chatScreenViewModel.userDataState.collectAsState().value
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bober_match))
    BackHandler(onBack = onDismiss)

    GradientBackground {
        LottieAnimation(composition, modifier = Modifier.align(Alignment.Center).size(320.dp), speed = 0.7f)
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("It's a match!", color = Color.White, style = TextStyle(fontFamily = BrandFont, fontSize = 44.sp))
            Spacer(Modifier.height(8.dp))
            Text(
                if (recipientName.isNotEmpty()) "You and $recipientName like each other." else "You like each other.",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))
            Box {
                Row {
                    Avatar(
                        me?.imageUrl?.firstOrNull(), 132.dp,
                        Modifier
                            .rotate(-8f)
                            .border(4.dp, Color.White, CircleShape)
                    )
                    Avatar(
                        recipientImage, 132.dp,
                        Modifier
                            .offset(x = (-24).dp)
                            .rotate(8f)
                            .border(4.dp, Color.White, CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(56.dp))
            BoberButton("Send a message", onClick = onSendMessage, style = BoberButtonStyle.OnGradient)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text("Keep swiping", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
