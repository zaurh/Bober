package com.zaurh.bober.screen.chat.components

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

/** One GIF-capable image loader for the whole app; building one per GIF is expensive. */
private object GifLoader {
    @Volatile private var loader: ImageLoader? = null

    fun get(context: Context): ImageLoader = loader ?: synchronized(this) {
        loader ?: ImageLoader.Builder(context.applicationContext)
            .components {
                if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
            }
            .build()
            .also { loader = it }
    }
}

@Composable
fun AnimatedGif(
    context: Context,
    gifUrl: String,
    size: Int,
    modifier: Modifier = Modifier,
    onClick: ((String) -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        imageLoader = GifLoader.get(context),
        model = gifUrl,
        contentDescription = "GIF",
        contentScale = ContentScale.Crop,
        loading = {
            Box(Modifier.size(size.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
            }
        },
        modifier = modifier
            .height(size.dp)
            .widthIn(min = size.dp / 2)
            .clip(MaterialTheme.shapes.medium)
            .then(if (onClick != null) Modifier.clickable { onClick(gifUrl) } else Modifier)
    )
}
