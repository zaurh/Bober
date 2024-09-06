package com.zaurh.bober.screen.liked_users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.util.calculateAge

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikedUserItem(
    userData: UserData,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(10)).clickable {
        onClick()
    }) {
        val profilePic = userData.imageUrl?.firstOrNull() ?: ""
        AsyncImage(
            modifier = Modifier.height(200.dp).fillMaxWidth(),
            model = profilePic,
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
            text = "${userData.username}, ${calculateAge(userData.birthDate ?: "")}",
            color = Color.White
        )
    }
}