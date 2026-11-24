package com.zaurh.bober.screen.who_likes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.responses.WhoLikesData
import com.zaurh.bober.ui.theme.PhotoScrim

@Composable
fun WhoLikesUserItem(
    whoLikesData: WhoLikesData,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = whoLikesData.image,
            contentDescription = whoLikesData.username,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.placeholder_image),
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PhotoScrim)
                .padding(start = 12.dp, end = 12.dp, top = 40.dp, bottom = 12.dp)
        ) {
            Text(
                if (whoLikesData.age.isNotBlank()) "${whoLikesData.username}, ${whoLikesData.age}" else whoLikesData.username,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
