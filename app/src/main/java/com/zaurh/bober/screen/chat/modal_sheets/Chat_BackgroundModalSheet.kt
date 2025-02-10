package com.zaurh.bober.screen.chat.modal_sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.data.user.BackgroundColor
import com.zaurh.bober.screen.chat.ChatScreenViewModel
import com.zaurh.bober.ui.theme.outfitFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat_BackgroundModalSheet(
    recipientId: String,
    doodle: Boolean,
    backgroundColor: BackgroundColor,
    chatScreenViewModel: ChatScreenViewModel
) {
    val lazyRowState = rememberLazyListState()

    LaunchedEffect(true) {
        lazyRowState.scrollToItem(BackgroundColor.entries.indexOfFirst { it == backgroundColor })
    }
    if (chatScreenViewModel.backgroundModalSheet.value) {
        ModalBottomSheet(content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 40.dp)
            ) {
                LazyRow(
                    state = lazyRowState
                ) {
                    items(BackgroundColor.entries) {
                        Box(
                            Modifier
                                .padding(4.dp)
                                .height(100.dp)
                                .width(80.dp)
                                .clip(RoundedCornerShape(20))
                                .background(it.color)
                                .border(
                                    1.dp,
                                    color = if (it == BackgroundColor.DEFAULT) Color.Black else Color.Transparent,
                                    shape = RoundedCornerShape(20)
                                )
                                .clickable {
                                    chatScreenViewModel.changeBackgroundColor(
                                        recipientId = recipientId,
                                        backgroundColor = it
                                    )
                                },
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (it == backgroundColor) {
                                Column(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "",
                                        tint = colorResource(
                                            R.color.darkGreen
                                        )
                                    )
                                }
                            }

                        }
                    }
                }
                Spacer(Modifier.size(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Doddle", fontFamily = outfitFamily, fontSize = 16.sp)
                    Switch(colors = SwitchDefaults.colors(
                        checkedBorderColor = Color.White.copy(alpha = 0.3f),
                        uncheckedBorderColor = Color.Gray,
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.secondary,
                        uncheckedTrackColor = Color.White
                    ), checked = doodle, onCheckedChange = {
                        chatScreenViewModel.changeDoodle(
                            recipientId = recipientId,
                            doodle = it
                        )
                    })
                }
            }
        }, onDismissRequest = {
            chatScreenViewModel.closeBackgroundModalSheet(
                recipientId = recipientId,
                backgroundColor = backgroundColor,
                doodle = doodle
            )
        })
    }
}