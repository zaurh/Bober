package com.zaurh.bober.screen.account.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountScreenItem(
    icon: Int,
    title: String,
    count: String = "",
    onClick: () -> Unit
) {
    Box{
        Column(
            Modifier
                .clip(RoundedCornerShape(20))
                .background(MaterialTheme.colorScheme.background)
                .clickable {
                    onClick()
                }
                .padding(top = 14.dp, bottom = 14.dp, start = 8.dp, end = 8.dp)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = icon),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(text = title, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        }
        if (count != ""){
            Box(modifier = Modifier
                .size(30.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
                ){
                Text(text = count, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

}