package com.zaurh.bober.screen.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.home.HomeViewModel

@Composable
fun Home_MatchUserItem(
    userData: UserData,
    new: Boolean,
    navController: NavController,
    homeViewModel: HomeViewModel,
) {

    Box {
        val profilePic = userData.imageUrl?.firstOrNull() ?: ""

            AsyncImage(
                modifier = Modifier
                    .height(150.dp)
                    .width(100.dp)
                    .padding(8.dp)
                    .clip(
                        RoundedCornerShape(20)
                    )
                    .clickable {
                        navController.navigate(Screen.ChatScreen.createRoute(username = userData.username))
                        homeViewModel.newMatchToOld(userData.id ?: "")
                    },
                model = profilePic,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        if (new) {
            Icon(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.online_ic),
                contentDescription = "",
                tint = colorResource(id = R.color.backgroundBottom)
            )
        }
    }

}