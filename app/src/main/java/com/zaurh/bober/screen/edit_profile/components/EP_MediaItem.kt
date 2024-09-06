package com.zaurh.bober.screen.edit_profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun EP_MediaItem(
    modifier: Modifier = Modifier,
    image: String,
    isDragging: Boolean,
    onClick: (String) -> Unit
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .height(200.dp)
            .width(160.dp)
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(20))
            .border(
                3.dp,
                color = if (isDragging) MaterialTheme.colorScheme.onSecondary else Color.Transparent,
                shape = RoundedCornerShape(20)
            )
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = image,
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        if (!isDragging) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .alpha(0.7f)
                    .background(Color.Gray)
                    .clickable {
                        onClick(image)
                    }
                )
                Icon(imageVector = Icons.Default.Clear, contentDescription = "", tint = Color.White)
            }
        }
    }

}