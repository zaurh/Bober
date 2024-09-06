package com.zaurh.bober.screen.chat.components

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zaurh.bober.R

@Composable
fun AnimatedGif(
    context: Context,
    gifUrl: String,
    size: Int,
    circularSize: Int,
    clipPercentage: Int,
    onClick: (String) -> Unit
) {
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    SubcomposeAsyncImage(
        imageLoader = imageLoader,
        model = gifUrl,
        loading = {
            Box(modifier = Modifier.size(size.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(circularSize.dp), color = colorResource(
                        id = R.color.backgroundBottom
                    )
                )
            }

        },
        contentDescription = "",
        modifier = Modifier
            .height(size.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(clipPercentage))
            .clickable {
                onClick(gifUrl)
            }
    )

}