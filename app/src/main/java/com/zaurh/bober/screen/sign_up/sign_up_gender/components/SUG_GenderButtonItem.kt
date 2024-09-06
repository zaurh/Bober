package com.zaurh.bober.screen.sign_up.sign_up_gender.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zaurh.bober.R

@Composable
fun SUG_GenderButtonItem(
    onClick: () -> Unit,
    selected: Boolean,
    icon: Int,
    text: String
) {
    Box(modifier = Modifier
        .size(150.dp)
        .clip(CircleShape)
        .background(if (selected) Color.White else colorResource(id = R.color.fadedWhite))
        .clickable {
            onClick()
        }
    ) {
        Icon(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center),
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = colorResource(id = if (selected) R.color.backgroundTop else R.color.white)
        )
    }
    Spacer(modifier = Modifier.size(8.dp))
    Text(text = text, color = Color.White)
}