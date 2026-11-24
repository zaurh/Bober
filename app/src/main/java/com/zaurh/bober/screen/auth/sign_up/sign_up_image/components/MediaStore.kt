package com.zaurh.bober.screen.auth.sign_up.sign_up_image.components

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

fun getMediaFromGallery(context: Context): List<String> {
    val mediaList = mutableListOf<String>()

    // Query for only images
    val projection = arrayOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DATE_MODIFIED
    )
    val selection =
        "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}"
    val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"

    val queryUri = MediaStore.Files.getContentUri("external")
    val cursor = context.contentResolver.query(queryUri, projection, selection, null, sortOrder)

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)

        while (it.moveToNext()) {
            val mediaId = it.getLong(idColumn)
            val mediaUri = Uri.withAppendedPath(queryUri, mediaId.toString())
            // Add only the media URI to the list
            mediaList.add(mediaUri.toString())
        }
    }

    return mediaList
}

@Composable
fun ImageThumbnail(uri: String, onClick: (String) -> Unit) {
    Image(
        painter = rememberAsyncImagePainter(uri),
        contentDescription = "Image",
        modifier = Modifier
            .size(100.dp)
            .clickable {
                onClick(uri)
            }
            .padding(1.dp),
        contentScale = ContentScale.Crop
    )
}
